/**
 * 
 */
package net.ijt.regfeat.intensity;

import java.util.Arrays;
import java.util.Collection;
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
        double[][] allValues = (double[][]) data.results.get(IntensityValues.class);

        // calculate average intensity per region, by converting each array of
        // double into a DoubleStream instance
        return Stream.of(allValues)
                .mapToDouble(values -> median(values))
                .toArray();
    }
    
    /**
     * Computes the median value of the Double values within the list.
     * 
     * @param values
     *            an array of double values
     * @return the median of the values within the array, or NaN if the array is
     *         empty
     */
    private static final double median(double[] values)
    {
        if (values.length == 0) return Double.NaN;
                
        double[] arr = Arrays.copyOf(values, values.length);
        Arrays.sort(arr);
        return arr[arr.length / 2];
    }

    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(IntensityValues.class);
    }
}
