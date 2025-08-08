/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import java.util.Arrays;
import java.util.Collection;

import inra.ijpb.geometry.OrientedBox2D;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.SingleValueFeature;

/**
 * Elongation of oriented bounding box.
 * 
 * @see OrientedBoundingBox
 */
public class OrientedBoxElongation extends SingleValueFeature
{
    public OrientedBoxElongation()
    {
        super("Oriented_Box_Elongation");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        OrientedBox2D[] OrientedBoxs = (OrientedBox2D[]) data.results.get(OrientedBoundingBox.class);
        
        // iterate over labels to compute new feature
        return Arrays.stream(OrientedBoxs)
            .mapToDouble(OrientedBoxElongation::boxElongation)
            .toArray();
    }
    
    private static final double boxElongation(OrientedBox2D box)
    {
        return box != null ? box.length() / box.width() : Double.NaN;
    }
    
    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(OrientedBoundingBox.class);
    }
}
