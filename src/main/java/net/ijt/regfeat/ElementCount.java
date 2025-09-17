/**
 * 
 */
package net.ijt.regfeat;

import java.util.Map;

import ij.ImageStack;
import ij.measure.ResultsTable;
import ij.process.ImageProcessor;
import inra.ijpb.label.LabelImages;

/**
 * Counts the number of elements (pixels or voxels) that compose each region.
 * 
 * Note: this class does not implement SingleValueFeature, as the result is
 * given as an array of int, rather than an array of double.
 */
public class ElementCount implements RegionTabularFeature
{
    /**
     * Default empty constructor.
     */
    public ElementCount()
    {
    }
    
    @Override
    public int[] compute(RegionFeatures data)
    {
        // create map of labels to indices
        Map<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(data.labels);
        
        // retrieve image size
        ImageStack stack = data.labelMap.getStack();
        int sizeX = stack.getWidth();
        int sizeY = stack.getHeight();
        int nSlices = stack.getSize();
        
        // allocate memory
        int[] counts = new int[data.labels.length];
        
        // iterate over slices
        for (int z = 1; z <= nSlices; z++)
        {
            ImageProcessor slice = stack.getProcessor(z);
            
            // iterate over slice pixels
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    // retrieve current label
                    int label = (int) slice.getf(x, y);
                    
                    // process only labels specified in data
                    if (label == 0) continue;
                    if (!labelIndices.containsKey(label)) continue;
                    
                    // update result
                    counts[labelIndices.get(label)]++;
                }
            }
        }
        
        return counts;
    }

    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof int[])
        {
            int[] counts = (int[]) obj;
            for (int r = 0; r < counts.length; r++)
            {
                table.setValue("Count", r, counts[r]);
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of integer values");
        }
    }

}
