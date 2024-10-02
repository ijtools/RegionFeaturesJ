/**
 * 
 */
package net.ijt.regfeat.morpho2d.core;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import inra.ijpb.measure.region2d.IntrinsicVolumes2D;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.morpho2d.Area;
import net.ijt.regfeat.morpho2d.EulerNumber;
import net.ijt.regfeat.morpho2d.Perimeter;

/**
 * Computes the three intrinsic volumes in 2D, and returns an array of
 * <code>IntrinsicVolumes2D.Result</code>.
 * 
 * This feature is devoted to factorize the computation of the Area, Perimeter,
 * and EulerNumber features.
 * 
 * @see Area
 * @see Perimeter
 * @see EulerNumber
 */
public class IntrinsicVolumes extends Feature
{
    @Override
    public IntrinsicVolumes2D.Result[] compute(RegionFeatures results)
    {
        ImagePlus labelMap = results.labelMap;
            
        IntrinsicVolumes2D algo = new IntrinsicVolumes2D();
        return algo.analyzeRegions(labelMap.getProcessor(), results.labels, labelMap.getCalibration());
    }
    
    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof IntrinsicVolumes2D.Result[])
        {
            IntrinsicVolumes2D.Result[] array = (IntrinsicVolumes2D.Result[]) obj;
            for (int r = 0; r < array.length; r++)
            {
                table.setValue("Area", r, array[r].area);
                table.setValue("Perimeter", r, array[r].perimeter);
                table.setValue("Euler Number", r, array[r].eulerNumber);
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of IntrinsicVolumes2D.Result");
        }
    }
}
