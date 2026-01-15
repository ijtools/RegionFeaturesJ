/**
 * 
 */
package net.ijt.regfeat.morpho3d;

import static org.junit.Assert.*;

import org.junit.Test;

import ij.ImagePlus;
import ij.ImageStack;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.RegionFeatures;

/**
 * 
 */
public class GeodesicDiameter3DTest
{
    /**
     * Test method for {@link net.ijt.regfeat.morpho3d.GeodesicDiameter3D#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void test_compute_eightCuboids()
    {
        ImagePlus labelMap = createImage_eightCuboids();
        RegionFeatures data = new RegionFeatures(labelMap, LabelImages.findAllLabels(labelMap));
        
        GeodesicDiameter3D feature = new GeodesicDiameter3D();
        double[] res = (double[]) feature.compute(data);
        
        assertEquals(res.length, 8);
        assertEquals(res[0], 1.0, 0.1);
        assertEquals(res[1], 5.0, 0.1);
        assertEquals(res[2], 5.0, 0.1);
        assertEquals(res[3], 5.0, 0.1);
        assertEquals(res[4], 6.33, 0.1);
        assertEquals(res[5], 6.33, 0.1);
        assertEquals(res[6], 6.33, 0.1);
        assertEquals(res[7], 7.67, 0.1);
    }

    /**
     * Test method for {@link net.ijt.regfeat.morpho3d.GeodesicDiameter3D#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void test_compute_eightCuboids_touchBorders()
    {
        ImagePlus labelMap = createImage_eightCuboids_touchBorders();
        RegionFeatures data = new RegionFeatures(labelMap, LabelImages.findAllLabels(labelMap));
        
        GeodesicDiameter3D feature = new GeodesicDiameter3D();
        double[] res = (double[]) feature.compute(data);
        
        assertEquals(res.length, 8);
        assertEquals(res[0], 1.0, 0.1);
        assertEquals(res[1], 5.0, 0.1);
        assertEquals(res[2], 5.0, 0.1);
        assertEquals(res[3], 5.0, 0.1);
        assertEquals(res[4], 6.33, 0.1);
        assertEquals(res[5], 6.33, 0.1);
        assertEquals(res[6], 6.33, 0.1);
        assertEquals(res[7], 7.67, 0.1);
    }

    private static final ImagePlus createImage_eightCuboids()
    {
        ImageStack image = ImageStack.create(8, 8, 8, 8);
        
        // one single-voxel region
        image.setVoxel(1, 1, 1, 3);
        
        // three 1x1x5 regions
        for (int i = 3; i < 8; i++)
        {
            image.setVoxel(i, 1, 1, 4);
            image.setVoxel(1, i, 1, 5);
            image.setVoxel(1, 1, i, 7);
        }

        
        // three 1x5x5 regions
        for (int i = 3; i < 8; i++)
        {
            for (int j = 3; j < 8; j++)
            {
                image.setVoxel(i, j, 1, 8);
                image.setVoxel(i, 1, j, 9);
                image.setVoxel(1, i, j, 11);
            }
        }

        // one 5x5x5 region
        for (int i = 3; i < 8; i++)
        {
            for (int j = 3; j < 8; j++)
            {
                for (int k = 3; k < 8; k++)
                {
                    image.setVoxel(i, j, k, 12);
                }
            }
        }
        
        return new ImagePlus("labelMap", image);
    }
    
    private static final ImagePlus createImage_eightCuboids_touchBorders()
    {
        ImageStack image = ImageStack.create(6, 6, 6, 8);
        
        // one single-voxel region
        image.setVoxel(0, 0, 0, 3);
        
        // three 1x1x5 regions
        for (int i = 1; i < 6; i++)
        {
            image.setVoxel(i, 0, 0, 4);
            image.setVoxel(0, i, 0, 5);
            image.setVoxel(0, 0, i, 7);
        }

        
        // three 1x5x5 regions
        for (int i = 1; i < 6; i++)
        {
            for (int j = 1; j < 6; j++)
            {
                image.setVoxel(i, j, 0, 8);
                image.setVoxel(i, 0, j, 9);
                image.setVoxel(0, i, j, 11);
            }
        }

        // one 5x5x5 region
        for (int i = 1; i < 6; i++)
        {
            for (int j = 1; j < 6; j++)
            {
                for (int k = 1; k < 6; k++)
                {
                    image.setVoxel(i, j, k, 12);
                }
            }
        }
        
        return new ImagePlus("labelMap", image);
    }
}
