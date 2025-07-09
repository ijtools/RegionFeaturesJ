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
 * The sphericity of a region, defined from normalized ratio of squared volume and
 * cubed surface area.
 * 
 * @see Volume
 * @see SurfaceArea
 */
public class Sphericity extends SingleValueFeature
{
    /**
     * Default empty constructor.
     */
    public Sphericity()
    {
        super("Sphericity");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        double[] vols = (double[]) data.results.get(Volume.class);
        double[] surfs = (double[]) data.results.get(SurfaceArea.class);

        // normalization constant such that sphere has a sphericity equal to 1
        double norm = 36 * Math.PI;

        // iterate over labels to compute new feature
        int[] labels = data.labels;
        double[] res = new double[labels.length];
        for (int i = 0; i < labels.length; i++)
        {
            double v = vols[i];
            double s = surfs[i];
            double sph = norm * v * v / (s * s * s);
            res[i] = sph;
        }
        return res;
    }

    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(Volume.class, SurfaceArea.class);
    }
}
