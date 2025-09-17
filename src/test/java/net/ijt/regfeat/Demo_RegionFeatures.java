/**
 * 
 */
package net.ijt.regfeat;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import net.ijt.regfeat.morpho2d.Area;
import net.ijt.regfeat.morpho2d.Centroid;
import net.ijt.regfeat.morpho2d.Circularity;
import net.ijt.regfeat.morpho2d.Perimeter;
/**
 * Simple file for demonstrating the usage of features.
 */
public class Demo_RegionFeatures
{
    /**
     * Default empty constructor.
     */
    private Demo_RegionFeatures()
    {
    }
    
    /**
     * The main function.
     * 
     * @param args the optional arguments (not used).
     */
    public static void main(String[] args)
    {
        ImagePlus image = createImagePlus();
        int[] labels = new int[] {3, 5, 8, 9};
        
        ResultsTable table = RegionFeatures.initialize(image, labels)
                .add(Area.class)
                .add(Perimeter.class)
                .add(Circularity.class)
                .add(Centroid.class)
                .computeAll()
                .createTable();
        
        table.show("Features");
        
        while(true)
        {
          ;  
        }
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
