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
 * Compute the median intensity within each region. 
 */
public class MedianIntensity extends SingleValueFeature
{
    public MedianIntensity()
    {
        super("Median");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        @SuppressWarnings("unchecked")
        List<Double>[] allValues = (List<Double>[]) data.results.get(IntensityValues.class);

        // calculate average intensity per region, by converting each List of
        // Double into a DoubleStream instance
        return Stream.of(allValues)
                .mapToDouble(values -> median(values))
                .toArray();
    }
    
    /**
     * Computes the median value of the Double values within the list.
     * 
     * @param values
     *            a list of Double
     * @return the median of the values, or NaN if the list is empty
     */
    private static final double median(List<Double> values)
    {
        if (values.isEmpty()) return Double.NaN;
                
        double[] arr = values.stream().mapToDouble(Double::doubleValue).toArray();
        Arrays.sort(arr);
        return arr[arr.length / 2];
    }

    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(IntensityValues.class);
    }
}
