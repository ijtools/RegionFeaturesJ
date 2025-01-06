/**
 * 
 */
package net.ijt.regfeat.morpho3d.core;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import inra.ijpb.measure.region3d.IntrinsicVolumes3D;
import net.ijt.regfeat.RegionFeature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.morpho3d.Volume;

/**
 * Computes the four intrinsic volumes in 3D, and returns an array of
 * <code>IntrinsicVolumes3D.Result</code>.
 * 
 * This feature is devoted to factorize the computation of the Volume, Surface
 * area, mean breadth, and EulerNumber features.
 * 
 * @see Volume
 */
public class IntrinsicVolumes implements RegionFeature
{
    @Override
    public IntrinsicVolumes3D.Result[] compute(RegionFeatures results)
    {
        ImagePlus labelMap = results.labelMap;
            
        IntrinsicVolumes3D algo = new IntrinsicVolumes3D();
        return algo.analyzeRegions(labelMap.getStack(), results.labels, labelMap.getCalibration());
    }
    
    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof IntrinsicVolumes3D.Result[])
        {
            IntrinsicVolumes3D.Result[] array = (IntrinsicVolumes3D.Result[]) obj;
            for (int r = 0; r < array.length; r++)
            {
                table.setValue("Volume", r, array[r].volume);
                table.setValue("Surface_Area", r, array[r].surfaceArea);
                table.setValue("Mean_Breadth", r, array[r].meanBreadth);
                table.setValue("Euler Number", r, array[r].eulerNumber);
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of IntrinsicVolumes3D.Result");
        }
    }
}
