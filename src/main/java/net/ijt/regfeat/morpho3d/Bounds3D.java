/**
 * 
 */
package net.ijt.regfeat.morpho3d;

import java.util.HashMap;

import ij.ImageStack;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import inra.ijpb.algo.AlgoStub;
import inra.ijpb.geometry.Box3D;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.RegionTabularFeature;

/**
 * Computes the bounds of each 3D region within a label map. Mostly a wrapper
 * for the <code>inra.ijpb.measure.region3d.BoundingBox3D</code> class from
 * MorphoLibJ.
 * 
 * @see net.ijt.regfeat.morpho2d.Bounds
 * @see inra.ijpb.measure.region3d.BoundingBox3D
 */
public class Bounds3D extends AlgoStub implements RegionTabularFeature
{
    public static final String[] colNames = new String[] {
            "Bounds3D_XMin", "Bounds3D_XMax", 
            "Bounds3D_YMin", "Bounds3D_YMax", 
            "Bounds3D_ZMin", "Bounds3D_ZMax"};
    
    @Override
    public Box3D[] compute(RegionFeatures data)
    {
        // retrieve label map and list of labels
        ImageStack labelMap = data.labelMap.getStack();
        int[] labels = data.labels;
        Calibration calib = data.labelMap.getCalibration();
        
        // size of image
        int sizeX = labelMap.getWidth();
        int sizeY = labelMap.getHeight();
        int sizeZ = labelMap.getSize();

        // Extract spatial calibration
        double sx = 1, sy = 1, sz = 1;
        double ox = 0, oy = 0, oz = 0;
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
        HashMap<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(labels);

        // allocate memory for result
        int nLabels = labels.length;
        double[] xmin = new double[nLabels];
        double[] xmax = new double[nLabels];
        double[] ymin = new double[nLabels];
        double[] ymax = new double[nLabels];
        double[] zmin = new double[nLabels];
        double[] zmax = new double[nLabels];
        
        // initialize to extreme values
        for (int i = 0; i < nLabels; i++)
        {
            xmin[i] = Double.POSITIVE_INFINITY;
            xmax[i] = Double.NEGATIVE_INFINITY;
            ymin[i] = Double.POSITIVE_INFINITY;
            ymax[i] = Double.NEGATIVE_INFINITY;
            zmin[i] = Double.POSITIVE_INFINITY;
            zmax[i] = Double.NEGATIVE_INFINITY;
        }

        // compute extreme coordinates of each region
        fireStatusChanged(this, "Compute bounds");
        for (int z = 0; z < sizeZ; z++) 
        {
            fireProgressChanged(this, z, sizeZ);
            
            for (int y = 0; y < sizeY; y++) 
            {
                for (int x = 0; x < sizeX; x++)
                {
                    int label = (int) labelMap.getVoxel(x, y, z);
                    if (label == 0)
                        continue;

                    // do not process labels that are not in the input list 
                    if (!labelIndices.containsKey(label))
                        continue;
                    int index = labelIndices.get(label);

                    xmin[index] = Math.min(xmin[index], x);
                    xmax[index] = Math.max(xmax[index], x + 1);
                    ymin[index] = Math.min(ymin[index], y);
                    ymax[index] = Math.max(ymax[index], y + 1);
                    zmin[index] = Math.min(zmin[index], z);
                    zmax[index] = Math.max(zmax[index], z + 1);
                }
            }
        }
        
        // create bounding box instances
        Box3D[] boxes = new Box3D[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
            boxes[i] = new Box3D(
                    xmin[i] * sx + ox, xmax[i] * sx + ox,
                    ymin[i] * sy + oy, ymax[i] * sy + oy, 
                    zmin[i] * sz + oz, zmax[i] * sz + oz);
        }
        return boxes;
    }

    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        Box3D[] boxes = (Box3D[]) data.results.get(this.getClass());
        for (int i = 0; i < boxes.length; i++)
        {
            // current box
            Box3D box = boxes[i];
            
            // coordinates of centroid
            table.setValue(colNames[0], i, box.getXMin());
            table.setValue(colNames[1], i, box.getXMax());
            table.setValue(colNames[2], i, box.getYMin());
            table.setValue(colNames[3], i, box.getYMax());
            table.setValue(colNames[4], i, box.getZMin());
            table.setValue(colNames[5], i, box.getZMax());
        }
    }
    @Override
    public String[] columnUnitNames(RegionFeatures data)
    {
        String unit = data.labelMap.getCalibration().getUnit();
        return new String[] {unit, unit, unit, unit, unit, unit};
    }

}
