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
     * Creates a label map containing four regions with labels 3, 5, 8 and 9.
     * 
     * @return a label map with four regions.
     */
    public static final ImagePlus createLabeMap_FourRegions_7x7()
    {
        ImageProcessor array = new ByteProcessor(8, 8);
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
        ImageProcessor array = new ByteProcessor(8, 8);
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                array.set(i, j, j * 10 + i);
            }
        }
        return new ImagePlus("values", array);
    }

}
