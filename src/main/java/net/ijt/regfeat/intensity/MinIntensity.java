/**
 * 
 */
package net.ijt.regfeat.intensity;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.SingleValueFeature;

/**
 * Compute the minimum intensity within each region. 
 */
public class MinIntensity extends SingleValueFeature
{
    public MinIntensity()
    {
        super("Min");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        @SuppressWarnings("unchecked")
        List<Double>[] allValues = (List<Double>[]) data.results.get(IntensityValues.class);

        // calculate minimum intensity per region, by converting each List of
        // Double into a DoubleStream instance
        return Stream.of(allValues)
                .mapToDouble(values -> values.stream()
                        .mapToDouble(Double::doubleValue)
                        .min()
                        .orElse(Double.NaN))
                .toArray();
    }

    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(IntensityValues.class);
    }
}
