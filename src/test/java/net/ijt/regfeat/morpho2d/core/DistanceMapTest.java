/**
 * 
 */
package net.ijt.regfeat.morpho2d.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import inra.ijpb.data.image.ImageUtils;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.RegionFeatures;

/**
 * 
 */
public class DistanceMapTest
{
    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.core.DistanceMap#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void test_compute_fourRegions()
    {
        ImageProcessor array = new ByteProcessor(15, 15);
        ImageUtils.fillRect(array, 1, 1, 3, 3, 3);
        ImageUtils.fillRect(array, 5, 1, 9, 3, 4);
        ImageUtils.fillRect(array, 1, 5, 3, 9, 5);
        ImageUtils.fillRect(array, 5, 5, 9, 9, 7);
        
        ImagePlus labelMap = new ImagePlus("labels", array);
        RegionFeatures data = new RegionFeatures(labelMap, LabelImages.findAllLabels(labelMap));

        DistanceMap feature = new DistanceMap();
        data.ensureRequiredFeaturesAreComputed(feature);
        ImagePlus res = feature.compute(data);
                
        assertEquals(labelMap.getWidth(), res.getWidth());
        assertEquals(labelMap.getHeight(), res.getHeight());
        ImageProcessor distMap = res.getProcessor();
        assertEquals(distMap.getf(2, 2),  2.0, 0.01);
        assertEquals(distMap.getf(9, 2),  2.0, 0.01);
        assertEquals(distMap.getf(2, 9),  2.0, 0.01);
        assertEquals(distMap.getf(9, 9),  5.0, 0.01);
    }

    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.core.DistanceMap#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void test_compute_adjacentRegions()
    {
        ImageProcessor array = new ByteProcessor(10, 10);
        ImageUtils.fillRect(array, 0, 0, 5, 5, 3);
        ImageUtils.fillRect(array, 5, 0, 5, 5, 4);
        ImageUtils.fillRect(array, 0, 5, 5, 5, 5);
        ImageUtils.fillRect(array, 5, 5, 5, 5, 7);
        
        ImagePlus labelMap = new ImagePlus("labels", array);
        RegionFeatures data = new RegionFeatures(labelMap, LabelImages.findAllLabels(labelMap));

        DistanceMap feature = new DistanceMap();
        data.ensureRequiredFeaturesAreComputed(feature);
        ImagePlus res = feature.compute(data);
                
        assertEquals(labelMap.getWidth(), res.getWidth());
        assertEquals(labelMap.getHeight(), res.getHeight());
        ImageProcessor distMap = res.getProcessor();
        assertEquals(distMap.getf(2, 2),  3.0, 0.01);
        assertEquals(distMap.getf(7, 2),  3.0, 0.01);
        assertEquals(distMap.getf(2, 7),  3.0, 0.01);
        assertEquals(distMap.getf(7, 7),  3.0, 0.01);
    }

}
