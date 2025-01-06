/**
 * 
 */
package net.ijt.regfeat.morpho3d;

import static org.junit.Assert.*;

import org.junit.Test;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import inra.ijpb.geometry.Box3D;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.RegionFeatures;

/**
 * 
 */
public class Bounds3DTest
{
    /**
     * Test method for {@link net.ijt.regfeat.morpho3d.Volume#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void testCompute()
    {
        ImagePlus labelMap = TestImages.createLabeMap_EightRegions_9x9x9();
        
        RegionFeatures data = new RegionFeatures(labelMap, LabelImages.findAllLabels(labelMap));
        
        Bounds3D feature = new Bounds3D();
        Box3D[] res = (Box3D[]) feature.compute(data);
                
        assertEquals(res.length, 8);
        for (int i = 0; i < 8; i++)
        {
            Box3D box = res[i];
            assertTrue(box.getXMin() >= 0);
            assertTrue(box.getYMin() >= 0);
            assertTrue(box.getZMin() >= 0);
            assertTrue(box.getXMin() <= box.getXMax());
            assertTrue(box.getYMin() <= box.getYMax());
            assertTrue(box.getZMin() <= box.getZMax());
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
                .add(Bounds3D.class)
                .createTable();
        
        assertEquals(8, table.getCounter());
        assertEquals(5, table.getLastColumn());
        
        assertTrue("Bounds3D_XMin".equals(table.getColumnHeading(0)));
        assertTrue("Bounds3D_XMax".equals(table.getColumnHeading(1)));
        assertTrue("Bounds3D_YMin".equals(table.getColumnHeading(2)));
        assertTrue("Bounds3D_YMax".equals(table.getColumnHeading(3)));
        assertTrue("Bounds3D_ZMin".equals(table.getColumnHeading(4)));
        assertTrue("Bounds3D_ZMax".equals(table.getColumnHeading(5)));
    }

}
