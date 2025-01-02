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
public class IntensityKurtosisTest
{
    /**
     * Test method for {@link net.ijt.regfeat.intensity.MaxIntensity#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void testCompute()
    {
        ImagePlus labelMap = TestImages.createLabeMap_FourRegions_9x9();
        RegionFeatures data = new RegionFeatures(labelMap, LabelImages.findAllLabels(labelMap));
        data.addImageData("intensity", TestImages.createIntensityImage_FourRegions_9x9());
        
        IntensityKurtosis feature = new IntensityKurtosis();
        data.add(feature.getClass());
        double[] res = feature.compute(data);
        
        assertNotNull(res);
        assertEquals(4, res.length);
        assertTrue(Double.isNaN(res[0]));
        assertTrue(Double.isFinite(res[1]));
        assertTrue(Double.isFinite(res[2]));
        assertTrue(Double.isFinite(res[3]));
    }

    /**
     * Test method for {@link net.ijt.regfeat.intensity.MeanIntensity#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void testCompute_withEmptyRegion()
    {
        ImagePlus labelMap = TestImages.createLabeMap_FourRegions_9x9();
        RegionFeatures data = new RegionFeatures(labelMap, new int[] {3, 5, 7, 8, 9});
        data.addImageData("intensity", TestImages.createIntensityImage_FourRegions_9x9());
        
        IntensityKurtosis feature = new IntensityKurtosis();
        data.add(feature.getClass());
        double[] res = feature.compute(data);
        
        assertNotNull(res);
        assertEquals(5, res.length);
        assertTrue(Double.isNaN(res[0]));
        assertTrue(Double.isFinite(res[1]));
        assertTrue(Double.isNaN(res[2]));
        assertTrue(Double.isFinite(res[3]));
        assertTrue(Double.isFinite(res[4]));
    }

    /**
     * Test method for {@link net.ijt.regfeat.intensity.MaxIntensity#updateTable(ij.measure.ResultsTable, net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void testUpdateTable()
    {
        ImagePlus labelMap = TestImages.createLabeMap_FourRegions_9x9();
        
        ResultsTable table = RegionFeatures.initialize(labelMap)
                .addImageData("intensity", TestImages.createIntensityImage_FourRegions_9x9())
                .add(IntensityKurtosis.class)
                .createTable();
        
        assertEquals(4, table.getCounter());
        assertEquals(0, table.getLastColumn());
    }
}
