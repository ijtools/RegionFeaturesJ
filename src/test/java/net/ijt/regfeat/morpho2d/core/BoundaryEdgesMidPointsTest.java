/**
 * 
 */
package net.ijt.regfeat.morpho2d.core;

import static org.junit.Assert.assertEquals;

import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Test;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import net.ijt.regfeat.RegionFeatures;

/**
 * 
 */
public class BoundaryEdgesMidPointsTest
{
    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.core.BoundaryEdgesMidPoints#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void test_compute_singleVoxel()
    {
        ByteProcessor image = new ByteProcessor(3, 3);
        image.set(1, 1, 7);
        ImagePlus labelMap = new ImagePlus("labels", image);
        
        RegionFeatures data = RegionFeatures.initialize(labelMap);
        
        BoundaryEdgesMidPoints algo = new BoundaryEdgesMidPoints(); 
        TreeMap<Double, TreeSet<Double>>[] result = algo.compute(data);
        
        assertEquals(1, result.length);
        TreeMap<Double, TreeSet<Double>> res0 = result[0];
        assertEquals(3, res0.size());
        assertEquals(1, res0.get(1.0).size());
        assertEquals(2, res0.get(1.5).size());
        assertEquals(1, res0.get(2.0).size());
    }

    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.core.BoundaryEdgesMidPoints#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void test_compute_fillImage3x3()
    {
        ByteProcessor image = new ByteProcessor(3, 3);
        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 3; x++)
            {
                image.set(x, y, 7);
            }
        }
        ImagePlus labelMap = new ImagePlus("labels", image);
        
        RegionFeatures data = RegionFeatures.initialize(labelMap);
        
        BoundaryEdgesMidPoints algo = new BoundaryEdgesMidPoints(); 
        TreeMap<Double, TreeSet<Double>>[] result = algo.compute(data);
        
        assertEquals(1, result.length);
        TreeMap<Double, TreeSet<Double>> res0 = result[0];
        // region with 3 rows -> 3+2 entries
        assertEquals(5, res0.size());
        // for extremity rows, we have x-coords for pixel centers -> 3 entries
        assertEquals(3, res0.get(0.0).size());
        assertEquals(3, res0.get(3.0).size());
        // for regular rows, we have x-coords for extremities -> 2 entries 
        assertEquals(2, res0.get(0.5).size());
        assertEquals(2, res0.get(1.5).size());
        assertEquals(2, res0.get(2.5).size());
    }

}
