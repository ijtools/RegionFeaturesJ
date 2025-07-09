/**
 * 
 */
package net.ijt.regfeat.intensity;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.DoubleStream;

import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.SingleValueFeature;

/**
 * Compute the skewness of intensity values within each region.
 * 
 * @see IntensityStandardDeviation
 * @see IntensityKurtosis
 */
public class IntensitySkewness extends SingleValueFeature
{
    /**
     * Default empty constructor.
     */
    public IntensitySkewness()
    {
        super("Skewness");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        double[][] allValues = (double[][]) data.results.get(IntensityValues.class);
        double[] means = (double[]) data.results.get(MeanIntensity.class);
        double[] stds = (double[]) data.results.get(IntensityStandardDeviation.class);

        int nLabels = allValues.length;
        double[] res = new double[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
            res[i] = skewness(allValues[i], means[i], stds[i]);
        }
        
        return res;
    }
    
    private static final double skewness(double[] values, double mean, double std)
    {
        return DoubleStream.of(values)
                .map(v -> Math.pow((v - mean) / std, 3))
                .average()
                .orElse(Double.NaN);
    }

    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(IntensityValues.class, MeanIntensity.class, IntensityStandardDeviation.class);
    }
}
