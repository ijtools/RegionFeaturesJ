/**
 * 
 */
package net.ijt.regfeat.morpho3d;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import inra.ijpb.geometry.Sphere;
import net.ijt.regfeat.RegionFeatures;

/**
 * 
 */
public class LargestInscribedBallTest
{
    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.EquivalentEllipse#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void testCompute()
    {
        ImagePlus labelMap = TestImages.createLabeMap_EightRegions_9x9x9();
        RegionFeatures data = RegionFeatures.initialize(labelMap);
        
        LargestInscribedBall feature = new LargestInscribedBall();
        Sphere[] res = (Sphere[]) feature.compute(data);
        
        assertEquals(res.length, 8);
        assertEquals(res[0].center().getX(), 1.0, 0.01);
        assertEquals(res[0].center().getY(), 1.0, 0.01);
        assertEquals(res[0].center().getZ(), 1.0, 0.01);
        assertEquals(res[0].radius(), 1.0, 0.01);
        assertEquals(res[7].center().getX(), 5.0, 0.01);
        assertEquals(res[7].center().getY(), 5.0, 0.01);
        assertEquals(res[7].center().getZ(), 5.0, 0.01);
        assertEquals(res[7].radius(), 3.0, 0.01);
    }
    
    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.Centroid#updateTable(ij.measure.ResultsTable, java.lang.Object)}.
     */
    @Test
    public final void testUpdateTable()
    {
        ImagePlus labelMap = TestImages.createLabeMap_EightRegions_9x9x9();
        ResultsTable table = RegionFeatures.initialize(labelMap)
                .add(LargestInscribedBall.class)
                .createTable();
        
        assertEquals(8, table.getCounter());
        assertEquals(3, table.getLastColumn());
    }
}
