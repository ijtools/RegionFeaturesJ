/**
 * 
 */
package net.ijt.regfeat.morpho2d.core;

import java.awt.Point;
import java.util.Arrays;
import java.util.Collection;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.process.ImageProcessor;
import inra.ijpb.algo.AlgoStub;
import inra.ijpb.label.LabelValues;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionTabularFeature;
import net.ijt.regfeat.RegionFeatures;

/**
 * Computes the position of the maximum values of the distance map.
 * 
 * @see DistanceMap
 */
public class DistanceMapMaximaPosition extends AlgoStub implements RegionTabularFeature
{
    /**
     * Default empty constructor.
     */
    public DistanceMapMaximaPosition()
    {
    }
    
    @Override
    public Point[] compute(RegionFeatures data)
    {
        // retrieve label map and list of labels
        ImageProcessor labelMap = data.labelMap.getProcessor();
        int[] labels = data.labels;
        
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        ImageProcessor distanceMap = ((ImagePlus) data.results.get(DistanceMap.class)).getProcessor();
        
        // Extract position of maxima
        return LabelValues.findPositionOfMaxValues(distanceMap, labelMap, labels);
    }
    
    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof Point[])
        {
            Point[] array = (Point[]) obj;
            for (int r = 0; r < array.length; r++)
            {
                Point pos = array[r];
                // coordinates of circle center
                table.setValue("DistanceMap_Maxima_X", r, pos.x);
                table.setValue("DistanceMap_Maxima_Y", r, pos.y);
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of Point");
        }
    }

    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(DistanceMap.class);
    }
}
