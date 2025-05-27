/**
 * 
 */
package net.ijt.regfeat.morpho3d;

import ij.measure.ResultsTable;
import inra.ijpb.geometry.Box3D;
import inra.ijpb.measure.region3d.BoundingBox3D;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.RegionTabularFeature;

/**
 * Computes the bounds of each 3D region within a label map. Mostly a wrapper
 * for the <code>inra.ijpb.measure.region3d.BoundingBox3D</code> class from
 * MorphoLibJ.
 */
public class Bounds3D implements RegionTabularFeature
{
    public static final String[] colNames = new String[] {
            "Bounds3D_XMin", "Bounds3D_XMax", 
            "Bounds3D_YMin", "Bounds3D_YMax", 
            "Bounds3D_ZMin", "Bounds3D_ZMax"};
    
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
