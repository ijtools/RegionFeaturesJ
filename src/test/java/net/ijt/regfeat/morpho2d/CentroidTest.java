/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import static org.junit.Assert.*;

import java.awt.geom.Point2D;

import org.junit.Test;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.RegionFeatures;

/**
 * 
 */
public class CentroidTest
{

    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.Centroid#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void testCompute()
    {
        ImagePlus labelMap = createImagePlus();
        RegionFeatures data = new RegionFeatures(labelMap, LabelImages.findAllLabels(labelMap));
        
        Centroid feature = new Centroid();
        Point2D[] res = (Point2D[]) feature.compute(data);

        Point2D res0 = res[0];
        assertEquals(res0.getX(), 1.5, 0.01);
        assertEquals(res0.getY(), 1.5, 0.01);

        Point2D res1 = res[1];
        assertEquals(res1.getX(), 5.0, 0.01);
        assertEquals(res1.getY(), 1.5, 0.01);

        Point2D res2 = res[2];
        assertEquals(res2.getX(), 1.5, 0.01);
        assertEquals(res2.getY(), 5.0, 0.01);

        Point2D res3 = res[3];
        assertEquals(res3.getX(), 5.0, 0.01);
        assertEquals(res3.getY(), 5.0, 0.01);
    }

    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.Centroid#updateTable(ij.measure.ResultsTable, java.lang.Object)}.
     */
    @Test
    public final void testPopulateTable()
    {
        ImagePlus labelMap = createImagePlus();
        RegionFeatures data = RegionFeatures.initialize(labelMap)
                .add(Centroid.class)
                .computeAll();

        ResultsTable table = new ResultsTable();
        Centroid feature = new Centroid();
        feature.updateTable(table, data);
        
        assertEquals(4, table.getCounter());
        assertEquals(1, table.getLastColumn());
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
