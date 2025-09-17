/**
 * 
 */
package net.ijt.regfeat.morpho3d;

import ij.ImageStack;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import inra.ijpb.geometry.Sphere;
import net.ijt.regfeat.RegionTabularFeature;
import net.ijt.regfeat.RegionFeatures;

/**
 * Computes the largest inscribed ball within 3D regions of a label map.
 */
public class LargestInscribedBall implements RegionTabularFeature
{
    /**
     * The names of the columns of the resulting table.
     */
    public static final String[] colNames = new String[] {
            "Inscribed_Ball_Center_X",
            "Inscribed_Ball_Center_Y",
            "Inscribed_Ball_Center_Z",
            "Inscribed_Ball_Radius"};
    
    /**
     * Default empty constructor.
     */
    public LargestInscribedBall()
    {
    }
    
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
                // coordinates of ball center
                table.setValue(colNames[0], r, ball.center().getX());
                table.setValue(colNames[1], r, ball.center().getY());
                table.setValue(colNames[2], r, ball.center().getZ());
                
                // ball radius
                table.setValue(colNames[3], r, ball.radius());
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of Sphere");
        }
    }
    
    @Override
    public String[] columnUnitNames(RegionFeatures data)
    {
        String[] unitNames = new String[colNames.length];
        String unitName = data.labelMap.getCalibration().getUnit();
        for (int c = 0; c < colNames.length; c++)
        {
            unitNames[c] = unitName;
        }
        return unitNames;
    }
    
}
