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
 * Computation of surface area using discretization of Crofton formula with 13
 * directions.
 * 
 * @see SurfaceArea_Crofton_D3
 */
public class SurfaceArea_Crofton_D13 extends SingleValueFeature
{
    /**
     * Default empty constructor.
     */
    public SurfaceArea_Crofton_D13()
    {
        super("SurfaceArea_Crofton_D13");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        IntrinsicVolumes3D.Result[] results = (IntrinsicVolumes3D.Result[]) data.results.get(IntrinsicVolumes.class);
        
        return Arrays.stream(results)
                .mapToDouble(res -> res.surfaceArea)
                .toArray();
    }
    
    @Override
    public Collection<Class<? extends Feature>>requiredFeatures()
    {
        return Arrays.asList(IntrinsicVolumes.class);
    }
}
