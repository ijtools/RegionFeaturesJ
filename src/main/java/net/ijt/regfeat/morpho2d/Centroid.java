/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import java.awt.geom.Point2D;

import ij.measure.Calibration;
import ij.measure.ResultsTable;
import ij.process.ImageProcessor;
import net.ijt.regfeat.RegionFeature;
import net.ijt.regfeat.RegionFeatures;

/**
 * Computes centroid of each regions within the label map.
 */
public class Centroid implements RegionFeature
{
    @Override
    public Point2D[] compute(RegionFeatures data)
    {
        ImageProcessor labelMap = data.labelMap.getProcessor();
        Calibration calib = data.labelMap.getCalibration();
        return inra.ijpb.measure.region2d.Centroid.centroids(labelMap, data.labels, calib);
    }
    
    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        String[] colNames = new String[] {"Centroid_X", "Centroid_Y"};
        if (data.displayUnitsInTable)
        {
            // update the name to take into account the unit
            Calibration calib = data.labelMap.getCalibration();
            colNames[0] = String.format("Centroid_X_[%s]", calib.getUnit());
            colNames[1] = String.format("Centroid_Y_[%s]", calib.getUnit());
        }
        
        Object obj = data.results.get(this.getClass());
        if (obj instanceof Point2D[])
        {
            Point2D[] array = (Point2D[]) obj;
            for (int r = 0; r < array.length; r++)
            {
                Point2D point = array[r];
                table.setValue(colNames[0], r, point.getX());
                table.setValue(colNames[1], r, point.getY());
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of Point2D");
        }
    }
}
