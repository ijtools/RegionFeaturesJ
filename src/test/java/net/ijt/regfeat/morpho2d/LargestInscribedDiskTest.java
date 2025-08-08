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
import inra.ijpb.geometry.Circle2D;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.RegionFeatures;

/**
 * 
 */
public class LargestInscribedDiskTest
{

    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.LargestInscribedDisk#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void test_compute()
    {
        ImagePlus labelMap = createImagePlus();
        RegionFeatures data = new RegionFeatures(labelMap, LabelImages.findAllLabels(labelMap));
        
        LargestInscribedDisk feature = new LargestInscribedDisk();
        Circle2D[] res = (Circle2D[]) feature.compute(data);

        Circle2D res0 = res[0];
        assertEquals(1.0, res0.getCenter().getX(), 0.01);
        assertEquals(1.0, res0.getCenter().getY(), 0.01);
        assertEquals(1.0, res0.getRadius(), 0.01);

        Circle2D res1 = res[1];
//        assertEquals(1.0, res1.getCenter().getX(), 0.01);
        assertEquals(1.0, res1.getCenter().getY(), 0.01);
        assertEquals(1.0, res1.getRadius(), 0.01);

        Circle2D res2 = res[2];
        assertEquals(1.0, res2.getCenter().getX(), 0.01);
//        assertEquals(1.0, res2.getCenter().getY(), 0.01);
        assertEquals(1.0, res2.getRadius(), 0.01);

        Circle2D res3 = res[3];
        assertEquals(5.0, res3.getCenter().getX(), 0.01);
        assertEquals(5.0, res3.getCenter().getY(), 0.01);
        assertEquals(3.0, res3.getRadius(), 0.01);
    }

    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.LargestInscribedDisk#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void test_compute_missingLabels()
    {
        ImagePlus labelMap = createImagePlus();
        // label map contains labels 3, 5, 8, 9
        RegionFeatures data = new RegionFeatures(labelMap, new int[] {3, 4, 9});
        
        LargestInscribedDisk feature = new LargestInscribedDisk();
        Circle2D[] res = (Circle2D[]) feature.compute(data);
        
        assertEquals(3, res.length);

        Circle2D res0 = res[0];
        assertEquals(1.0, res0.getCenter().getX(), 0.01);
        assertEquals(1.0, res0.getCenter().getY(), 0.01);
        assertEquals(1.0, res0.getRadius(), 0.01);

        Circle2D res2 = res[2];
        assertEquals(5.0, res2.getCenter().getX(), 0.01);
        assertEquals(5.0, res2.getCenter().getY(), 0.01);
        assertEquals(3.0, res2.getRadius(), 0.01);
    }

    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.LargestInscribedDisk#updateTable(ij.measure.ResultsTable, net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void testUpdateTable()
    {
        ImagePlus labelMap = createImagePlus();
        RegionFeatures data = RegionFeatures.initialize(labelMap)
                .add(LargestInscribedDisk.class)
                .computeAll();

        ResultsTable table = new ResultsTable();
        LargestInscribedDisk feature = new LargestInscribedDisk();
        feature.updateTable(table, data);
        
        assertEquals(4, table.getCounter());
        assertEquals(2, table.getLastColumn());
    }
    
    private static final ImagePlus createImagePlus()
    {
        ImageProcessor array = new ByteProcessor(9, 9);
        array.set(1, 1, 3);
        for (int i = 3; i < 8; i++)
        {
            array.set(i, 1, 5);
            array.set(1, i, 8);
        }
        for (int i = 3; i < 8; i++)
        {
            for (int j = 3; j < 8; j++)
            {
                array.set(i, j, 9);
            }
        }
        return new ImagePlus("labels", array);
    }
}
