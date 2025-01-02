/**
 * 
 */
package net.ijt.regfeat.intensity;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

/**
 * A collection of simple test images.
 */
public class TestImages
{
    /**
     * Creates a label map containing four regions with labels 3, 5, 8 and 9,
     * within a 7-by-7 image.
     * 
     * Regions are composed of 1, 3, 3, and 9 pixels respectively.
     * 
     * @return a label map with four regions.
     */
    public static final ImagePlus createLabeMap_FourRegions_7x7()
    {
        ImageProcessor array = new ByteProcessor(7, 7);
        // create single-pixel region
        array.set(1, 1, 3);
        // create regions with three pixels, horizontal and vertical
        for (int i = 3; i < 6; i++)
        {
            array.set(i, 1, 5);
            array.set(1, i, 8);
        }
        // create 3-by-3 region
        for (int i = 3; i < 6; i++)
        {
            for (int j = 3; j < 6; j++)
            {
                array.set(i, j, 9);
            }
        }
        return new ImagePlus("labels", array);
    }
    
    /**
     * Creates a label map containing four regions with labels 3, 5, 8 and 9,
     * within a 9-by-9 image.
     * 
     * Regions are composed of 1, 5, 5, and 16 pixels respectively.
     * 
     * @return a label map with four regions.
     */
    public static final ImagePlus createLabeMap_FourRegions_9x9()
    {
        ImageProcessor array = new ByteProcessor(9, 9);
        // create single-pixel region
        array.set(1, 1, 3);
        // create regions with three pixels, horizontal and vertical
        for (int i = 3; i < 8; i++)
        {
            array.set(i, 1, 5);
            array.set(1, i, 8);
        }
        // create 3-by-3 region
        for (int i = 3; i < 8; i++)
        {
            for (int j = 3; j < 8; j++)
            {
                array.set(i, j, 9);
            }
        }
        return new ImagePlus("labels", array);
    }
    
    /**
     * Creates an intensity image with formula: 
     * <code>image(x,y) = y * 10 + x</code>.
     * 
     * This results in following values per region:
     * <ul>
     * <li>label 3: 11</li>
     * <li>label 5: 13, 14, 15</li>
     * <li>label 8: 31, 41, 51</li>
     * <li>label 9: 33, 34, 35, 43, 44, 45, 53, 54, 55</li>
     * </ul>
     * 
     * @return an image of intensities to be used with label map.
     */
    public static final ImagePlus createIntensityImage_FourRegions_7x7()
    {
        ImageProcessor array = new ByteProcessor(7, 7);
        for (int i = 0; i < 7; i++)
        {
            for (int j = 0; j < 7; j++)
            {
                array.set(i, j, j * 10 + i);
            }
        }
        return new ImagePlus("values", array);
    }

    /**
     * Creates an intensity image with formula:
     * <code>image(x,y) = y * 10 + x</code>.
     * 
     * This results in following values per region:
     * <ul>
     * <li>label 3: 11</li>
     * <li>label 5: 13, 14, 15, 16, 17</li>
     * <li>label 8: 31, 41, 51, 61, 71</li>
     * <li>label 9: 33, 34, 35, 36, 37, 43, 44, 45, 46, 47, 53, 54, 55, 56, 57,
     * 63, 64, 65, 66, 67, 73, 74, 75, 76, 77</li>
     * </ul>
     * 
     * @return an image of intensities to be used with label map.
     */
    public static final ImagePlus createIntensityImage_FourRegions_9x9()
    {
        ImageProcessor array = new ByteProcessor(9, 9);
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                array.set(i, j, j * 10 + i);
            }
        }
        return new ImagePlus("values", array);
    }
}
