/**
 * 
 */
package net.ijt.regfeat.morpho3d;

import java.util.Arrays;
import java.util.Collection;

import inra.ijpb.geometry.Sphere;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.SingleValueFeature;

/**
 * The GeodesicElongation, defined as the ratio of Geodesic diameter over
 * diameter of largest inscribed disk.
 */
public class GeodesicElongation3D extends SingleValueFeature
{
    /**
     * Default empty constructor.
     */
    public GeodesicElongation3D()
    {
        super("Geodesic_Elongation");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        double[] geodDiams = (double[]) data.results.get(GeodesicDiameter3D.class);
        Sphere[] balls = (Sphere[]) data.results.get(LargestInscribedBall.class);
        
        // iterate over labels to compute new feature
        int[] labels = data.labels;
        double[] res = new double[labels.length];
        for (int i = 0; i < labels.length; i++)
        {
            if (balls[i] != null)
            {
                double gd = geodDiams[i];
                double cd = balls[i].radius() * 2;
                res[i] = gd / cd;
            }
            else
            {
                res[i] = Double.NaN;
            }
        }
        return res;
    }
    
    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(GeodesicDiameter3D.class, LargestInscribedBall.class);
    }
}
