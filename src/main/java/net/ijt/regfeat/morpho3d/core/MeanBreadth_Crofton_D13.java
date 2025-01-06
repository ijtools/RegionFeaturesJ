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

/**
 * Computation of the mean breadth using discretization of Crofton formula with 13
 * directions.
 * 
 * @see MeanBreadth_Crofton_D3
 * @see SurfaceArea_Crofton_D13
 */
public class MeanBreadth_Crofton_D13 extends SingleValueFeature
{
    public MeanBreadth_Crofton_D13()
    {
        super("MeanBreadth_Crofton_D13");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        IntrinsicVolumes3D.Result[] results = (IntrinsicVolumes3D.Result[]) data.results.get(IntrinsicVolumes.class);
        
        return Arrays.stream(results)
                .mapToDouble(res -> res.meanBreadth)
                .toArray();
    }
    
    @Override
    public Collection<Class<? extends Feature>>requiredFeatures()
    {
        return Arrays.asList(IntrinsicVolumes.class);
    }
}
