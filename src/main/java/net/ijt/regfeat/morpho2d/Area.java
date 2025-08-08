/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import java.util.Arrays;
import java.util.Map;

import ij.measure.Calibration;
import ij.process.ImageProcessor;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.ElementCount;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.SingleValueFeature;

/**
 * A feature that computes the area of 2D regions. In case of 3D label map,
 * computes the area of regions on the current slice.
 * 
 * @see Perimeter
 * @see Circularity
 * @see ConvexArea
 * @see ElementCount
 */
public class Area extends SingleValueFeature
{
    /**
     * Default empty constructor.
     */
    public Area()
    {
        super("Area");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        
        ImageProcessor labelMap = data.labelMap.getProcessor();
        // create map of labels to indices
        Map<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(data.labels);

        // area of unit voxel
        Calibration calib = data.labelMap.getCalibration();
        double pixelArea = calib.pixelWidth * calib.pixelHeight;

        // retrieve image size
        int sizeX = labelMap.getWidth();
        int sizeY = labelMap.getHeight();
        
        // allocate memory
        int[] counts = new int[data.labels.length];
        
        // iterate over image pixels
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                // retrieve current label
                int label = (int) labelMap.getf(x, y);

                // process only labels specified in data
                if (label == 0) continue;
                if (!labelIndices.containsKey(label)) continue;

                // update result
                counts[labelIndices.get(label)]++;
            }
        }

        // compute area from pixel count
        return Arrays.stream(counts)
                .mapToDouble(count -> count * pixelArea)
                .toArray();
    }
    
    @Override
    public String[] columnUnitNames(RegionFeatures data)
    {
        return new String[] {data.labelMap.getCalibration().getUnit() + "^2"};
    }
}
