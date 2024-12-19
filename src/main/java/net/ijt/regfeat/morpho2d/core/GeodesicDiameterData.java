/**
 * 
 */
package net.ijt.regfeat.morpho2d.core;

import static inra.ijpb.measure.region2d.GeodesicDiameter.geodesicDiameters;

import ij.measure.Calibration;
import ij.measure.ResultsTable;
import ij.process.ImageProcessor;
import inra.ijpb.measure.region2d.GeodesicDiameter.Result;
import net.ijt.regfeat.RegionFeature;
import net.ijt.regfeat.RegionFeatures;

/**
 * 
 */
public class GeodesicDiameterData implements RegionFeature
{

    @Override
    public Result[] compute(RegionFeatures data)
    {
        // retrieve label map and list of labels
        ImageProcessor labelMap = data.labelMap.getProcessor();
        int[] labels = data.labels;
        Calibration calib = data.labelMap.getCalibration();
        
        return geodesicDiameters(labelMap, labels, calib);
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
                // current diameter
                Result res = array[r];

                // add an entry to the resulting data table
                table.setValue("GeodesicDiameter", r, res.diameter);

                // coordinates of max inscribed circle
                table.setValue("Radius", r, res.innerRadius);
                table.setValue("InitPoint.X", r, res.initialPoint.getX());
                table.setValue("InitPoint.Y", r, res.initialPoint.getY());
                table.setValue("GeodesicElongation", r, Math.max(res.diameter / (res.innerRadius * 2), 1.0));

                // coordinate of first and second geodesic extremities 
                table.setValue("Extremity1.X", r, res.firstExtremity.getX());
                table.setValue("Extremity1.Y", r, res.firstExtremity.getY());
                table.setValue("Extremity2.X", r, res.secondExtremity.getX());
                table.setValue("Extremity2.Y", r, res.secondExtremity.getY());
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of GeodesicDiameter.Result");
        }
    }
}
