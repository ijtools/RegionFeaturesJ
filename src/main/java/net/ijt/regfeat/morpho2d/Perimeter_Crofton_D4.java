/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import inra.ijpb.measure.region2d.IntrinsicVolumes2D;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionAnalyisData;

/**
 * Computation of perimeter using discretization of Crofton formula with four
 * directions.
 */
public class Perimeter_Crofton_D4 extends Feature
{
    @Override
    public double[] compute(RegionAnalyisData results)
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
    public void populateTable(ResultsTable table, int row, Object value)
    {
        table.setValue("Perimeter_Crofton_D4", row, (double) value);
    }
}
