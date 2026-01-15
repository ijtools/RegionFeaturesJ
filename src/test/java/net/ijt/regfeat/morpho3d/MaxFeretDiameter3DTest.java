/**
 * 
 */
package net.ijt.regfeat.morpho3d;

import static org.junit.Assert.*;

import org.junit.Test;

import ij.ImagePlus;
import ij.ImageStack;
import net.ijt.regfeat.RegionFeatures;

/**
 * 
 */
public class MaxFeretDiameter3DTest
{
    /**
     * Test method for {@link net.ijt.regfeat.morpho3d.MaxFeretDiameter3D#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void test_compute_singleCube()
    {
        // create image containing a single 8-voxel side cube
        ImageStack stack = ImageStack.create(10, 10, 10, 8);
        for (int i = 1; i < 9; i++)
        {
            stack.setVoxel(i, i, i, 255);
        }
        ImagePlus labelMap = new ImagePlus("labelMap", stack);
        RegionFeatures data = RegionFeatures.initialize(labelMap);
        
        MaxFeretDiameter3D algo = new MaxFeretDiameter3D(); 
        double[] result = algo.compute(data);
        
        assertEquals(1, result.length);
        double diam = result[0];
        assertEquals(Math.hypot(Math.hypot(8, 7), 7), diam, 0.01);
    }

    /**
     * Test method for {@link net.ijt.regfeat.morpho3d.MaxFeretDiameter3D#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void test_compute_eightCuboids()
    {
        // create image containing eight cuboidal regions with various dimensions
        ImagePlus labelMap = TestImages.createLabeMap_EightRegions_9x9x9();
        RegionFeatures data = RegionFeatures.initialize(labelMap);
        
        MaxFeretDiameter3D algo = new MaxFeretDiameter3D(); 
        double[] diams = algo.compute(data);
        
        assertEquals(8, diams.length);
        assertEquals(1.0, diams[0], 0.01);
        assertEquals(5.0, diams[1], 0.01);
        assertEquals(5.0, diams[2], 0.01);
        assertEquals(5.0, diams[4], 0.01);
        double fd4 = Math.hypot(5, 4);
        assertEquals(fd4, diams[3], 0.01);
        assertEquals(fd4, diams[5], 0.01);
        assertEquals(fd4, diams[6], 0.01);
        double fd7 = Math.hypot(Math.hypot(5, 4), 4);
        assertEquals(fd7, diams[7], 0.01);
    }
}
