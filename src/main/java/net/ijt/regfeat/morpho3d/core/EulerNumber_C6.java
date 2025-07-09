/**
 * 
 */
package net.ijt.regfeat.morpho3d.core;

import java.util.Arrays;
import java.util.Collection;

import inra.ijpb.measure.region3d.IntrinsicVolumes3D;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.SingleValueFeature;
import net.ijt.regfeat.morpho2d.EulerNumber;

/**
 * Euler number for 3D regions using the C6 connectivity.
 * 
 * @see EulerNumber
 */
public class EulerNumber_C6 extends SingleValueFeature
{
    /**
     * Default empty constructor.
     */
    public EulerNumber_C6()
    {
        super("Euler_Number_C6");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        IntrinsicVolumes3D.Result[] res = (IntrinsicVolumes3D.Result[]) data.results.get(IntrinsicVolumes.class);
        
        double[] eulers = new double[res.length];
        for (int i = 0; i < res.length; i++)
        {
            eulers[i] = res[i].eulerNumber;
        }
        return eulers;
    }
    
    @Override
    public Collection<Class<? extends Feature>>requiredFeatures()
    {
        return Arrays.asList(IntrinsicVolumes.class);
    }
}
