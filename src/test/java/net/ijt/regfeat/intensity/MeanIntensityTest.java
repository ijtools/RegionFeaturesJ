/**
 * 
 */
package net.ijt.regfeat.intensity;

import static org.junit.Assert.*;

import org.junit.Test;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.RegionFeatures;

/**
 * 
 */
public class MeanIntensityTest
{

    /**
     * Test method for {@link net.ijt.regfeat.intensity.MeanIntensity#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void testCompute()
    {
        ImagePlus labelMap = createLabelImage();
        RegionFeatures data = new RegionFeatures(labelMap, LabelImages.findAllLabels(labelMap));
        data.addImageData("intensity", createIntensityImage());
        
        data.add(MeanIntensity.class);
        data.computeAll();
        
        double[] res = new MeanIntensity().compute(data);
        
        assertNotNull(res);
        assertEquals(4, res.length);
        assertEquals(11.0, res[0], 0.01);
        assertEquals(14.5, res[1], 0.01);
        assertEquals(46.0, res[2], 0.01);
        assertEquals(49.5, res[3], 0.01);
    }

    /**
     * Test method for {@link net.ijt.regfeat.intensity.MeanIntensity#updateTable(ij.measure.ResultsTable, net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void testUpdateTable()
    {
        ImagePlus labelMap = createLabelImage();
        
        ResultsTable table = RegionFeatures.initialize(labelMap)
                .addImageData("intensity", createIntensityImage())
                .add(MeanIntensity.class)
                .createTable();
        
        assertEquals(4, table.getCounter());
        assertEquals(0, table.getLastColumn());
    }


    private static final ImagePlus createLabelImage()
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
    
    private static final ImagePlus createIntensityImage()
    {
        ImageProcessor array = new ByteProcessor(8, 8);
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                array.set(i, j, j * 10 + i);
            }
        }
        return new ImagePlus("values", array);
    }
}
