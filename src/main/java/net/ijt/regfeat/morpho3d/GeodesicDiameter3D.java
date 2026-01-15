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
import net.ijt.regfeat.morpho3d.core.GeodesicDiameter3DData;
import net.ijt.regfeat.morpho3d.core.GeodesicDiameter3DData.Result;


/**
 * Computes the 3D geodesic diameter of each region within a label map.
 * 
 */
public class GeodesicDiameter3D extends SingleValueFeature
{
    /**
     * Default empty constructor.
     */
    public GeodesicDiameter3D()
    {
        super("Geodesic_Diameter_3D");
    }

    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        Result[] results = (Result[]) data.results.get(GeodesicDiameter3DData.class);
        
        // iterate over labels, and re-calibrate the geodesic diameter
        Calibration calib = data.labelMap.getCalibration();
        return Arrays.stream(results)
                .mapToDouble(res -> res.diameter * calib.pixelWidth)
                .toArray();
    }

    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(GeodesicDiameter3DData.class);
    }
    
    @Override
    public String[] columnUnitNames(RegionFeatures data)
    {
        return new String[] {data.labelMap.getCalibration().getUnit()};
    }
}
