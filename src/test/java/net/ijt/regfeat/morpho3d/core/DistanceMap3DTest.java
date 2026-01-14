/**
 * 
 */
package net.ijt.regfeat.morpho3d.core;

import static org.junit.Assert.*;

import org.junit.Test;

import ij.ImagePlus;
import ij.ImageStack;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.RegionFeatures;

/**
 * 
 */
public class DistanceMap3DTest
{
    /**
     * Test method for {@link net.ijt.regfeat.morpho3d.core.DistanceMap3D_Chamfer_Float_Svensson#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void test_compute_eightCubes()
    {
        // test with an image containing eight cuboids
        ImagePlus labelMap = createImage_eightCubes();
        RegionFeatures data = new RegionFeatures(labelMap, LabelImages.findAllLabels(labelMap));

        DistanceMap3D feature = new DistanceMap3D();
        data.ensureRequiredFeaturesAreComputed(feature);
        ImagePlus resPlus = feature.compute(data);
                
        ImageStack result = resPlus.getStack();
        assertEquals(32, result.getBitDepth());
        
        assertEquals(2, result.getVoxel(2, 2, 2), .1);
        assertEquals(2, result.getVoxel(5, 2, 2), .1);
        assertEquals(2, result.getVoxel(2, 5, 2), .1);
        assertEquals(2, result.getVoxel(5, 5, 2), .1);
        assertEquals(2, result.getVoxel(2, 2, 5), .1);
        assertEquals(2, result.getVoxel(5, 2, 5), .1);
        assertEquals(2, result.getVoxel(2, 5, 5), .1);
        assertEquals(2, result.getVoxel(5, 5, 5), .1);
        
        assertEquals(1, result.getVoxel(3, 3, 3), .1);
        assertEquals(1, result.getVoxel(4, 3, 3), .1);
        assertEquals(1, result.getVoxel(3, 4, 3), .1);
        assertEquals(1, result.getVoxel(4, 4, 3), .1);
        assertEquals(1, result.getVoxel(3, 3, 4), .1);
        assertEquals(1, result.getVoxel(4, 3, 4), .1);
        assertEquals(1, result.getVoxel(3, 4, 4), .1);
        assertEquals(1, result.getVoxel(4, 4, 4), .1);
    }
    
    /**
     * Test method for {@link net.ijt.regfeat.morpho3d.core.DistanceMap3D_Chamfer_Float_Svensson#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void test_compute_eightCubes_touchBorders()
    {
        // test with an image containing eight cuboids
        ImagePlus labelMap = createImage_eightCubes_touchBorder();
        RegionFeatures data = new RegionFeatures(labelMap, LabelImages.findAllLabels(labelMap));

        DistanceMap3D feature = new DistanceMap3D();
        data.ensureRequiredFeaturesAreComputed(feature);
        ImagePlus resPlus = feature.compute(data);
                
        ImageStack result = resPlus.getStack();
        assertEquals(32, result.getBitDepth());
        
        // center of each cube -> distance equals 3
        assertEquals(3, result.getVoxel(2, 2, 2), .1);
        assertEquals(3, result.getVoxel(7, 2, 2), .1);
        assertEquals(3, result.getVoxel(2, 7, 2), .1);
        assertEquals(3, result.getVoxel(7, 7, 2), .1);
        assertEquals(3, result.getVoxel(2, 2, 7), .1);
        assertEquals(3, result.getVoxel(7, 2, 7), .1);
        assertEquals(3, result.getVoxel(2, 7, 7), .1);
        assertEquals(3, result.getVoxel(7, 7, 7), .1);
        
        // some border voxels of first region
        assertEquals(5, result.getVoxel(0, 0, 0), .1);
        assertEquals(1, result.getVoxel(4, 2, 2), .1);
        assertEquals(1, result.getVoxel(2, 4, 2), .1);
        assertEquals(1, result.getVoxel(2, 2, 4), .1);
        assertEquals(1, result.getVoxel(4, 4, 4), .1);
        
        // some border voxels of last region
        assertEquals(1, result.getVoxel(5, 5, 5), .1);
        assertEquals(1, result.getVoxel(5, 7, 7), .1);
        assertEquals(1, result.getVoxel(7, 5, 7), .1);
        assertEquals(1, result.getVoxel(7, 7, 5), .1);
        assertEquals(5, result.getVoxel(9, 9, 9), .1);
    }
    
    private static final ImagePlus createImage_eightCubes()
    {
        // create 3D image containing a cube 
        ImageStack image = ImageStack.create(11, 11, 11, 8);
        for (int z = 0; z < 3; z++)
        {
            for (int y = 0; y < 3; y++)
            {
                for (int x = 0; x < 3; x++)
                {
                    image.setVoxel(x+1, y+1, z+1, 1);
                    image.setVoxel(x+4, y+1, z+1, 2);
                    image.setVoxel(x+1, y+4, z+1, 3);
                    image.setVoxel(x+4, y+4, z+1, 4);
                    image.setVoxel(x+1, y+1, z+4, 5);
                    image.setVoxel(x+4, y+1, z+4, 6);
                    image.setVoxel(x+1, y+4, z+4, 7);
                    image.setVoxel(x+4, y+4, z+4, 8);
                }
            }
        }
        return new ImagePlus("labelMap", image);
    }
    
    private static final ImagePlus createImage_eightCubes_touchBorder()
    {
        // create 3D image containing a cube 
        ImageStack image = ImageStack.create(10, 10, 10, 8);
        for (int z = 0; z < 5; z++)
        {
            for (int y = 0; y < 5; y++)
            {
                for (int x = 0; x < 5; x++)
                {
                    image.setVoxel(x+0, y+0, z+0, 1);
                    image.setVoxel(x+5, y+0, z+0, 2);
                    image.setVoxel(x+0, y+5, z+0, 3);
                    image.setVoxel(x+5, y+5, z+0, 4);
                    image.setVoxel(x+0, y+0, z+5, 5);
                    image.setVoxel(x+5, y+0, z+5, 6);
                    image.setVoxel(x+0, y+5, z+5, 7);
                    image.setVoxel(x+5, y+5, z+5, 8);
                }
            }
        }
        return new ImagePlus("labelMap", image);
    }
}
