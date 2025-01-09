/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import java.util.Arrays;
import java.util.Collection;

import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.SingleValueFeature;

/**
 * The tortuosity, defined as the ratio of Geodesic diameter over Max Feret
 * Diameter.
 * 
 * @see GeodesicDiameter
 * @see MaxFeretDiameter
 */
public class Tortuosity extends SingleValueFeature
{
    public Tortuosity()
    {
        super("Tortuosity");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        double[] geodDiams = (double[]) data.results.get(GeodesicDiameter.class);
        double[] feretDiams = (double[]) data.results.get(MaxFeretDiameter.class);
        
        // iterate over labels to compute new feature
        int[] labels = data.labels;
        double[] res = new double[labels.length];
        for (int i = 0; i < labels.length; i++)
        {
            double gd = geodDiams[i];
            double fd = feretDiams[i];
            res[i] = gd / fd;
        }
        return res;
    }
    
    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(GeodesicDiameter.class, MaxFeretDiameter.class);
    }
}
