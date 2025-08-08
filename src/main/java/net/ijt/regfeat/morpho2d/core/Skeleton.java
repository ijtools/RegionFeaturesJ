/**
 * 
 */
package net.ijt.regfeat.morpho2d.core;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import inra.ijpb.binary.BinaryImages;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;

/**
 * Computes the skeleton of each region within a label map, and returns another
 * image containing the skeleton of each region.
 * 
 * Uses an adaptation of the algorithm from ImageJ.
 * 
 * @see inra.ijpb.binary.BinaryImages#skeleton(ImageProcessor)
 */
public class Skeleton implements Feature
{
    /**
     * Default empty constructor.
     */
    public Skeleton()
    {
    }

    @Override
    public ImagePlus compute(RegionFeatures data)
    {
        // Compute skeleton of each region.
        ImageProcessor skeleton = BinaryImages.skeleton(data.labelMap.getProcessor());

        String newName = data.labelMap.getShortTitle() + "-skeleton";
        return new ImagePlus(newName, skeleton);
    }
}
