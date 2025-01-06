/**
 * 
 */
package net.ijt.regfeat.morpho3d;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import net.ijt.regfeat.RegionFeatures;

/**
 * 
 */
public class EllipsoidElongationsTest
{
    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.EquivalentEllipse#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void testCompute()
    {
        ImagePlus labelMap = TestImages.createLabeMap_EightRegions_9x9x9();
        RegionFeatures data = RegionFeatures.initialize(labelMap);
        
        EllipsoidElongations feature = new EllipsoidElongations();
        double[][] res = (double[][]) feature.compute(data);
        
        assertEquals(res.length, 8);
        assertEquals(res[0][0], 1.0, 0.01);
        assertEquals(res[0][1], 1.0, 0.01);
        assertEquals(res[0][2], 1.0, 0.01);
    }
    
    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.Centroid#updateTable(ij.measure.ResultsTable, java.lang.Object)}.
     */
    @Test
    public final void testUpdateTable()
    {
        ImagePlus labelMap = TestImages.createLabeMap_EightRegions_9x9x9();
        ResultsTable table = RegionFeatures.initialize(labelMap)
                .add(EllipsoidElongations.class)
                .createTable();
        
        assertEquals(8, table.getCounter());
        assertEquals(2, table.getLastColumn());
    }
}
