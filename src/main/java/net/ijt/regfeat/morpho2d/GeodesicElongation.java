/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import java.util.Arrays;
import java.util.Collection;

import inra.ijpb.geometry.Circle2D;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.SingleValueFeature;

/**
 * The GeodesicElongation, defined as the ratio of Geodesic diameter over
 * diameter of largest inscribed disk.
 */
public class GeodesicElongation extends SingleValueFeature
{
    /**
     * Default empty constructor.
     */
    public GeodesicElongation()
    {
        super("Geodesic_Elongation");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        double[] geodDiams = (double[]) data.results.get(GeodesicDiameter.class);
        Circle2D[] circles = (Circle2D[]) data.results.get(LargestInscribedDisk.class);
        
        // iterate over labels to compute new feature
        int[] labels = data.labels;
        double[] res = new double[labels.length];
        for (int i = 0; i < labels.length; i++)
        {
            double gd = geodDiams[i];
            double cd = circles[i].getRadius() * 2;
            res[i] = gd / cd;
        }
        return res;
    }
    
    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(GeodesicDiameter.class, LargestInscribedDisk.class);
    }
}
