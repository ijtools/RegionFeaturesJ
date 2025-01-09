/**
 * 
 */
package net.ijt.regfeat.morpho3d;

import java.util.Arrays;
import java.util.Collection;

import ij.measure.Calibration;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.SingleValueFeature;
import net.ijt.regfeat.morpho3d.core.SurfaceArea_Crofton_D13;

/**
 * Computes the surface area of a 3D region. In practice, this feature is an alias
 * for the SurfaceArea_Crofton_D13 feature.
 * 
 * @see Volume
 * @see MeanBreadth
 * @see EulerNumber
 */
public class SurfaceArea extends SingleValueFeature
{
    public SurfaceArea()
    {
        super("Surface_Area");
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
            setName(String.format("Surface_Area_[%s^2]", calib.getUnit()));
        }
        
        return (double[]) data.results.get(SurfaceArea_Crofton_D13.class);
    }
    
    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(SurfaceArea_Crofton_D13.class);
    }
}
