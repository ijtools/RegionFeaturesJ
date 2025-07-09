/**
 * 
 */
package net.ijt.regfeat.morpho3d.core;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import inra.ijpb.measure.region3d.IntrinsicVolumes3D;
import net.ijt.regfeat.RegionTabularFeature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.morpho3d.Volume;

/**
 * Computes the four intrinsic volumes in 3D, and returns an array of
 * <code>IntrinsicVolumes3D.Result</code>.
 * 
 * This feature is devoted to factorize the computation of the Volume, Surface
 * area, mean breadth, and EulerNumber features.
 * 
 * @see Volume
 */
public class IntrinsicVolumes implements RegionTabularFeature
{
    /**
     * The names of the columns of the resulting table.
     */
    public static final String[] colNames = new String[] {"Volume", "Surface_Area", "Mean_Breadth", "Euler_Number"};
    
    /**
     * Default empty constructor.
     */
    public IntrinsicVolumes()
    {
    }
    
    @Override
    public IntrinsicVolumes3D.Result[] compute(RegionFeatures results)
    {
        ImagePlus labelMap = results.labelMap;
            
        IntrinsicVolumes3D algo = new IntrinsicVolumes3D();
        return algo.analyzeRegions(labelMap.getStack(), results.labels, labelMap.getCalibration());
    }
    
    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof IntrinsicVolumes3D.Result[])
        {
            IntrinsicVolumes3D.Result[] array = (IntrinsicVolumes3D.Result[]) obj;
            for (int r = 0; r < array.length; r++)
            {
                IntrinsicVolumes3D.Result res = array[r];
                table.setValue(colNames[0], r, res.volume);
                table.setValue(colNames[1], r, res.surfaceArea);
                table.setValue(colNames[2], r, res.meanBreadth);
                table.setValue(colNames[3], r, res.eulerNumber);
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of IntrinsicVolumes3D.Result");
        }
    }
}
