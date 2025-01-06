/**
 * 
 */
package net.ijt.regfeat.morpho3d;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import inra.ijpb.geometry.Ellipsoid;
import net.ijt.regfeat.RegionFeatures;

/**
 * 
 */
public class EquivalentEllipsoidTest
{
    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.EquivalentEllipse#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void testCompute()
    {
        ImagePlus labelMap = TestImages.createLabeMap_EightRegions_9x9x9();
        RegionFeatures data = RegionFeatures.initialize(labelMap);
        
        EquivalentEllipsoid feature = new EquivalentEllipsoid();
        Ellipsoid[] res = (Ellipsoid[]) feature.compute(data);
        
        assertEquals(res.length, 8);
        assertEquals(res[0].center().getX(), 1.5, 0.01);
        assertEquals(res[0].center().getY(), 1.5, 0.01);
        assertEquals(res[0].center().getZ(), 1.5, 0.01);
        assertEquals(res[7].center().getX(), 5.5, 0.01);
        assertEquals(res[7].center().getY(), 5.5, 0.01);
        assertEquals(res[7].center().getZ(), 5.5, 0.01);
    }
    
    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.Centroid#updateTable(ij.measure.ResultsTable, java.lang.Object)}.
     */
    @Test
    public final void testUpdateTable()
    {
        ImagePlus labelMap = TestImages.createLabeMap_EightRegions_9x9x9();
        ResultsTable table = RegionFeatures.initialize(labelMap)
                .add(EquivalentEllipsoid.class)
                .createTable();
        
        assertEquals(8, table.getCounter());
        assertEquals(8, table.getLastColumn());
    }
}
