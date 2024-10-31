/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import inra.ijpb.geometry.Ellipse;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.RegionFeatures;

/**
 * 
 */
public class EquivalentEllipseTest
{

    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.EquivalentEllipse#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void testCompute()
    {
        ImagePlus labelMap = createImagePlus();
        RegionFeatures data = new RegionFeatures(labelMap, LabelImages.findAllLabels(labelMap));
        EquivalentEllipse feature = new EquivalentEllipse();

        Ellipse[] res = (Ellipse[]) feature.compute(data);
        
        assertEquals(res[0].center().getX(), 1.5, 0.01);
        assertEquals(res[0].center().getY(), 1.5, 0.01);
        assertEquals(res[3].center().getX(), 5.0, 0.01);
        assertEquals(res[3].center().getY(), 5.0, 0.01);
    }
    
    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.EquivalentEllipse#compute(net.ijt.regfeat.RegionFeatures)}.
     */
    @Test
    public final void testCompute_wheatGrains()
    {
        ImagePlus labelMap = IJ.openImage(getClass().getResource("/grains-med-WTH-lbl.tif").getFile());
        RegionFeatures data = new RegionFeatures(labelMap, LabelImages.findAllLabels(labelMap));
        
        EquivalentEllipse feature = new EquivalentEllipse();
        Ellipse[] res = (Ellipse[]) feature.compute(data);
        
        assertEquals(96, res.length);
    }
    
    /**
     * Test method for {@link net.ijt.regfeat.morpho2d.Centroid#updateTable(ij.measure.ResultsTable, java.lang.Object)}.
     */
    @Test
    public final void testUpdateTable()
    {
        ImagePlus labelMap = createImagePlus();
        RegionFeatures data = RegionFeatures.initialize(labelMap)
                .add(EquivalentEllipse.class)
                .computeAll();
        
        EquivalentEllipse feature = new EquivalentEllipse();
        ResultsTable table = new ResultsTable();
        feature.updateTable(table, data);
        
        assertEquals(4, table.getCounter());
        assertEquals(4, table.getLastColumn());
    }
    
    @Test
    public final void test_overlayResult()
    {
        ImagePlus labelMap = IJ.openImage(getClass().getResource("/grains-med-WTH-lbl.tif").getFile());
        RegionFeatures data = new RegionFeatures(labelMap, LabelImages.findAllLabels(labelMap));
        data.process(EquivalentEllipse.class);
        
        EquivalentEllipse feature = new EquivalentEllipse();
        feature.overlayResult(data, labelMap);
        labelMap.show();
//        while(true);
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
