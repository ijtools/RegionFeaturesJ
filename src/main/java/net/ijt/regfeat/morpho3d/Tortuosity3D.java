/**
 * 
 */
package net.ijt.regfeat.morpho3d;

import java.util.Arrays;
import java.util.Collection;

import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.SingleValueFeature;

/**
 * The tortuosity, defined as the ratio of (3D) Geodesic diameter over (3D) Max
 * Feret Diameter.
 * 
 * @see GeodesicDiameter3D
 * @see MaxFeretDiameter3D
 */
public class Tortuosity3D extends SingleValueFeature
{
    /**
     * Default empty constructor.
     */
    public Tortuosity3D()
    {
        super("Tortuosity");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        double[] geodDiams = (double[]) data.results.get(GeodesicDiameter3D.class);
        double[] feretDiams = (double[]) data.results.get(MaxFeretDiameter3D.class);
        
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
        return Arrays.asList(GeodesicDiameter3D.class, MaxFeretDiameter3D.class);
    }
}
