/**
 * 
 */
package net.ijt.regfeat.intensity;

import static org.junit.Assert.*;

import org.junit.Test;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.RegionFeatures;

/**
 * 
 */
public class MedianIntensityTest
{
    /**
     * Test method for {@link net.ijt.regfeat.intensity.MeanIntensity#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void testCompute()
    {
        ImagePlus labelMap = TestImages.createLabeMap_FourRegions_7x7();
        RegionFeatures data = new RegionFeatures(labelMap, LabelImages.findAllLabels(labelMap));
        data.addImageData("intensity", TestImages.createIntensityImage_FourRegions_7x7());
        
        data.add(MedianIntensity.class);
        data.computeAll();
        
        double[] res = new MedianIntensity().compute(data);
        
        assertNotNull(res);
        assertEquals(4, res.length);
        assertEquals(11.0, res[0], 0.01);
        assertEquals(14.0, res[1], 0.01);
        assertEquals(41.0, res[2], 0.01);
        assertEquals(44.0, res[3], 0.01);
    }

    /**
     * Test method for {@link net.ijt.regfeat.intensity.MeanIntensity#updateTable(ij.measure.ResultsTable, net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void testUpdateTable()
    {
        ImagePlus labelMap = TestImages.createLabeMap_FourRegions_7x7();
        
        ResultsTable table = RegionFeatures.initialize(labelMap)
                .addImageData("intensity", TestImages.createIntensityImage_FourRegions_7x7())
                .add(MedianIntensity.class)
                .createTable();
        
        assertEquals(4, table.getCounter());
        assertEquals(0, table.getLastColumn());
    }
}
