/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import java.util.Arrays;
import java.util.Collection;

import ij.measure.Calibration;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.SingleValueFeature;
import net.ijt.regfeat.morpho2d.core.Perimeter_Crofton_D4;

/**
 * Computes the perimeter of a 2D region. In practice, this feature is an alias
 * for the Perimeter_Crofton_D4 feature.
 * 
 * @see Circularity
 * @see ConvexPerimeter
 */
public class Perimeter extends SingleValueFeature
{
    public Perimeter()
    {
        super("Perimeter");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);

        // optionally update the feature name to take into account the unit
        if (data.displayUnitsInTable)
        {
            Calibration calib = data.labelMap.getCalibration();
            setName(String.format("Perimeter_[%s]", calib.getUnit()));
        }

        return (double[]) data.results.get(Perimeter_Crofton_D4.class);
    }
    
    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(Perimeter_Crofton_D4.class);
    }
}
