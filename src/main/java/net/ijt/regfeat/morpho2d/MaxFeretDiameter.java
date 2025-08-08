/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import java.util.Arrays;
import java.util.Collection;

import inra.ijpb.geometry.PointPair2D;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.SingleValueFeature;
import net.ijt.regfeat.morpho2d.core.FurthestPointPair;

/**
 * Computes the largest Feret diameter of each region within a label map.
 * 
 * @see Tortuosity
 */
public class MaxFeretDiameter extends SingleValueFeature
{
    /**
     * Default empty constructor.
     */
    public MaxFeretDiameter()
    {
        super("Max_Feret_Diameter");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        PointPair2D[] pairs = (PointPair2D[]) data.results.get(FurthestPointPair.class);
        
        return Arrays.stream(pairs)
                .mapToDouble(pair -> pair != null ? pair.diameter() : Double.NaN)
                .toArray();
    }

    @Override
    public Collection<Class<? extends Feature>>requiredFeatures()
    {
        return Arrays.asList(FurthestPointPair.class);
    }
    
    @Override
    public String[] columnUnitNames(RegionFeatures data)
    {
        return new String[] {data.labelMap.getCalibration().getUnit()};
    }
}
