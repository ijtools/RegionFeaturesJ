/**
 * 
 */
package net.ijt.regfeat.morpho3d;

import java.util.Arrays;
import java.util.Collection;

import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.SingleValueFeature;
import net.ijt.regfeat.morpho3d.core.EulerNumber_C6;

/**
 * Computes the Euler number of 2D regions. Uses the default C4 connectivity. In
 * practice, this feature is an alias for the EulerNumber_C4 feature.
 */
public class EulerNumber extends SingleValueFeature
{
    public EulerNumber()
    {
        super("Euler_Number");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        return (double[]) data.results.get(EulerNumber_C6.class);
    }
    
    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(EulerNumber_C6.class);
    }
}
