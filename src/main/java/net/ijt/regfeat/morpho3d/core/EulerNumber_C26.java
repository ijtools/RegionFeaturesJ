/**
 * 
 */
package net.ijt.regfeat.morpho3d.core;

import ij.ImagePlus;
import inra.ijpb.measure.IntrinsicVolumes3D;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.SingleValueFeature;
import net.ijt.regfeat.morpho2d.EulerNumber;

/**
 * Euler number for 3D regions using the C26 connectivity.
 * 
 * @see EulerNumber
 */
public class EulerNumber_C26 extends SingleValueFeature
{
    public EulerNumber_C26()
    {
        super("Euler_Number_C26");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve Euler Number
        ImagePlus labelMap = data.labelMap;
        return IntrinsicVolumes3D.eulerNumbers(labelMap.getStack(), data.labels, 26);
    }
}
