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
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.RegionFeatures;

/**
 * 
 */
public class MaxFeretDiameterTest
{
    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.Centroid#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void testCompute()
    {
        ImagePlus labelMap = createImagePlus();
        RegionFeatures data = new RegionFeatures(labelMap, LabelImages.findAllLabels(labelMap));
   
        MaxFeretDiameter feature = new MaxFeretDiameter();
        double[] diameters = (double[]) feature.compute(data);
        
        assertEquals(diameters.length, 4);
        assertEquals(diameters[0], 1.0, 0.01);
        assertEquals(diameters[1], 4.0, 0.01);
        assertEquals(diameters[2], 4.0, 0.01);
        assertEquals(diameters[3], 5.0, 0.1);
    }
    
    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.Centroid#updateTable(ij.measure.ResultsTable, java.lang.Object)}.
     */
    @Test
    public final void testPopulateTable()
    {
        ImagePlus labelMap = createImagePlus();
        RegionFeatures data = RegionFeatures.initialize(labelMap)
                .add(MaxFeretDiameter.class)
                .computeAll();

        MaxFeretDiameter feature = new MaxFeretDiameter();
        ResultsTable table = new ResultsTable();
        feature.updateTable(table, data);
        
        assertEquals(4, table.getCounter());
        assertEquals(0, table.getLastColumn());
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
