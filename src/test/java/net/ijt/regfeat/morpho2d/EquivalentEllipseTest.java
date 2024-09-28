/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import static org.junit.Assert.*;

import org.junit.Test;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import inra.ijpb.geometry.Ellipse;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.RegionAnalyisData;

/**
 * 
 */
public class EquivalentEllipseTest
{

    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.Centroid#compute(net.ijt.regfeat.RegionAnalyisData)}.
     */
    @Test
    public final void testComputeRegionAnalyisData()
    {
        ImagePlus labelMap = createImagePlus();
        RegionAnalyisData data = new RegionAnalyisData(labelMap, LabelImages.findAllLabels(labelMap));
        EquivalentEllipse feature = new EquivalentEllipse();

        Ellipse[] res = (Ellipse[]) feature.compute(data);
        
        assertEquals(res[0].center().getX(), 1.5, 0.01);
        assertEquals(res[0].center().getY(), 1.5, 0.01);
        assertEquals(res[3].center().getX(), 5.0, 0.01);
        assertEquals(res[3].center().getY(), 5.0, 0.01);
    }
    
    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.Centroid#populateTable(ij.measure.ResultsTable, java.lang.Object)}.
     */
    @Test
    public final void testPopulateTable()
    {
        ImagePlus labelMap = createImagePlus();
        RegionAnalyisData data = new RegionAnalyisData(labelMap, LabelImages.findAllLabels(labelMap));
        
        EquivalentEllipse feature = new EquivalentEllipse();
        ResultsTable table = new ResultsTable();
        feature.populateTable(table, feature.compute(data));
        
        assertEquals(4, table.getCounter());
        assertEquals(4, table.getLastColumn());
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
