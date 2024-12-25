/**
 * 
 */
package net.ijt.regfeat.intensity;

import java.util.Arrays;
import java.util.Collection;

import ij.measure.ResultsTable;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeature;
import net.ijt.regfeat.RegionFeatures;

/**
 * Compute the standard deviation of intensity within each region. 
 */
public class IntensityStandardDeviation implements RegionFeature
{
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        double[] variances = (double[]) data.results.get(IntensityVariance.class);

        // allocate result array
        int nLabels = data.labels.length;
        double[] std = new double[nLabels];
        
        // calculate median intensity per region
        for (int i = 0; i < nLabels; i++)
        {
            std[i] = Math.sqrt(variances[i]);
        }
        return std;
    }

    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof double[])
        {
            double[] array = (double[]) obj;
            for (int r = 0; r < array.length; r++)
            {
                table.setValue("Std. Dev.", r, array[r]);
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of double");
        }
    }

    
    @Override
    public Collection<Class<? extends Feature>>requiredFeatures()
    {
        return Arrays.asList(IntensityVariance.class);
    }
}
