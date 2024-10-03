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
import inra.ijpb.geometry.OrientedBox2D;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.RegionFeatures;

/**
 * 
 */
public class OrientedBoundingBoxTest
{

    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.OrientedBoundingBox#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void testCompute()
    {
        ImagePlus labelMap = createImagePlus();
        RegionFeatures data = new RegionFeatures(labelMap, LabelImages.findAllLabels(labelMap));
        OrientedBoundingBox feature = new OrientedBoundingBox();

        OrientedBox2D[] res = (OrientedBox2D[]) feature.compute(data);
        
        assertEquals(res[0].center().getX(), 1.5, 0.01);
        assertEquals(res[0].center().getY(), 1.5, 0.01);
        assertEquals(res[3].center().getX(), 5.0, 0.01);
        assertEquals(res[3].center().getY(), 5.0, 0.01);
    }
    
    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.Centroid#updateTable(ij.measure.ResultsTable, java.lang.Object)}.
     */
    @Test
    public final void testUpdateTable()
    {
        ImagePlus labelMap = createImagePlus();
        RegionFeatures data = RegionFeatures.initialize(labelMap)
                .add(OrientedBoundingBox.class)
                .computeAll();
        
        OrientedBoundingBox feature = new OrientedBoundingBox();
        ResultsTable table = new ResultsTable();
        feature.updateTable(table, data);
        
        assertEquals(4, table.getCounter());
        assertEquals(4, table.getLastColumn());
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
