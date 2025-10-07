/**
 * 
 */
package net.ijt.regfeat.plugins;

import java.awt.Color;
import java.util.HashMap;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.PlugIn;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.RegionFeatures;

/**
 * Convert label map image into a RGB image, by using the color table stored in the RegionFeatures data.
 */
public class CreateLabelMapColorImage implements PlugIn
{
    /**
     * Default empty constructor.
     */
    public CreateLabelMapColorImage()
    {
    }

    @Override
    public void run(String arg)
    {
        ImagePlus imagePlus = IJ.getImage();
        
        RegionFeatures features = RegionFeatures.initialize(imagePlus);
        
        ImagePlus resultPlus = labelToRgb(imagePlus, features);
        
        resultPlus.show();
    }
    
    private static final ImagePlus labelToRgb(ImagePlus imagePlus, RegionFeatures features) 
    {
        ImagePlus resultPlus;
        String newName = imagePlus.getShortTitle() + "-rgb";
        
        // Dispatch to appropriate function depending on dimension
        if (imagePlus.getStackSize() == 1)
        {
            // process planar image
            ImageProcessor image = imagePlus.getProcessor();
            ImageProcessor result = labelToRgb(image, features);
            resultPlus = new ImagePlus(newName, result);
        } 
        else 
        {
            // process image stack
            ImageStack image = imagePlus.getStack();
            ImageStack result = labelToRgb(image, features);
            resultPlus = new ImagePlus(newName, result);
        }
        
        resultPlus.copyScale(imagePlus);
        return resultPlus;
    }
    
    private static final ColorProcessor labelToRgb(ImageProcessor image, RegionFeatures features) 
    {
        // retrieve label map features settings
        int[] labels = features.labels;
        Color[] labelColors = features.labelColors;
        
        // create associative array to know index of each label
        HashMap<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(labels);

        int bgColorCode = Color.BLACK.getRGB();
        
        // retrieve image size
        int width = image.getWidth();
        int height = image.getHeight();
        
        // create result image array
        ColorProcessor result = new ColorProcessor(width, height);
        
        // iterate over pixels
        for (int y = 0; y < height; y++) 
        {
            for (int x = 0; x < width; x++) 
            {
                int label = (int) image.getf(x, y);
                if (label == 0 || !labelIndices.containsKey(label)) 
                {
                    result.set(x, y, bgColorCode);
                } 
                else 
                {
                    int index = labelIndices.get(label);
                    int color = labelColors[index].getRGB();
                    result.set(x, y, color);
                }
            }
        }
        
        return result;
    }

//    private static final ColorProcessor labelToRgb(ImageProcessor image, int[] labels, Color[] labelColors) 
//    {
//        int width = image.getWidth();
//        int height = image.getHeight();
//        
//        // create associative array to know index of each label
//        HashMap<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(labels);
//
//        int bgColorCode = Color.BLACK.getRGB();
//        
//        ColorProcessor result = new ColorProcessor(width, height);
//        for (int y = 0; y < height; y++) 
//        {
//            for (int x = 0; x < width; x++) 
//            {
//                int label = (int) image.getf(x, y);
//                if (label == 0 || !labelIndices.containsKey(label)) 
//                {
//                    result.set(x, y, bgColorCode);
//                } 
//                else 
//                {
//                    int index = labelIndices.get(label);
//                    int color = labelColors[index].getRGB();
//                    result.set(x, y, color);
//                }
//            }
//        }
//        
//        return result;
//    }

    private static final ImageStack labelToRgb(ImageStack image, RegionFeatures features) 
    {
        // retrieve label map features settings
        int[] labels = features.labels;
        Color[] labelColors = features.labelColors;
        
        // create associative array to know index of each label
        HashMap<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(labels);

        int bgColorCode = Color.BLACK.getRGB();
        
        // retrieve image size
        int width = image.getWidth();
        int height = image.getHeight();
        int depth = image.getSize();
        
        // create result image array
        ImageStack result = ImageStack.create(width, height, depth, 24);
        
        // iterate over voxels
        for (int z = 0; z < depth; z++) 
        {
            for (int y = 0; y < height; y++) 
            {
                for (int x = 0; x < width; x++) 
                {
                    int label = (int) image.getVoxel(x, y, z);
                    if (label == 0 || !labelIndices.containsKey(label)) 
                    {
                        result.setVoxel(x, y, z, bgColorCode);
                    } 
                    else 
                    {
                        int index = labelIndices.get(label);
                        int color = labelColors[index].getRGB();
                        result.setVoxel(x, y, z, color);
                    }
                }
            }
        }
        
        return result;
    }
    
//    private static final ImageStack labelToRgb(ImageStack image, int[] labels, Color[] labelColors) 
//    {
//        int width = image.getWidth();
//        int height = image.getHeight();
//        int depth = image.getSize();
//        
//        // create associative array to know index of each label
//        HashMap<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(labels);
//
//        int bgColorCode = Color.BLACK.getRGB();
//        
//        ImageStack result = ImageStack.create(width, height, depth, 24);
//        for (int z = 0; z < depth; z++) 
//        {
//            for (int y = 0; y < height; y++) 
//            {
//                for (int x = 0; x < width; x++) 
//                {
//                    int label = (int) image.getVoxel(x, y, z);
//                    if (label == 0 || !labelIndices.containsKey(label)) 
//                    {
//                        result.setVoxel(x, y, z, bgColorCode);
//                    } 
//                    else 
//                    {
//                        int index = labelIndices.get(label);
//                        int color = labelColors[index].getRGB();
//                        result.setVoxel(x, y, z, color);
//                    }
//                }
//            }
//        }
//        
//        return result;
//    }
}
