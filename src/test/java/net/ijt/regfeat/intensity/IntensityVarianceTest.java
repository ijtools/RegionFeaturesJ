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
public class IntensityVarianceTest
{
    /**
     * Test method for {@link net.ijt.regfeat.intensity.MaxIntensity#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void testCompute()
    {
        ImagePlus labelMap = TestImages.createLabeMap_FourRegions_7x7();
        RegionFeatures data = new RegionFeatures(labelMap, LabelImages.findAllLabels(labelMap));
        data.addImageData("intensity", TestImages.createIntensityImage_FourRegions_7x7());
        
        IntensityVariance feature = new IntensityVariance();
        data.add(feature.getClass());
        double[] res = feature.compute(data);
        
        assertNotNull(res);
        assertEquals(4, res.length);
        assertTrue(Double.isNaN(res[0]));
        assertEquals(  1.0, res[1], 0.1);
        assertEquals(100.0, res[2], 0.1);
        assertEquals( 75.7, res[3], 0.1);
    }

    /**
     * Test method for {@link net.ijt.regfeat.intensity.MeanIntensity#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void testCompute_withEmptyRegion()
    {
        ImagePlus labelMap = TestImages.createLabeMap_FourRegions_7x7();
        RegionFeatures data = new RegionFeatures(labelMap, new int[] {3, 5, 7, 8, 9});
        data.addImageData("intensity", TestImages.createIntensityImage_FourRegions_7x7());
        
        IntensityVariance feature = new IntensityVariance();
        data.add(feature.getClass());
        double[] res = feature.compute(data);
        
        assertNotNull(res);
        assertEquals(5, res.length);
        assertTrue(Double.isNaN(res[0]));
        assertEquals(  1.0, res[1], 0.1);
        assertTrue(Double.isNaN(res[2]));
        assertEquals(100.0, res[3], 0.1);
        assertEquals( 75.7, res[4], 0.1);
    }

    /**
     * Test method for {@link net.ijt.regfeat.intensity.MaxIntensity#updateTable(ij.measure.ResultsTable, net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void testUpdateTable()
    {
        ImagePlus labelMap = TestImages.createLabeMap_FourRegions_7x7();
        
        ResultsTable table = RegionFeatures.initialize(labelMap)
                .addImageData("intensity", TestImages.createIntensityImage_FourRegions_7x7())
                .add(IntensityVariance.class)
                .createTable();
        
        assertEquals(4, table.getCounter());
        assertEquals(0, table.getLastColumn());
    }
}
