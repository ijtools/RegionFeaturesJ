/**
 * 
 */
package net.ijt.regfeat.morpho3d.core;

import ij.ImagePlus;
import inra.ijpb.measure.IntrinsicVolumes3D;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.SingleValueFeature;

/**
 * Computation of surface area computed based on Crofton formula discretized
 * with three directions.
 * 
 * @see SurfaceArea_Crofton_D13
 */
public class SurfaceArea_Crofton_D3 extends SingleValueFeature
{
    public SurfaceArea_Crofton_D3()
    {
        super("SurfaceArea_Crofton_D3");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        ImagePlus labelMap = data.labelMap;
        return IntrinsicVolumes3D.surfaceAreas(labelMap.getStack(), data.labels, labelMap.getCalibration(), 3);
    }
}
