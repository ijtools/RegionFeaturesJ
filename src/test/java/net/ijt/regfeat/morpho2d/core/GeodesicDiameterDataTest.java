/**
 * 
 */
package net.ijt.regfeat.morpho2d.core;

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
public class GeodesicDiameterDataTest
{

    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.core.GeodesicDiameterData#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void testCompute()
    {
        ImagePlus labelMap = createImagePlus();
        RegionFeatures data = new RegionFeatures(labelMap, LabelImages.findAllLabels(labelMap));
        GeodesicDiameterData feature = new GeodesicDiameterData();

        Object[] res = (Object[]) feature.compute(data);
        
        assertEquals(res.length, 4);
    }
    
    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.core.GeodesicDiameterData#updateTable(ij.measure.ResultsTable, java.lang.Object)}.
     */
    @Test
    public final void testUpdateTable()
    {
        ImagePlus labelMap = createImagePlus();
        RegionFeatures data = RegionFeatures.initialize(labelMap);
        data.process(GeodesicDiameterData.class);
        
        GeodesicDiameterData feature = new GeodesicDiameterData();
        ResultsTable table = new ResultsTable();
        feature.updateTable(table, data);
        
        assertEquals(4, table.getCounter());
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
