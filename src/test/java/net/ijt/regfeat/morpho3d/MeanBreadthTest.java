/**
 * 
 */
package net.ijt.regfeat.morpho3d;

import static org.junit.Assert.*;

import org.junit.Test;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.RegionFeatures;

/**
 * 
 */
public class MeanBreadthTest
{

    /**
     * Test method for {@link net.ijt.regfeat.morpho3d.Volume#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void testCompute()
    {
        ImagePlus labelMap = TestImages.createLabeMap_EightRegions_9x9x9();
        
        RegionFeatures data = new RegionFeatures(labelMap, LabelImages.findAllLabels(labelMap));
        
        MeanBreadth feature = new MeanBreadth();
        double[] res = (double[]) feature.compute(data);
                
        assertEquals(res.length, 8);
        for (int i = 0; i < 8; i++)
        {
            assertTrue(res[i] > 0.0);
        }
    }

    /**
     * Test method for {@link net.ijt.regfeat.SingleValueFeature#updateTable(ij.measure.ResultsTable, net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void testUpdateTable()
    {
        ImagePlus labelMap = TestImages.createLabeMap_EightRegions_9x9x9();
        
        ResultsTable table = RegionFeatures.initialize(labelMap)
                .add(MeanBreadth.class)
                .createTable();
        
        assertEquals(8, table.getCounter());
        assertTrue("Mean_Breadth".equals(table.getColumnHeading(0)));
        assertEquals(0, table.getLastColumn());
    }

}
