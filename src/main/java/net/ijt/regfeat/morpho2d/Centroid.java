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
    public Point2D[] compute(RegionFeatures results)
    {
        ImageProcessor labelMap = results.labelMap.getProcessor();
        Calibration calib = results.labelMap.getCalibration();
        return inra.ijpb.measure.region2d.Centroid.centroids(labelMap, results.labels, calib);
    }
    
    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof Point2D[])
        {
            Point2D[] array = (Point2D[]) obj;
            for (int r = 0; r < array.length; r++)
            {
                Point2D point = array[r];
                table.setValue("Centroid_X", r, point.getX());
                table.setValue("Centroid_Y", r, point.getY());
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of Point2D");
        }
    }
}
