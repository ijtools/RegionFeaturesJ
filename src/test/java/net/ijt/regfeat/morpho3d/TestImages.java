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
     * Regions are composed of 1, 5, 25 or 125 voxels, depending on their location.
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
     * Creates a label map containing eight regions with labels 3, 5, 8, 9, 10, 13, 14 and 15,
     * within a 6x6x6 image, such that regions touch borders and touch each other.
     * 
     * Regions are composed of 1, 5, 25 or 125 voxels, depending on their location.
     * 
     * @return a label map with eight regions.
     */
    public static final ImagePlus createImage_eightCuboids_touchBorders()
    {
        ImageStack image = ImageStack.create(6, 6, 6, 8);
        
        // one single-voxel region
        image.setVoxel(0, 0, 0, 3);
        
        // three 1x1x5 regions
        for (int i = 1; i < 6; i++)
        {
            image.setVoxel(i, 0, 0, 5);
            image.setVoxel(0, i, 0, 8);
            image.setVoxel(0, 0, i, 9);
        }

        
        // three 1x5x5 regions
        for (int i = 1; i < 6; i++)
        {
            for (int j = 1; j < 6; j++)
            {
                image.setVoxel(i, j, 0, 10);
                image.setVoxel(i, 0, j, 13);
                image.setVoxel(0, i, j, 14);
            }
        }

        // one 5x5x5 region
        for (int i = 1; i < 6; i++)
        {
            for (int j = 1; j < 6; j++)
            {
                for (int k = 1; k < 6; k++)
                {
                    image.setVoxel(i, j, k, 15);
                }
            }
        }
        
        return new ImagePlus("labels", image);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private TestImages()
    {
    }
}
