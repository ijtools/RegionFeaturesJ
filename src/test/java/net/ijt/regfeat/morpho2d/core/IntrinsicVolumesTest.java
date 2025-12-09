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
import inra.ijpb.measure.region2d.IntrinsicVolumes2D;
import net.ijt.regfeat.RegionFeatures;

/**
 * 
 */
public class IntrinsicVolumesTest
{

    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.core.IntrinsicVolumes#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void test_compute()
    {
        ImagePlus labelMap = createImagePlus();
        RegionFeatures data = new RegionFeatures(labelMap, LabelImages.findAllLabels(labelMap));
        IntrinsicVolumes feature = new IntrinsicVolumes();

        IntrinsicVolumes2D.Result[] res = (IntrinsicVolumes2D.Result[]) feature.compute(data);
        
        assertEquals(res[0].area,  1.0, 0.01);
        assertEquals(res[1].area,  4.0, 0.01);
        assertEquals(res[2].area,  4.0, 0.01);
        assertEquals(res[3].area, 16.0, 0.01);
        assertEquals(res[0].eulerNumber, 1.0, 0.01);
        assertEquals(res[1].eulerNumber, 1.0, 0.01);
        assertEquals(res[2].eulerNumber, 1.0, 0.01);
        assertEquals(res[3].eulerNumber, 1.0, 0.01);
    }

    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.core.IntrinsicVolumes#updateTable(ij.measure.ResultsTable, net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void test_updateTable()
    {
        ImagePlus labelMap = createImagePlus();
        RegionFeatures data = RegionFeatures.initialize(labelMap)
                .add(IntrinsicVolumes.class)
                .computeAll();
        
        IntrinsicVolumes feature = new IntrinsicVolumes();
        ResultsTable table = new ResultsTable();
        feature.updateTable(table, data);
        
        assertEquals(4, table.getCounter());
        assertEquals(2, table.getLastColumn());
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
