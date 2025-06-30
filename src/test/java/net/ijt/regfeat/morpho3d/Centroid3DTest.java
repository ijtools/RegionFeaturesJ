/**
 * 
 */
package net.ijt.regfeat.morpho3d;

import static org.junit.Assert.*;

import org.junit.Test;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import inra.ijpb.geometry.Point3D;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.RegionFeatures;

/**
 * 
 */
public class Centroid3DTest
{
    /**
     * Test method for {@link net.ijt.regfeat.morpho3d.Volume#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void testCompute()
    {
        ImagePlus labelMap = TestImages.createLabeMap_EightRegions_9x9x9();
        
        RegionFeatures data = new RegionFeatures(labelMap, LabelImages.findAllLabels(labelMap));
        
        Centroid3D feature = new Centroid3D();
        Point3D[] res = (Point3D[]) feature.compute(data);
                
        assertEquals(res.length, 8);
        Point3D[] exp = new Point3D[] {
                new Point3D(1.5, 1.5, 1.5),
                new Point3D(5.5, 1.5, 1.5),
                new Point3D(1.5, 5.5, 1.5),
                new Point3D(5.5, 5.5, 1.5),
                new Point3D(1.5, 1.5, 5.5),
                new Point3D(5.5, 1.5, 5.5),
                new Point3D(1.5, 5.5, 5.5),
                new Point3D(5.5, 5.5, 5.5),
        };
        for (int i = 0; i < 8; i++)
        {
            Point3D p = res[i];
            assertEquals(p.getX(), exp[i].getX(), 0.01);
            assertEquals(p.getY(), exp[i].getY(), 0.01);
            assertEquals(p.getZ(), exp[i].getZ(), 0.01);
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
                .add(Centroid3D.class)
                .createTable();
        
        assertEquals(8, table.getCounter());
        assertEquals(2, table.getLastColumn());
        
        assertTrue("Centroid_X".equals(table.getColumnHeading(0)));
        assertTrue("Centroid_Y".equals(table.getColumnHeading(1)));
        assertTrue("Centroid_Z".equals(table.getColumnHeading(2)));
    }

}
