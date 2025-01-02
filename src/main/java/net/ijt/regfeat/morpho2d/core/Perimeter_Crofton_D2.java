/**
 * 
 */
package net.ijt.regfeat.morpho2d.core;

import ij.ImagePlus;
import inra.ijpb.measure.IntrinsicVolumes2D;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.SingleValueFeature;

/**
 * Perimeter computed based on Crofton formula discretized with two directions.
 * 
 * @see Perimeter_Crofton_D4
 */
public class Perimeter_Crofton_D2 extends SingleValueFeature
{
    public Perimeter_Crofton_D2()
    {
        super("Perimeter_Crofton_D2");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        ImagePlus labelMap = data.labelMap;
        return IntrinsicVolumes2D.perimeters(labelMap.getProcessor(), data.labels, labelMap.getCalibration(), 2);
    }
}
