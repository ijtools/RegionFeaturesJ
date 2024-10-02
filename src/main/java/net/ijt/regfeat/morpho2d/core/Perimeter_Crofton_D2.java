/**
 * 
 */
package net.ijt.regfeat.morpho2d.core;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import inra.ijpb.measure.IntrinsicVolumes2D;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;

/**
 * Perimeter computed based on Crofton formula discretized with two directions.
 * 
 * @see Perimeter_Crofton_D4
 */
public class Perimeter_Crofton_D2 extends Feature
{
    @Override
    public double[] compute(RegionFeatures data)
    {
        ImagePlus labelMap = data.labelMap;
        return IntrinsicVolumes2D.perimeters(labelMap.getProcessor(), data.labels, labelMap.getCalibration(), 2);
    }
    
    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof double[])
        {
            double[] array = (double[]) obj;
            for (int r = 0; r < array.length; r++)
            {
                table.setValue("Perimeter_Crofton_D2", r, array[r]);
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of double");
        }
    }
}
