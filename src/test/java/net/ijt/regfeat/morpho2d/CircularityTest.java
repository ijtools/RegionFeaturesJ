/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.RegionAnalyisData;

/**
 * 
 */
public class CircularityTest
{
    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.Circularity#compute(net.ijt.regfeat.RegionAnalyisData)}.
     */
    @Test
    public final void testCompute()
    {
        ImagePlus labelMap = createImagePlus();
        RegionAnalyisData data = new RegionAnalyisData(labelMap, LabelImages.findAllLabels(labelMap));
        
        Circularity feature = new Circularity();
        Object[] res = feature.compute(data);
        
        Object res3 = res[3];
        assertTrue(res3 instanceof Double);
        assertEquals((double) res3, 1.0, 0.2);
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
