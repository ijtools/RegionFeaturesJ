/**
 * 
 */
package net.ijt.regfeat.intensity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.process.ImageProcessor;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.RegionFeature;
import net.ijt.regfeat.RegionFeatures;

/**
 * Computes an array of intensity values for each region. This is the base
 * feature for most other statistics.
 */
public final class IntensityValues implements RegionFeature
{
    @Override
    public List<Double>[] compute(RegionFeatures data)
    {
        // retrieve necessary data
        ImagePlus intensityImage = data.getImageData("intensity");
        if (intensityImage == null)
        {
            throw new RuntimeException("Requires to populate the 'RegionFeatures' class with an 'intensity' image data");
        }
        
        int nLabels = data.labels.length;
        
        // initializes one array list for each label
        @SuppressWarnings("unchecked")
        ArrayList<Double>[] regionValues = (ArrayList<Double>[]) new ArrayList[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
            regionValues[i] = new ArrayList<Double>();
        }
        
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
                    
                    // add current intensity value to the list associated to current region
                    regionValues[labelIndices.get(label)].add((double) valueMap.getf(x, y));
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

}
