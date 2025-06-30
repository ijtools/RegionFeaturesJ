/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.RegionFeatures;

/**
 * 
 */
public class AverageThicknessTest
{

    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.AverageThickness#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void testCompute()
    {
        ImagePlus labelMap = createImagePlus();
        RegionFeatures data = new RegionFeatures(labelMap, LabelImages.findAllLabels(labelMap));
        data.add(AverageThickness.class);
        data.computeAll();
        
        double[] thicknesses = (double[]) data.results.get(AverageThickness.class);
        
        assertNotNull(thicknesses);
        assertEquals(4, thicknesses.length);
        assertEquals(1.0, thicknesses[0], 0.01);
        assertEquals(1.0, thicknesses[1], 0.01);
        assertEquals(1.0, thicknesses[2], 0.01);
        assertEquals(3.0, thicknesses[3], 0.01);
    }
    
    private static final ImagePlus createImagePlus()
    {
        ImageProcessor array = new ByteProcessor(9, 9);
        array.set(1, 1, 3);
        for (int i = 3; i < 7; i++)
        {
            array.set(i, 1, 5);
            array.set(1, i, 8);
        }
        
        for (int i = 3; i < 8; i++)
        {
            for (int j = 3; j < 8; j++)
            {
                array.set(i, j, 9);
            }
        }
        return new ImagePlus("labels", array);
    }

}
