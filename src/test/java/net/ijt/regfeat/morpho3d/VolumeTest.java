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
public class VolumeTest
{

    /**
     * Test method for {@link net.ijt.regfeat.morpho3d.Volume#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void testCompute()
    {
        ImagePlus labelMap = TestImages.createLabeMap_EightRegions_9x9x9();
        
        RegionFeatures data = new RegionFeatures(labelMap, LabelImages.findAllLabels(labelMap));
        
        Volume feature = new Volume();
        double[] res = (double[]) feature.compute(data);
                
        assertEquals(res.length, 8);
        assertEquals(res[0],   1.0, 0.01);
        assertEquals(res[1],   5.0, 0.01);
        assertEquals(res[2],   5.0, 0.01);
        assertEquals(res[3],  25.0, 0.01);
        assertEquals(res[4],   5.0, 0.01);
        assertEquals(res[5],  25.0, 0.01);
        assertEquals(res[6],  25.0, 0.01);
        assertEquals(res[7], 125.0, 0.01);
    }

    /**
     * Test method for {@link net.ijt.regfeat.SingleValueFeature#updateTable(ij.measure.ResultsTable, net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void testUpdateTable()
    {
        ImagePlus labelMap = TestImages.createLabeMap_EightRegions_9x9x9();
        
        ResultsTable table = RegionFeatures.initialize(labelMap)
                .add(Volume.class)
                .createTable();
        
        assertEquals(8, table.getCounter());
        assertTrue("Volume".equals(table.getColumnHeading(0)));
        assertEquals(0, table.getLastColumn());
    }

}
