/**
 * 
 */
package net.ijt.regfeat;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import net.ijt.regfeat.morpho2d.Area;
import net.ijt.regfeat.morpho2d.Circularity;
import net.ijt.regfeat.morpho2d.Perimeter;

/**
 * 
 */
public class RegionFeaturesTest
{
    /**
     * Test method for {@link net.ijt.regfeat.RegionFeatures#createTable()}.
     */
    @Test
    public final void testCreateTable()
    {
        ImagePlus labelMap = createImagePlus();
        
        ResultsTable table = RegionFeatures.initialize(labelMap)
                .add(Circularity.class)
                .add(Area.class)
                .add(Perimeter.class)
                .createTable();
                
        assertEquals(4, table.getCounter());
        assertEquals(2, table.getLastColumn());
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
