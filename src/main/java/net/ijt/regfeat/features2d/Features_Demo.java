/**
 * 
 */
package net.ijt.regfeat.features2d;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
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
        
        RegionAnalyisData results = new RegionAnalyisData(image, labels);
        results.updateWith(Perimeter.instance());
        Circularity circularity = Circularity.instance();
        System.out.println(Circularity.class.getName());
        results.updateWith(circularity);
        
        System.out.println("Computed features:");
        results.printComputedFeatures();
        
        for (int label : labels)
        {
            System.out.println("circ of " + label + " is " + results.regionData.get(label).features.get(Circularity.class));
        }
        
        
        ResultsTable table = results.createTable(Area.instance(), Perimeter.instance(), circularity);
        table.show("Features");
        
//        ResultsTable table = new ResultsTable();
//        for (int i = 0; i < labels.length; i++)
//        {
//            circularity.populateTable(table, i, results.results.get(labels[i]).getFeature(circularity.getId()));
//        }
//        table.show("Circularity");
        
        while(true)
        {
          ;  
        }
//        FeatureManager fm = FeatureManager.getInstance();
//        fm.addFeature(Area.instance());
////        fm.addFeature(Area.instance());
//        fm.addFeature(Perimeter_Crofton_D4.instance());
//        
//        fm.printFeatureList();
    }
    
}
