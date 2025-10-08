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
import net.ijt.regfeat.morpho2d.EulerNumber;

/**
 * Euler number using the C4 connectivity.
 * 
 * @see EulerNumber
 */
public class EulerNumber_C4 extends SingleValueFeature
{
    /**
     * Default empty constructor.
     */
    public EulerNumber_C4()
    {
        super("Euler_Number_C4");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        IntrinsicVolumes2D.Result[] results = (IntrinsicVolumes2D.Result[]) data.results.get(IntrinsicVolumes.class);
        
        // convert Euler numbers into an array of double
        return Stream.of(results)
                .mapToDouble(res -> res.eulerNumber)
                .toArray();
    }
    
    @Override
    public Collection<Class<? extends Feature>>requiredFeatures()
    {
        return Arrays.asList(IntrinsicVolumes.class);
    }
}
