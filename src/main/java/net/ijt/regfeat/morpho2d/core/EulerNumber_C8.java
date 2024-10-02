/**
 * 
 */
package net.ijt.regfeat.morpho2d.core;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import inra.ijpb.measure.IntrinsicVolumes2D;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.morpho2d.EulerNumber;

/**
 * Euler number using the C8 connectivity.
 * 
 * @see EulerNumber
 */
public class EulerNumber_C8 extends Feature
{
    @Override
    public int[] compute(RegionFeatures data)
    {
        ImagePlus labelMap = data.labelMap;
        return IntrinsicVolumes2D.eulerNumbers(labelMap.getProcessor(), data.labels, 8);
    }
    
    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof int[])
        {
            int[] array = (int[]) obj;
            for (int r = 0; r < array.length; r++)
            {
                table.setValue("Euler Number_C8", r, array[r]);
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of double");
        }
    }
}
