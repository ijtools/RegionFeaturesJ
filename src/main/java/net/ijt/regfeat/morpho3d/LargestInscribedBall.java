/**
 * 
 */
package net.ijt.regfeat.morpho3d;

import ij.ImageStack;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import inra.ijpb.geometry.Sphere;
import net.ijt.regfeat.RegionFeature;
import net.ijt.regfeat.RegionFeatures;

/**
 * Computes the largest inscribed ball within 3D regions of a label map.
 */
public class LargestInscribedBall implements RegionFeature
{
    @Override
    public Sphere[] compute(RegionFeatures results)
    {
        ImageStack labelMap = results.labelMap.getStack();
        Calibration calib = results.labelMap.getCalibration();
        return new inra.ijpb.measure.region3d.LargestInscribedBall().analyzeRegions(labelMap, results.labels, calib);
    }
    
    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof Sphere[])
        {
            Sphere[] array = (Sphere[]) obj;
            for (int r = 0; r < array.length; r++)
            {
                Sphere ball = array[r];
                // coordinates of circle center
                table.setValue("Inscribed_Ball_Center_X", r, ball.center().getX());
                table.setValue("Inscribed_Ball_Center_Y", r, ball.center().getY());
                table.setValue("Inscribed_Ball_Center_Z", r, ball.center().getZ());
                
                // circle radius
                table.setValue("Inscribed_Ball_Radius", r, ball.radius());
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of Sphere");
        }
    }
}
