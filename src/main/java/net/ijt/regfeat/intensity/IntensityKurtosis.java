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
 * Compute the kurtosis, or "tailedness", of intensity values within each region.
 * 
 * @see IntensityStandardDeviation
 * @see IntensitySkewness
 */
public class IntensityKurtosis extends SingleValueFeature
{
    public IntensityKurtosis()
    {
        super("Kurtosis");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        double[][] allValues = (double[][]) data.results.get(IntensityValues.class);
        double[] means = (double[]) data.results.get(MeanIntensity.class);
        double[] vars = (double[]) data.results.get(IntensityVariance.class);

        int nLabels = allValues.length;
        double[] res = new double[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
            res[i] = kurtosis(allValues[i], means[i], vars[i]);
        }
        
        return res;
    }
    
    /**
     * Computes an unbiased estimated of the kurtosis of a sample of values.
     * 
     * @see <a href=
     *      "https://en.wikipedia.org/wiki/Kurtosis#Standard_unbiased_estimator">Kurtosis
     *      standard unbiased estimator (Wikipedia)</a>
     * @param values
     *            the array of sample values
     * @param mean
     *            the sample mean
     * @param var
     *            the unbiased estimate of the variance
     * @return an unbiased estimate of the sample kurtosis
     */
    private static final double kurtosis(double[] values, double mean, double var)
    {
        // uses a double to avoid rounding effects
        double n = values.length;
        if (n < 4) return Double.NaN;
        
        double k1 = n * (n + 1) / ((n - 1) * (n - 2) * (n - 3));
        double K_4 = DoubleStream.of(values)
                .map(v -> Math.pow((v - mean), 4))
                .sum();
        double k2 = 3 * (n - 1) * (n - 1) / ((n - 2) * (n - 3));
        
        return k1 * K_4 / (var * var) - k2;
    }

    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(IntensityValues.class, MeanIntensity.class, IntensityVariance.class);
    }
}
