/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.DoubleStream;

import ij.ImagePlus;
import ij.measure.Calibration;
import ij.process.ImageProcessor;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.SingleValueFeature;
import net.ijt.regfeat.morpho2d.core.DistanceMap;
import net.ijt.regfeat.morpho2d.core.Skeleton;

/**
 * Computes the average thickness of regions, by computing the average of the
 * distance map on the inner skeleton of each region.
 */
public class AverageThickness extends SingleValueFeature
{
    public AverageThickness()
    {
        super("Average_Thickness");
    }

    @Override
    public double[] compute(RegionFeatures data)
    {
        // check input validity
        Calibration calib = data.labelMap.getCalibration();
        if (calib.pixelWidth != calib.pixelHeight)
        {
            throw new IllegalArgumentException("Requires input image to have square pixels (width = height)");
        }
        
        // Retrieve or compute the skeleton of each region
        ImageProcessor skeleton = ((ImagePlus) data.results.get(Skeleton.class)).getProcessor();
        
        // Retrieve or compute the distance map associated to each region
        ImageProcessor distanceMap = ((ImagePlus) data.results.get(DistanceMap.class)).getProcessor();

        // compute average thickness in pixel coordinates
        double[] res = averageThickness(skeleton, distanceMap, data.labels);
        
        // calibrate the array of thicknesses
        return DoubleStream.of(res)
                .map(v -> v * calib.pixelWidth)
                .toArray();
    }
    
    /**
     * Computes the average thickness in pixel coordinates.
     * 
     * @param skeleton
     *            the ImageProcessor containing the skeleton of each region
     * @param distanceMap
     *            the ImageProcessor containing the distance map of each region
     * @param labels
     *            the array of labels to compute
     * @return the average thickness of each region with a label within the
     *         {@code labels} array.
     */
    private static final double[] averageThickness(ImageProcessor skeleton, ImageProcessor distanceMap, int[] labels)
    {
        // retrieve image size
        int sizeX = skeleton.getWidth();
        int sizeY = skeleton.getHeight();

        // allocate memory for result values
        int nLabels = labels.length;
        double[] sums = new double[nLabels];
        int[] counts = new int[nLabels];

        // Iterate over skeleton pixels
        Map<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(labels);
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                // label of current pixel 
                int label = (int) skeleton.getf(x, y);
                if (label == 0)
                {
                    continue;
                }

                int index = labelIndices.get(label);
                
                // update results for current region
                sums[index] += distanceMap.getf(x, y);
                counts[index]++;
            }
        }
        
        // compute average thickness from the ratio of sum of skeleton values
        // divided by number of skeleton pixels
        double[] res = new double[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
            res[i] = (sums[i] / counts[i]) * 2 - 1;
        }
        return res;
    }
    
    @Override
    public String[] columnUnitNames(RegionFeatures data)
    {
        return new String[] {data.labelMap.getCalibration().getUnit()};
    }

    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(Skeleton.class, DistanceMap.class);
    }
}
