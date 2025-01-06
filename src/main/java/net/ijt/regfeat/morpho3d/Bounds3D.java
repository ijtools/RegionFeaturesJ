/**
 * 
 */
package net.ijt.regfeat.morpho3d;

import ij.measure.ResultsTable;
import inra.ijpb.geometry.Box3D;
import inra.ijpb.measure.region3d.BoundingBox3D;
import net.ijt.regfeat.RegionFeature;
import net.ijt.regfeat.RegionFeatures;

/**
 * Computes the bounds of each 3D region within a label map. Mostly a wrapper
 * for the <code>inra.ijpb.measure.region3d.BoundingBox3D</code> class from
 * MorphoLibJ.
 */
public class Bounds3D implements RegionFeature
{
    @Override
    public Box3D[] compute(RegionFeatures data)
    {
        return BoundingBox3D.boundingBoxes(data.labelMap.getStack(), data.labels, data.labelMap.getCalibration());
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
            table.setValue("Bounds3D_XMin", i, box.getXMin());
            table.setValue("Bounds3D_XMax", i, box.getXMax());
            table.setValue("Bounds3D_YMin", i, box.getYMin());
            table.setValue("Bounds3D_YMax", i, box.getYMax());
            table.setValue("Bounds3D_ZMin", i, box.getZMin());
            table.setValue("Bounds3D_ZMax", i, box.getZMax());
        }
    }
}
