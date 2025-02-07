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
public class CenterOfMassTest
{
    /**
     * Test method for {@link net.ijt.regfeat.intensity.CenterOfMass#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void testCompute()
    {
        ImagePlus labelMap = TestImages.createLabeMap_FourRegions_7x7();
        RegionFeatures data = new RegionFeatures(labelMap, LabelImages.findAllLabels(labelMap));
        data.addImageData("intensity", TestImages.createIntensityImage_FourRegions_7x7());
        
        data.add(MinIntensity.class);
        data.computeAll();
        
        double[][] res = new CenterOfMass().compute(data);
        
        assertNotNull(res);
        assertEquals(4, res.length);
        assertEquals(2, res[0].length);
        assertEquals(2, res[0].length);
    }

    /**
     * Test method for {@link net.ijt.regfeat.intensity.CenterOfMass#updateTable(ij.measure.ResultsTable, net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void testUpdateTable()
    {
        ImagePlus labelMap = TestImages.createLabeMap_FourRegions_7x7();
        
        ResultsTable table = RegionFeatures.initialize(labelMap)
                .addImageData("intensity", TestImages.createIntensityImage_FourRegions_7x7())
                .add(CenterOfMass.class)
                .createTable();
        
        assertEquals(4, table.getCounter());
        assertEquals(1, table.getLastColumn());
    }
}
