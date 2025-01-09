/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import java.util.Arrays;
import java.util.Collection;

import ij.measure.Calibration;
import inra.ijpb.geometry.PointPair2D;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.SingleValueFeature;
import net.ijt.regfeat.morpho2d.core.FurthestPointPair;

/**
 * 
 */
public class MaxFeretDiameter extends SingleValueFeature
{
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
        
        // optionally update the feature name to take into account the unit
        if (data.displayUnitsInTable)
        {
            Calibration calib = data.labelMap.getCalibration();
            setName(String.format("Max_Feret_Diameter_[%s]", calib.getUnit()));
        }
        
        return Arrays.stream(pairs)
                .mapToDouble(pair -> pair.diameter())
                .toArray();
    }

    @Override
    public Collection<Class<? extends Feature>>requiredFeatures()
    {
        return Arrays.asList(FurthestPointPair.class);
    }
}
