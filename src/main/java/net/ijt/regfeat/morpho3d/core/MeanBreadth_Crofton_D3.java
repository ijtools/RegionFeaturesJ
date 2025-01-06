/**
 * 
 */
package net.ijt.regfeat.morpho3d.core;

import ij.ImagePlus;
import inra.ijpb.measure.IntrinsicVolumes3D;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.SingleValueFeature;

/**
 * Computation of mean breadth computed based on Crofton formula discretized
 * with three directions.
 * 
 * @see SurfaceArea_Crofton_D13
 */
public class MeanBreadth_Crofton_D3 extends SingleValueFeature
{
    public MeanBreadth_Crofton_D3()
    {
        super("MeanBreadth_Crofton_D3");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        ImagePlus labelMap = data.labelMap;
        return IntrinsicVolumes3D.meanBreadths(labelMap.getStack(), data.labels, labelMap.getCalibration(), 13, 8);
    }
}
