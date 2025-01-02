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
 * Compute the standard deviation of intensity within each region. 
 */
public class IntensityStandardDeviation extends SingleValueFeature
{
    public IntensityStandardDeviation()
    {
        super("Standard_Deviation");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        double[] variances = (double[]) data.results.get(IntensityVariance.class);

        // calculate standard deviation of intensities per region
        return DoubleStream.of(variances).map(Math::sqrt).toArray();
    }

    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(IntensityVariance.class);
    }
}
