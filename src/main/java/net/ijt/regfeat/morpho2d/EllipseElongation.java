/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import java.util.Arrays;
import java.util.Collection;

import inra.ijpb.geometry.Ellipse;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.SingleValueFeature;

/**
 * Elongation of Equivalent ellipse.
 * 
 * @see EquivalentEllipse.
 */
public class EllipseElongation extends SingleValueFeature
{
    public EllipseElongation()
    {
        super("Ellipse_Elongation");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        Ellipse[] ellipses = (Ellipse[]) data.results.get(EquivalentEllipse.class);
        
        // iterate over labels to compute new feature
        return Arrays.stream(ellipses)
            .mapToDouble(elli -> elli.radius1() / elli.radius2())
            .toArray();
    }

    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(EquivalentEllipse.class);
    }
}
