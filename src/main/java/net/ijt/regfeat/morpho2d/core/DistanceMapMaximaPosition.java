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
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.RegionTabularFeature;

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
        
        // Note: the following can be replaced by MorphoLibJ code from version after 1.6.4
        
        // Init Position and value of maximum for each label
        int nLabels = labels.length;
        Point[] posMax  = new Point[nLabels];
        float[] maxValues = new float[nLabels];
        for (int i = 0; i < nLabels; i++) 
        {
            maxValues[i] = Float.NEGATIVE_INFINITY;
            posMax[i] = new Point(-1, -1);
        }
        
        // iterate on image pixels
        int width   = labelMap.getWidth();
        int height  = labelMap.getHeight();
        for (int y = 0; y < height; y++) 
        {
            for (int x = 0; x < width; x++) 
            {
                // retrieve current label
                int label = (int) labelMap.getf(x, y);
                
                // do not process pixels that do not belong to any particle
                if (label == 0)
                    continue;
                // do not process labels not in the list
                if (!data.labelIndices.containsKey(label))
                    continue;
    
                int index = data.labelIndices.get(label);
                
                // update values and positions
                float value = distanceMap.getf(x, y);
                if (value > maxValues[index]) 
                {
                    posMax[index].setLocation(x, y);
                    maxValues[index] = value;
                }
            }
        }
                
        return posMax;
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
                if (pos != null)
                {
                    // coordinates of inscribed disk center
                    table.setValue("DistanceMap_Maxima_X", r, pos.x);
                    table.setValue("DistanceMap_Maxima_Y", r, pos.y);
                }
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
