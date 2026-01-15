/**
 * 
 */
package net.ijt.regfeat.morpho3d.core;

import static org.junit.Assert.*;

import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Test;

import ij.ImagePlus;
import ij.ImageStack;
import inra.ijpb.data.image.ImageUtils;
import net.ijt.regfeat.RegionFeatures;

/**
 * 
 */
public class BoundaryFacesMidPointsTest
{
    /**
     * Test method for {@link net.ijt.regfeat.morpho3d.core.BoundaryFacesMidPoints#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void test_compute_singleVoxel()
    {
        ImageStack array = ImageStack.create(3, 3, 3, 8);
        array.setVoxel(1, 1, 1, 7);
        ImagePlus labelMap = new ImagePlus("labels", array);
        
        RegionFeatures data = RegionFeatures.initialize(labelMap);
        
        BoundaryFacesMidPoints algo = new BoundaryFacesMidPoints(); 
        TreeMap<Double, TreeMap<Double, TreeSet<Double>>>[] result = algo.compute(data);
        
        assertEquals(1, result.length);
        TreeMap<Double, TreeMap<Double, TreeSet<Double>>> res0 = result[0];
        assertEquals(3, res0.size());
        assertEquals(1, res0.get(1.0).size());
        assertEquals(3, res0.get(1.5).size());
        assertEquals(1, res0.get(2.0).size());
    }

    /**
     * Test method for {@link net.ijt.regfeat.morpho3d.core.BoundaryFacesMidPoints#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void test_compute_fillImage3x3x3()
    {
        ImageStack array = ImageStack.create(3, 3, 3, 8);
        ImageUtils.fill(array, (x,y,z) -> 7.0);
        ImagePlus labelMap = new ImagePlus("labels", array);
        
        RegionFeatures data = RegionFeatures.initialize(labelMap);
        
        BoundaryFacesMidPoints algo = new BoundaryFacesMidPoints(); 
        TreeMap<Double, TreeMap<Double, TreeSet<Double>>>[] result = algo.compute(data);
        
        assertEquals(1, result.length);
        TreeMap<Double, TreeMap<Double, TreeSet<Double>>> res0 = result[0];
        // region with 3 slices -> 3+2 entries
        assertEquals(5, res0.size());
        // for extremity slices, we have y-rows for voxel centers -> 3 entries
        assertEquals(3, res0.get(0.0).size());
        assertEquals(3, res0.get(3.0).size());
        // for slices corresponding to voxel centers, we have y-rows for voxel corners and centers -> 3+2 entries
        assertEquals(5, res0.get(0.5).size());
        assertEquals(5, res0.get(1.5).size());
        assertEquals(5, res0.get(2.5).size());
    }

}
