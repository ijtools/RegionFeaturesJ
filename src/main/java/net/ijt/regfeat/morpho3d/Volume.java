/**
 * 
 */
package net.ijt.regfeat.morpho3d;

import java.util.Arrays;
import java.util.Collection;

import ij.measure.Calibration;
import net.ijt.regfeat.ElementCount;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.SingleValueFeature;

/**
 * A feature that computes the volume of regions.
 */
public class Volume extends SingleValueFeature
{
    public Volume()
    {
        super("Volume");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        int[] counts = (int[]) data.results.get(ElementCount.class);
        
        // volume of unit voxel
        Calibration calib = data.labelMap.getCalibration();
        double voxelVolume = calib.pixelWidth * calib.pixelHeight * calib.pixelDepth;
        
        // optionally update the feature name to take into account the unit
        if (data.displayUnitsInTable)
        {
            setName(String.format("Volume_[%s^3]", calib.getUnit()));
        }
        
        // compute volume from voxel count
        return Arrays.stream(counts)
                .mapToDouble(count -> count * voxelVolume)
                .toArray();
    }
    
    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(ElementCount.class);
    }
}
