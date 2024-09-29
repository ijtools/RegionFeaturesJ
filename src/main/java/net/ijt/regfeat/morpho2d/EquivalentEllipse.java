/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import static java.lang.Math.sqrt;

import java.awt.geom.Point2D;
import java.util.HashMap;

import ij.measure.Calibration;
import ij.measure.ResultsTable;
import ij.process.ImageProcessor;
import inra.ijpb.geometry.Ellipse;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;

/**
 * Compute equivalent ellipse of regions stored within label map.
 * 
 * The equivalent ellipse of a region is computed such that is has same second
 * order moments as the region. The code is adapted from that of MorphoLibJ.
 * 
 * @see inra.ijpb.measure.region2d.EquivalentEllipse
 */
public class EquivalentEllipse extends Feature
{

    @Override
    public Ellipse[] compute(RegionFeatures data)
    {
        // retrieve label map and list of labels
        ImageProcessor labelMap = data.labelMap.getProcessor();
        int[] labels = data.labels;
        Calibration calib = data.labelMap.getCalibration();
        
        // size of image
        int sizeX = labelMap.getWidth();
        int sizeY = labelMap.getHeight();

        // Extract spatial calibration
        double sx = 1, sy = 1;
        double ox = 0, oy = 0;
        if (calib != null)
        {
            sx = calib.pixelWidth;
            sy = calib.pixelHeight;
            ox = calib.xOrigin;
            oy = calib.yOrigin;
        }
        
        // create associative array to know index of each label
        HashMap<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(labels);

        // allocate memory for result
        int nLabels = labels.length;
        int[] counts = new int[nLabels];
        double[] cx = new double[nLabels];
        double[] cy = new double[nLabels];
        double[] Ixx = new double[nLabels];
        double[] Iyy = new double[nLabels];
        double[] Ixy = new double[nLabels];

//        fireStatusChanged(this, "Compute centroids");
        // compute centroid of each region
        for (int y = 0; y < sizeY; y++) 
        {
            for (int x = 0; x < sizeX; x++)
            {
                int label = (int) labelMap.getf(x, y);
                if (label == 0)
                    continue;

                // do not process labels that are not in the input list 
                if (!labelIndices.containsKey(label))
                    continue;

                int index = labelIndices.get(label);
                cx[index] += x * sx;
                cy[index] += y * sy;
                counts[index]++;
            }
        }

        // normalize by number of pixels in each region
        for (int i = 0; i < nLabels; i++)
        {
            cx[i] = cx[i] / counts[i];
            cy[i] = cy[i] / counts[i];
        }

        // compute centered inertia matrix of each label
//        fireStatusChanged(this, "Compute Inertia Matrices");
        for (int y = 0; y < sizeY; y++) 
        {
            for (int x = 0; x < sizeX; x++)
            {
                int label = (int) labelMap.getf(x, y);
                if (label == 0)
                    continue;

                int index = labelIndices.get(label);
                double x2 = x * sx - cx[index];
                double y2 = y * sy - cy[index];
                Ixx[index] += x2 * x2;
                Ixy[index] += x2 * y2;
                Iyy[index] += y2 * y2;
            }
        }

        // normalize by number of pixels in each region
        for (int i = 0; i < nLabels; i++)
        {
            Ixx[i] = Ixx[i] / counts[i] + sx / 12.0;
            Ixy[i] = Ixy[i] / counts[i];
            Iyy[i] = Iyy[i] / counts[i] + sy / 12.0;
        }

        // Create array of result
        Ellipse[] ellipses = new Ellipse[nLabels];
        
        // compute ellipse parameters for each region
//        fireStatusChanged(this, "Compute Ellipses");
        final double sqrt2 = sqrt(2);
        for (int i = 0; i < nLabels; i++) 
        {
            double xx = Ixx[i];
            double xy = Ixy[i];
            double yy = Iyy[i];

            // compute ellipse semi-axes lengths
            double common = sqrt((xx - yy) * (xx - yy) + 4 * xy * xy);
            double ra = sqrt2 * sqrt(xx + yy + common);
            double rb = sqrt2 * sqrt(xx + yy - common);

            // compute ellipse angle and convert into degrees
            double theta = Math.toDegrees(Math.atan2(2 * xy, xx - yy) / 2);

            Point2D center = new Point2D.Double(cx[i] + sx / 2 + ox, cy[i] + sy / 2 + oy);
            ellipses[i] = new Ellipse(center, ra, rb, theta);
        }

        return ellipses;
    }

    @Override
    public void populateTable(ResultsTable table, Object obj)
    {
        if (obj instanceof Ellipse[])
        {
            Ellipse[] array = (Ellipse[]) obj;
            for (int r = 0; r < array.length; r++)
            {
                // current ellipse
                Ellipse ellipse = array[r];
                
                // coordinates of centroid
                Point2D center = ellipse.center();
                table.setValue("Ellipse.Center.X", r, center.getX());
                table.setValue("Ellipse.Center.Y", r, center.getY());
                
                // ellipse size
                table.setValue("Ellipse.Radius1", r, ellipse.radius1());
                table.setValue("Ellipse.Radius2", r, ellipse.radius2());
        
                // ellipse orientation (degrees)
                table.setValue("Ellipse.Orientation", r, ellipse.orientation());
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of Ellipse");
        }
    }

}
