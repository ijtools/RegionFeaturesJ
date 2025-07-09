/**
 * 
 */
package net.ijt.regfeat.intensity;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.SingleValueFeature;

/**
 * Compute the average intensity for each region. 
 */
public class MeanIntensity extends SingleValueFeature
{
    /**
     * Default empty constructor.
     */
    public MeanIntensity()
    {
        super("Mean");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        double[][] allValues = (double[][]) data.results.get(IntensityValues.class);

        // calculate average intensity per region, by converting each array of
        // double into a DoubleStream instance
        return Stream.of(allValues)
                .mapToDouble(values -> DoubleStream.of(values)
                        .average()
                        .orElse(Double.NaN))
                .toArray();
    }

    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(IntensityValues.class);
    }
}
