/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import ij.measure.Calibration;
import ij.measure.ResultsTable;
import ij.process.ImageProcessor;
import inra.ijpb.measure.region2d.AverageThickness.Result;
import net.ijt.regfeat.RegionFeature;
import net.ijt.regfeat.RegionFeatures;

/**
 * Computes the average thickness of regions, by computing the average of the
 * distance map on the inner skeleton of each region.
 */
public class AverageThickness implements RegionFeature
{

    @Override
    public Result[] compute(RegionFeatures data)
    {
        // retrieve label map and list of labels
        ImageProcessor labelMap = data.labelMap.getProcessor();
        int[] labels = data.labels;
        Calibration calib = data.labelMap.getCalibration();
        
        return new inra.ijpb.measure.region2d.AverageThickness().analyzeRegions(labelMap, labels, calib);
    }

    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof Result[])
        {
            Result[] array = (Result[]) obj;
            for (int r = 0; r < array.length; r++)
            {
                table.setValue("Average_Thickness", r, array[r].avgThickness);
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of double");
        }
    }

}
