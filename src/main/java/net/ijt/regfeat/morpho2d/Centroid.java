/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import java.awt.geom.Point2D;
import java.util.HashMap;

import ij.measure.Calibration;
import ij.measure.ResultsTable;
import ij.process.ImageProcessor;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.RegionTabularFeature;
import net.ijt.regfeat.RegionFeatures;

/**
 * Computes centroid of each region within the label map.
 */
public class Centroid implements RegionTabularFeature
{
    public static final String[] colNames = new String[] {"Centroid_X", "Centroid_Y"};
    
    @Override
    public Point2D[] compute(RegionFeatures data)
    {
        ImageProcessor labelMap = data.labelMap.getProcessor();
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
        HashMap<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(data.labels);

        // allocate memory for result
        int nLabels = data.labels.length;
        int[] counts = new int[nLabels];
        double[] cx = new double[nLabels];
        double[] cy = new double[nLabels];

//        fireStatusChanged(this, "Compute centroids");
        // iterate over pixels, and updates centroid of the region
        for (int y = 0; y < sizeY; y++) 
        {
            for (int x = 0; x < sizeX; x++)
            {
                int label = (int) labelMap.getf(x, y);
                if (label == 0)
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
            if (counts[i] == 0) continue;

            cx[i] /= counts[i];
            cy[i] /= counts[i];
        }

        // add coordinates of origin pixel (IJ coordinate system)
        for (int i = 0; i < nLabels; i++)
        {
            if (counts[i] == 0) continue;

            cx[i] += .5 * sx + ox;
            cy[i] += .5 * sy + oy;
        }

        // create array of Point3D
        Point2D[] points = new Point2D[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
            points[i] = new Point2D.Double(cx[i], cy[i]);
        }

        return points;
    }
    
    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof Point2D[])
        {
            Point2D[] array = (Point2D[]) obj;
            for (int r = 0; r < array.length; r++)
            {
                Point2D point = array[r];
                table.setValue(colNames[0], r, point.getX());
                table.setValue(colNames[1], r, point.getY());
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of Point2D");
        }
    }
    
    @Override
    public String[] columnUnitNames(RegionFeatures data)
    {
        String unit = data.labelMap.getCalibration().getUnit();
        return new String[] {unit, unit};
    }
}
