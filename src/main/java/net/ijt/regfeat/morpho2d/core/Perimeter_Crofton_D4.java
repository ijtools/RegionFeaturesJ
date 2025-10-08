/**
 * 
 */
package net.ijt.regfeat.morpho2d.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import inra.ijpb.measure.region2d.IntrinsicVolumes2D;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.SingleValueFeature;

/**
 * Computation of perimeter using discretization of Crofton formula with four
 * directions.
 */
public class Perimeter_Crofton_D4 extends SingleValueFeature
{
    /**
     * Default empty constructor.
     */
    public Perimeter_Crofton_D4()
    {
        super("Perimeter_Crofton_D4");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        IntrinsicVolumes2D.Result[] results = (IntrinsicVolumes2D.Result[]) data.results.get(IntrinsicVolumes.class);
        
        // convert perimeter values into an array of double
        return Stream.of(results)
                .mapToDouble(res -> res.perimeter)
                .toArray();
    }
    
    @Override
    public Collection<Class<? extends Feature>>requiredFeatures()
    {
        return Arrays.asList(IntrinsicVolumes.class);
    }
}
