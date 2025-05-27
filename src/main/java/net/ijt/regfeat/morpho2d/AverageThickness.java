/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import java.util.Arrays;

import ij.measure.Calibration;
import ij.process.ImageProcessor;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.SingleValueFeature;

/**
 * Computes the average thickness of regions, by computing the average of the
 * distance map on the inner skeleton of each region.
 */
public class AverageThickness extends SingleValueFeature
{
    public AverageThickness()
    {
        super("Average_Thickness");
    }

    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve label map and list of labels
        ImageProcessor labelMap = data.labelMap.getProcessor();
        int[] labels = data.labels;
        Calibration calib = data.labelMap.getCalibration();
        
        // retrieve the "avgThickness" field from the array of Result
        inra.ijpb.measure.region2d.AverageThickness algo = new inra.ijpb.measure.region2d.AverageThickness();
        return Arrays.stream(algo.analyzeRegions(labelMap, labels, calib))
                .mapToDouble(res -> res.avgThickness)
                .toArray();
    }
    
    @Override
    public String[] columnUnitNames(RegionFeatures data)
    {
        return new String[] {data.labelMap.getCalibration().getUnit()};
    }
}
