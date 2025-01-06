package net.ijt.regfeat.morpho3d;

import ij.ImagePlus;
import ij.ImageStack;

/**
 * A collection of images useful for tests.
 */
public class TestImages
{
    /**
     * Creates a label map containing eight regions with labels 3, 5, 8, 9, 10, 13, 14 and 15,
     * within a 9-by-9-by-9 image.
     * 
     * Regions are composed of 1, 5, 25 or 125 pixels, depending on their location.
     * 
     * @return a label map with eight regions.
     */
    public static final ImagePlus createLabeMap_EightRegions_9x9x9()
    {
        ImageStack array = ImageStack.create(9, 9, 9, 8);
        
        // create single-voxel region
        array.setVoxel(1, 1, 1, 3);
        
        // create regions with five voxels, in the three main directions
        for (int i = 3; i < 8; i++)
        {
            array.setVoxel(i, 1, 1, 5);
            array.setVoxel(1, i, 1, 8);
            array.setVoxel(1, 1, i, 10);
        }
        
        // create 5-by-5 regions, in the three main planes
        for (int i = 3; i < 8; i++)
        {
            for (int j = 3; j < 8; j++)
            {
                array.setVoxel(i, j, 1, 9);
                array.setVoxel(i, 1, j, 13);
                array.setVoxel(1, i, j, 14);
            }
        }
        
        // create cubic region with side five voxels
        for (int i = 3; i < 8; i++)
        {
            for (int j = 3; j < 8; j++)
            {
                for (int k = 3; k < 8; k++)
                {
                    array.setVoxel(i, j, k, 15);
                }            
            }
        }
        
        return new ImagePlus("labels", array);
    }
    

    /**
     * Private constructor to prevent instantiation.
     */
    private TestImages()
    {
    }
}
