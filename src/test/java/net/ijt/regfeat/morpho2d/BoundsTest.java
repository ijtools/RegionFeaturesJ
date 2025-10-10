/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import inra.ijpb.geometry.Box2D;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.OverlayFeature;
import net.ijt.regfeat.RegionFeatures;

/**
 * 
 */
public class BoundsTest
{

    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.Bounds#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void testCompute()
    {
        ImagePlus labelMap = createImagePlus();
        RegionFeatures data = new RegionFeatures(labelMap, LabelImages.findAllLabels(labelMap));
        
        Bounds feature = new Bounds();
        Box2D[] res = (Box2D[]) feature.compute(data);
        
        Box2D res0 = res[0];
        assertEquals(res0.getXMin(), 1.0, 0.01);
        assertEquals(res0.getYMin(), 1.0, 0.01);
        assertEquals(res0.getXMax(), 2.0, 0.01);
        assertEquals(res0.getYMax(), 2.0, 0.01);
        Box2D res3 = res[3];
        assertEquals(res3.getXMin(), 3.0, 0.01);
        assertEquals(res3.getYMin(), 3.0, 0.01);
        assertEquals(res3.getXMax(), 7.0, 0.01);
        assertEquals(res3.getYMax(), 7.0, 0.01);
    }
    
    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.Bounds#updateTable(ij.measure.ResultsTable, java.lang.Object)}.
     */
    @Test
    public final void test_updateTable()
    {
        ImagePlus labelMap = createImagePlus();
        RegionFeatures data = RegionFeatures.initialize(labelMap)
                .add(Bounds.class)
                .computeAll();

        ResultsTable table = new ResultsTable();
        Bounds feature = new Bounds();
        feature.updateTable(table, data);
        
        assertEquals(4, table.getCounter());
        assertEquals(3, table.getLastColumn());
    }
    
    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.Bounds#overlayResult(ij.ImagePlus, java.lang.Object)}.
     */
    @Test
    public final void test_overlayResult()
    {
        ImagePlus labelMap = createImagePlus();
        RegionFeatures data = RegionFeatures.initialize(labelMap)
                .add(Bounds.class)
                .computeAll();
        
        labelMap.show();
        OverlayFeature feature = (OverlayFeature) data.getFeature(Bounds.class);
        
        // check only that there is no bug
        feature.overlayResult(labelMap, data, 1.5);
    }

    private static final ImagePlus createImagePlus()
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
}
