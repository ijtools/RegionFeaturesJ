/**
 * 
 */
package net.ijt.regfeat.morpho2d.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import inra.ijpb.geometry.PointPair2D;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.RegionFeatures;

/**
 * 
 */
public class FurthestPointPairTest
{

    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.core.FurthestPointPair#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void test_compute()
    {
        ImagePlus labelMap = createImagePlus();
        RegionFeatures data = new RegionFeatures(labelMap, LabelImages.findAllLabels(labelMap));
        FurthestPointPair feature = new FurthestPointPair();

        PointPair2D[] res = (PointPair2D[]) feature.compute(data);
        assertNotNull(res);
    }

    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.core.FurthestPointPair#updateTable(ij.measure.ResultsTable, net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void test_updateTable()
    {
        ImagePlus labelMap = createImagePlus();
        RegionFeatures data = RegionFeatures.initialize(labelMap)
                .add(FurthestPointPair.class)
                .computeAll();
        
        FurthestPointPair feature = new FurthestPointPair();
        ResultsTable table = new ResultsTable();
        feature.updateTable(table, data);
        
        assertEquals(4, table.getCounter());
        assertEquals(3, table.getLastColumn());
    }

    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.core.FurthestPointPair#overlayResult(ij.ImagePlus, net.ijt.regfeat.RegionFeatures, double)}.
     */
    @Test
    public final void test_overlayResult()
    {
        ImagePlus labelMap = IJ.openImage(getClass().getResource("/grains-med-WTH-lbl.tif").getFile());
        RegionFeatures data = new RegionFeatures(labelMap, LabelImages.findAllLabels(labelMap));
        data.process(FurthestPointPair.class);
        
        FurthestPointPair feature = new FurthestPointPair();
        feature.overlayResult(labelMap, data, 1.5);
        labelMap.show();
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
