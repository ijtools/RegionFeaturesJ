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
 * A feature that computes the area of 2D regions.
 */
public class Area extends Feature
{
    @Override
    public double[] compute(RegionAnalyisData results)
    {
        ImagePlus labelMap = results.labelMap;
            
        IntrinsicVolumes2D algo = new IntrinsicVolumes2D();
        IntrinsicVolumes2D.Result[] res = algo.analyzeRegions(labelMap.getProcessor(), results.labels, labelMap.getCalibration());
        
        double[] areas = new double[results.labels.length];
        for (int i = 0; i < results.labels.length; i++)
        {
            areas[i] = res[i].area;
        }
        return areas;
    }
    
    @Override
    public void populateTable(ResultsTable table, Object obj)
    {
        if (obj instanceof double[])
        {
            double[] array = (double[]) obj;
            for (int r = 0; r < array.length; r++)
            {
                table.setValue("Area", r, array[r]);
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of double");
        }
    }
}
