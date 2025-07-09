/**
 * 
 */
package net.ijt.regfeat.morpho3d;

import java.util.HashMap;

import ij.ImageStack;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import inra.ijpb.geometry.Point3D;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.RegionTabularFeature;
import net.ijt.regfeat.RegionFeatures;

/**
 * Computes centroid of each regions within the label map.
 */
public class Centroid3D implements RegionTabularFeature
{
    /**
     * The names of the columns of the resulting table.
     */
    public static final String[] colNames = new String[] {"Centroid_X", "Centroid_Y", "Centroid_Z"};
    
    /**
     * Default empty constructor.
     */
    public Centroid3D()
    {
    }
    
    @Override
    public Point3D[] compute(RegionFeatures data)
    {
        ImageStack image = data.labelMap.getStack();
        
        // size of image
        int sizeX = image.getWidth();
        int sizeY = image.getHeight();
        int sizeZ = image.getSize();

        // retrieve spatial calibration
        double sx = 1, sy = 1, sz = 1;
        double ox = 0, oy = 0, oz = 0;
        Calibration calib = data.labelMap.getCalibration();
        if (calib != null)
        {
            sx = calib.pixelWidth;
            sy = calib.pixelHeight;
            sz = calib.pixelDepth;
            ox = calib.xOrigin;
            oy = calib.yOrigin;
            oz = calib.zOrigin;
        }
        
        // create associative array to know index of each label
        HashMap<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(data.labels);

        // allocate memory for result
        int nLabels = data.labels.length;
        int[] counts = new int[nLabels];
        double[] cx = new double[nLabels];
        double[] cy = new double[nLabels];
        double[] cz = new double[nLabels];

//        fireStatusChanged(this, "Compute centroids");
        // compute centroid of each region
        for (int z = 0; z < sizeZ; z++) 
        {
            for (int y = 0; y < sizeY; y++) 
            {
                for (int x = 0; x < sizeX; x++)
                {
                    int label = (int) image.getVoxel(x, y, z);
                    if (label == 0)
                        continue;

                    // do not process labels that are not in the input list 
                    if (!labelIndices.containsKey(label))
                        continue;

                    int index = labelIndices.get(label);
                    cx[index] += x * sx;
                    cy[index] += y * sy;
                    cz[index] += z * sz;
                    counts[index]++;
                }
            }
        }
//        this.fireProgressChanged(this, 1, 1);

        // normalize by number of voxels in each region
        for (int i = 0; i < nLabels; i++)
        {
            if (counts[i] == 0) continue;

            cx[i] /= counts[i];
            cy[i] /= counts[i];
            cz[i] /= counts[i];
        }

        // add coordinates of origin pixel (IJ coordinate system)
        for (int i = 0; i < nLabels; i++)
        {
            if (counts[i] == 0) continue;

            cx[i] += .5 * sx + ox;
            cy[i] += .5 * sy + oy;
            cz[i] += .5 * sz + oz;
        }

        // create array of Point3D
        Point3D[] points = new Point3D[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
            points[i] = new Point3D(cx[i], cy[i], cz[i]);
        }

        return points;
    }
    
    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof Point3D[])
        {
            Point3D[] array = (Point3D[]) obj;
            for (int r = 0; r < array.length; r++)
            {
                Point3D point = array[r];
                table.setValue(colNames[0], r, point.getX());
                table.setValue(colNames[1], r, point.getY());
                table.setValue(colNames[2], r, point.getZ());
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of Point3D");
        }
    }
    
    @Override
    public String[] columnUnitNames(RegionFeatures data)
    {
        String unit = data.labelMap.getCalibration().getUnit();
        return new String[] {unit, unit, unit};
    }
}
