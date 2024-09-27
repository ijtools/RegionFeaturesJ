/**
 * 
 */
package net.ijt.regfeat;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import net.ijt.regfeat.morpho2d.Area;
import net.ijt.regfeat.morpho2d.Circularity;
import net.ijt.regfeat.morpho2d.Perimeter;
/**
 * Simple file for demonstrating the usage of features.
 */
public class Features_Demo
{
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        System.out.println("hello...");
        
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
        ImagePlus image = new ImagePlus("labels", array);
        int[] labels = new int[] {3, 5, 8, 9};
        
        RegionAnalyisData data = new RegionAnalyisData(image, labels);
        data.updateWith(Perimeter.class);
        data.updateWith(Circularity.class);
        
        System.out.println("Computed features:");
        data.printComputedFeatures();
        
        for (int i = 0; i < labels.length; i++)
        {
          System.out.println("circ of " + labels[i] + " is " + ((double[]) data.results.get(Circularity.class))[i]);
        }
        
        @SuppressWarnings("unchecked")
        ResultsTable table = data.createTable(Area.class, Perimeter.class, Circularity.class);
        table.show("Features");
        
        while(true)
        {
          ;  
        }
    }
    
}
