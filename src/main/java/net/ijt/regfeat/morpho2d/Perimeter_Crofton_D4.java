/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import inra.ijpb.measure.region2d.IntrinsicVolumes2D;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;

/**
 * Computation of perimeter using discretization of Crofton formula with four
 * directions.
 */
public class Perimeter_Crofton_D4 extends Feature
{
    @Override
    public double[] compute(RegionFeatures results)
    {
        ImagePlus labelMap = results.labelMap;
        int[] labels = results.labels;
            
        IntrinsicVolumes2D algo = new IntrinsicVolumes2D();
        IntrinsicVolumes2D.Result[] res = algo.analyzeRegions(labelMap.getProcessor(), labels, labelMap.getCalibration());
        
        double[] perims = new double[labels.length];
        for (int i = 0; i < labels.length; i++)
        {
            perims[i] = res[i].perimeter;
        }
        return perims;
    }
    
    @Override
    public void populateTable(ResultsTable table, Object obj)
    {
        if (obj instanceof double[])
        {
            double[] array = (double[]) obj;
            for (int r = 0; r < array.length; r++)
            {
                table.setValue("Perimeter_Crofton_D4", r, array[r]);
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of double");
        }
    }
}
