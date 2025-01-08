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
import net.ijt.regfeat.morpho2d.core.GeodesicDiameterData;
import net.ijt.regfeat.morpho2d.core.GeodesicDiameterData.Result;

/**
 * Computes the geodesic diameter of each region.
 * 
 * @see GeodesicElongation
 * @see Tortuosity
 */
public class GeodesicDiameter extends SingleValueFeature
{
    public GeodesicDiameter()
    {
        super("Geodesic_Diameter");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        Result[] results = (Result[]) data.results.get(GeodesicDiameterData.class);
        
        // iterate over labels, and re-calibrate the geodesic diameter
        Calibration calib = data.labelMap.getCalibration();
        return Arrays.stream(results)
                .mapToDouble(res -> res.diameter * calib.pixelWidth)
                .toArray();
    }

    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(GeodesicDiameterData.class);
    }
}
