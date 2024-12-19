/**
 * 
 */
package net.ijt.regfeat.morpho2d.core;

import java.util.Arrays;
import java.util.Collection;

import ij.measure.ResultsTable;
import inra.ijpb.measure.region2d.IntrinsicVolumes2D;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.morpho2d.EulerNumber;

/**
 * Euler number using the C4 connectivity.
 * 
 * @see EulerNumber
 */
public class EulerNumber_C4 extends RegionFeature
{
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        ensureRequiredFeaturesAreComputed(data);
        IntrinsicVolumes2D.Result[] res = (IntrinsicVolumes2D.Result[]) data.results.get(IntrinsicVolumes.class);
        
        double[] eulers = new double[res.length];
        for (int i = 0; i < res.length; i++)
        {
            eulers[i] = res[i].eulerNumber;
        }
        return eulers;
    }
    
    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof double[])
        {
            double[] array = (double[]) obj;
            for (int r = 0; r < array.length; r++)
            {
                table.setValue("Euler Number_C4", r, array[r]);
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of double");
        }
    }
    
    @Override
    public Collection<Class<? extends Feature>>requiredFeatures()
    {
        return Arrays.asList(IntrinsicVolumes.class);
    }
}
