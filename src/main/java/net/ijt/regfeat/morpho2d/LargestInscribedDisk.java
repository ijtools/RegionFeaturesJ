/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import ij.measure.Calibration;
import ij.measure.ResultsTable;
import ij.process.ImageProcessor;
import inra.ijpb.geometry.Circle2D;
import inra.ijpb.measure.region2d.LargestInscribedCircle;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;

/**
 * 
 */
public class LargestInscribedDisk extends Feature
{
    @Override
    public Circle2D[] compute(RegionFeatures results)
    {
        ImageProcessor labelMap = results.labelMap.getProcessor();
        Calibration calib = results.labelMap.getCalibration();
        return LargestInscribedCircle.largestInscribedCircles(labelMap, results.labels, calib);
    }
    
    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof Circle2D[])
        {
            Circle2D[] array = (Circle2D[]) obj;
            for (int r = 0; r < array.length; r++)
            {
                Circle2D circle = array[r];
                // coordinates of circle center
                table.setValue("InscrCircle.Center.X", r, circle.getCenter().getX());
                table.setValue("InscrCircle.Center.Y", r, circle.getCenter().getY());
                
                // circle radius
                table.setValue("InscrCircle.Radius", r, circle.getRadius());
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of Circle2D");
        }
    }
}
