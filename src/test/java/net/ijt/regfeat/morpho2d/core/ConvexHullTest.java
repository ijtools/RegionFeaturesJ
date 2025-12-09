/**
 * 
 */
package net.ijt.regfeat.morpho2d.core;

import static org.junit.Assert.*;

import org.junit.Test;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import inra.ijpb.geometry.Polygon2D;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.RegionFeatures;

/**
 * 
 */
public class ConvexHullTest
{
    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.core.ConvexHull#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void test_compute_fourRect()
    {
        ImagePlus labelMap = createImagePlus_fourRect();
        RegionFeatures data = new RegionFeatures(labelMap, LabelImages.findAllLabels(labelMap));
        
        ConvexHull feature = new ConvexHull();
        Polygon2D[] res = (Polygon2D[]) feature.compute(data);
        
        assertEquals(4, res.length);
        assertEquals(4, res[0].vertexNumber());
        assertEquals(6, res[1].vertexNumber());
        assertEquals(6, res[2].vertexNumber());
        assertEquals(8, res[3].vertexNumber());
    }

    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.core.ConvexHull#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void test_compute_NonConvex()
    {
        ImagePlus labelMap = createImagePlus_nonConvexRegions();
        RegionFeatures data = new RegionFeatures(labelMap, LabelImages.findAllLabels(labelMap));
        
        ConvexHull feature = new ConvexHull();
        Polygon2D[] res = (Polygon2D[]) feature.compute(data);
        
        assertEquals(2, res.length);
        assertEquals(8, res[0].vertexNumber());
        assertEquals(16, res[1].vertexNumber());
    }

    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.core.ConvexHull#overlayResult(ij.ImagePlus, net.ijt.regfeat.RegionFeatures, double)}.
     */
    @Test
    public final void test_overlayResult()
    {
        ImagePlus labelMap = IJ.openImage(getClass().getResource("/grains-med-WTH-lbl.tif").getFile());
        RegionFeatures data = new RegionFeatures(labelMap, LabelImages.findAllLabels(labelMap));
        data.process(ConvexHull.class);
        
        ConvexHull feature = new ConvexHull();
        feature.overlayResult(labelMap, data, 1.5);
        labelMap.show();
    }

    private static final ImagePlus createImagePlus_fourRect()
    {
        ImageProcessor array = new ByteProcessor(8, 8);
        array.set(1, 1, 3);
        for (int i = 3; i < 7; i++)
        {
            array.set(i, 1, 5);
            array.set(1, i, 8);
        }
        for (int i = 3; i < 7; i++)
        {
            for (int j = 3; j < 7; j++)
            {
                array.set(i, j, 9);
            }
        }
        return new ImagePlus("labels", array);
    }
    
    private static final ImagePlus createImagePlus_nonConvexRegions()
    {
        ImageProcessor array = new ByteProcessor(10, 10);
        // first region with four isolated pixels -> 2*4=8 vertices
        array.set(1, 1, 3);
        array.set(5, 1, 3);
        array.set(1, 5, 3);
        array.set(5, 5, 3);
        
        // second region with 12 isolated pixels
        // -> 16 vertices
        array.set(1, 4, 7);
        array.set(2, 2, 7);
        array.set(4, 1, 7);
        array.set(6, 1, 7);
        array.set(8, 2, 7);
        array.set(9, 4, 7);
        array.set(1, 6, 7);
        array.set(2, 8, 7);
        array.set(4, 9, 7);
        array.set(6, 9, 7);
        array.set(8, 8, 7);
        array.set(9, 6, 7);
        return new ImagePlus("labels", array);
    }
}
