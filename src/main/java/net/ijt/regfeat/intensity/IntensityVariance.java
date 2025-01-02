/**
 * 
 */
package net.ijt.regfeat.intensity;

import java.util.Arrays;
import java.util.Collection;

import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.SingleValueFeature;

/**
 * Compute the variance of intensity values within each region.
 * 
 * @see MeanIntensity
 * @see IntensityStandardDeviation
 * @see IntensitySkewness
 * @see IntensityKurtosis
 */
public class IntensityVariance extends SingleValueFeature
{
    public IntensityVariance()
    {
        super("Variance");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        double[][] allValues = (double[][]) data.results.get(IntensityValues.class);
        double[] meanValues = (double[]) data.results.get(MeanIntensity.class);

        // allocate result array
        int nLabels = data.labels.length;
        double[] vars = new double[nLabels]; 
        
        // calculate variance of intensity per region
        for (int i = 0; i < nLabels; i++)
        {
            vars[i] = variance(allValues[i], meanValues[i]);
        }
        return vars;
    }
    
    private static final double variance(double[] values, double mean)
    {
        // requires at least two values to compute variance
        if (values.length < 2) return Double.NaN;
        
        double sumSq = 0;
        for (double v : values)
        {
            sumSq += (v - mean) * (v - mean);
        }
        return sumSq / (values.length - 1);
    }

    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(IntensityValues.class, MeanIntensity.class);
    }
}
