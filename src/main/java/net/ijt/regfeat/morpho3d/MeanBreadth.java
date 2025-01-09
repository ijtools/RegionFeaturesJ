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
import net.ijt.regfeat.morpho3d.core.MeanBreadth_Crofton_D13;

/**
 * Computes the mean breadth of a 3D region. In practice, this feature is an alias
 * for the MeanBreadth_Crofton_D13 feature.
 * 
 * @see Volume
 * @see SurfaceArea
 * @see EulerNumber
 */
public class MeanBreadth extends SingleValueFeature
{
    public MeanBreadth()
    {
        super("Mean_Breadth");
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
            setName(String.format("Mean_Breadth_[%s]", calib.getUnit()));
        }
        
        return (double[]) data.results.get(MeanBreadth_Crofton_D13.class);
    }
    
    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(MeanBreadth_Crofton_D13.class);
    }
}
