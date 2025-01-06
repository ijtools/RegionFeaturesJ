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
public class EulerNumberTest
{

    /**
     * Test method for {@link net.ijt.regfeat.morpho3d.Volume#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void testCompute()
    {
        ImagePlus labelMap = TestImages.createLabeMap_EightRegions_9x9x9();
        
        RegionFeatures data = new RegionFeatures(labelMap, LabelImages.findAllLabels(labelMap));
        
        EulerNumber feature = new EulerNumber();
        double[] res = (double[]) feature.compute(data);
                
        assertEquals(res.length, 8);
        assertEquals(res[0],   1.0, 0.01);
        assertEquals(res[1],   1.0, 0.01);
        assertEquals(res[2],   1.0, 0.01);
        assertEquals(res[3],   1.0, 0.01);
        assertEquals(res[4],   1.0, 0.01);
        assertEquals(res[5],   1.0, 0.01);
        assertEquals(res[6],   1.0, 0.01);
        assertEquals(res[7],   1.0, 0.01);
    }

    /**
     * Test method for {@link net.ijt.regfeat.SingleValueFeature#updateTable(ij.measure.ResultsTable, net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void testUpdateTable()
    {
        ImagePlus labelMap = TestImages.createLabeMap_EightRegions_9x9x9();
        
        ResultsTable table = RegionFeatures.initialize(labelMap)
                .add(EulerNumber.class)
                .createTable();
        
        assertEquals(8, table.getCounter());
        assertTrue("Euler_Number".equals(table.getColumnHeading(0)));
        assertEquals(0, table.getLastColumn());
    }

}
