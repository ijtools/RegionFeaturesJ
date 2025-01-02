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
public class MaxIntensityTest
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
        
        data.add(MaxIntensity.class);
        data.computeAll();
        
        double[] res = new MaxIntensity().compute(data);
        
        assertNotNull(res);
        assertEquals(4, res.length);
        assertEquals(11.0, res[0], 0.01);
        assertEquals(15.0, res[1], 0.01);
        assertEquals(51.0, res[2], 0.01);
        assertEquals(55.0, res[3], 0.01);
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
                .add(MaxIntensity.class)
                .createTable();
        
        assertEquals(4, table.getCounter());
        assertEquals(0, table.getLastColumn());
    }
}
