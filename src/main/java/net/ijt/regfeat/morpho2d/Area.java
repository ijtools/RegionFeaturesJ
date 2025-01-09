/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import java.util.Arrays;
import java.util.Collection;

import ij.measure.Calibration;
import net.ijt.regfeat.ElementCount;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.SingleValueFeature;

/**
 * A feature that computes the area of 2D regions.
 * 
 * @see Circularity
 * @see ConvexArea
 */
public class Area extends SingleValueFeature
{
    public Area()
    {
        super("Area");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        int[] counts = (int[]) data.results.get(ElementCount.class);
        
        // volume of unit voxel
        Calibration calib = data.labelMap.getCalibration();
        double pixelArea = calib.pixelWidth * calib.pixelHeight;
        
        // optionally update the feature name to take into account the unit
        if (data.displayUnitsInTable)
        {
            setName(String.format("Area_[%s^2]", calib.getUnit()));
        }
        
        // compute volume from voxel count
        return Arrays.stream(counts)
                .mapToDouble(count -> count * pixelArea)
                .toArray();
    }
    
    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(ElementCount.class);
    }
}
