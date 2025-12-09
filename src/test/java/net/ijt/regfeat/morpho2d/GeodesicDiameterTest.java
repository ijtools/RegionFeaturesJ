/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import static org.junit.Assert.*;

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
public class GeodesicDiameterTest
{

    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.GeodesicDiameter#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void test_compute()
    {
        ImagePlus labelMap = createImagePlus();
        RegionFeatures data = new RegionFeatures(labelMap, LabelImages.findAllLabels(labelMap));
        
        GeodesicDiameter feature = new GeodesicDiameter();
        double[] res = (double[]) feature.compute(data);
        
        assertEquals(res.length, 4);
        assertEquals(res[0], 1.0, 0.1);
        assertEquals(res[1], 4.0, 0.1);
        assertEquals(res[2], 4.0, 0.1);
        assertEquals(res[3], 5.2, 0.1);
    }

    /**
     * Test method for {@link net.ijt.regfeat.SingleValueFeature#updateTable(ij.measure.ResultsTable, net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void test_updateTable()
    {
        ImagePlus labelMap = createImagePlus();
        RegionFeatures data = RegionFeatures.initialize(labelMap)
                .add(GeodesicDiameter.class)
                .computeAll();

        GeodesicDiameter feature = new GeodesicDiameter();
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
