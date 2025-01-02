/**
 * 
 */
package net.ijt.regfeat.intensity;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.SingleValueFeature;

/**
 * Compute the variance of intensity within each region. 
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
        @SuppressWarnings("unchecked")
        List<Double>[] allValues = (List<Double>[]) data.results.get(IntensityValues.class);
        double[] meanValues = (double[]) data.results.get(MeanIntensity.class);

        // allocate result array
        int nLabels = data.labels.length;
        double[] vars = new double[nLabels]; 
        
        // calculate variance of intensity per region
        for (int i = 0; i < nLabels; i++)
        {
            double sumSq = 0;
            for (double v : allValues[i])
            {
                double v2 = v - meanValues[i];
                sumSq += v2 * v2;
            }
            vars[i] = sumSq / (allValues[i].size() - 1);
        }
        return vars;
    }

    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(IntensityValues.class, MeanIntensity.class);
    }
}
