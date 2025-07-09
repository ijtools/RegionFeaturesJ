/**
 * 
 */
package net.ijt.regfeat.intensity;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.process.ImageProcessor;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.ElementCount;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionTabularFeature;
import net.ijt.regfeat.RegionFeatures;

/**
 * Computes an array of intensity values for each region. This is the base
 * feature for most other statistics.
 */
public final class IntensityValues implements RegionTabularFeature
{
    /**
     * Default empty constructor.
     */
    public IntensityValues()
    {
    }
    
    @Override
    public double[][] compute(RegionFeatures data)
    {
        // retrieve necessary data
        ImagePlus intensityImage = data.getImageData("intensity");
        if (intensityImage == null)
        {
            throw new RuntimeException("Requires to populate the 'RegionFeatures' class with an 'intensity' image data");
        }
        
        int nLabels = data.labels.length;
        
        // retrieve number of elements of each region
        data.ensureRequiredFeaturesAreComputed(this);
        int[] counts = (int[]) data.results.get(ElementCount.class);
        
        // initializes one array for each label
        double[][] regionValues = new double[nLabels][];
        for (int i = 0; i < nLabels; i++)
        {
            regionValues[i] = new double[counts[i]];
        }
        
        // initialize one counter for each array
        int[] counters = new int[nLabels];
        
        // create associative hash table to know the index of each label
        Map<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(data.labels);
        
        // retrieve image size(s)
        int sizeX = data.labelMap.getWidth();
        int sizeY = data.labelMap.getHeight();
        int sizeZ = data.labelMap.getImageStackSize();
        
        // iterate over slices
        for (int z = 1; z <= sizeZ; z++)
        {
            // retrieve the image processors corresponding to the current slice 
            ImageProcessor labelMap = data.labelMap.getImageStack().getProcessor(z);
            ImageProcessor valueMap = intensityImage.getImageStack().getProcessor(z);
            
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    // retrieve label of current region
                    int label = (int) labelMap.getf(x, y);
                    
                    // check label need to be processed
                    if (label == 0) continue;
                    if (!labelIndices.containsKey(label)) continue;
                    
                    // add current intensity value to the array associated to the current region
                    int index = labelIndices.get(label);
                    regionValues[index][counters[index]++] = (double) valueMap.getf(x, y);
                }
            }
        }
        
        return regionValues;
    }

    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        // nothing to do...
    }

    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(ElementCount.class);
    }
}
