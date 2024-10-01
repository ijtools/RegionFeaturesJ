/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import ij.measure.ResultsTable;
import inra.ijpb.geometry.Box2D;
import inra.ijpb.measure.region2d.BoundingBox;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;

/**
 * 
 */
public class Bounds extends Feature
{
    @Override
    public Object compute(RegionFeatures data)
    {
        return BoundingBox.boundingBoxes(data.labelMap.getProcessor(), data.labels, data.labelMap.getCalibration());
    }

    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        Box2D[] boxes = (Box2D[]) data.results.get(this.getClass());
        
        for (int i = 0; i < boxes.length; i++)
        {
            // current box
            Box2D box = boxes[i];
            
            // coordinates of centroid
            table.setValue("Bounds2D.XMin", i, box.getXMin());
            table.setValue("Bounds2D.XMax", i, box.getXMax());
            table.setValue("Bounds2D.YMin", i, box.getYMin());
            table.setValue("Bounds2D.YMax", i, box.getYMax());
        }
    }
}
