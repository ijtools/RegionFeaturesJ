/**
 * 
 */
package net.ijt.regfeat.morpho2d.core;

import ij.ImagePlus;
import inra.ijpb.measure.IntrinsicVolumes2D;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.SingleValueFeature;
import net.ijt.regfeat.morpho2d.EulerNumber;

/**
 * Euler number using the C8 connectivity.
 * 
 * @see EulerNumber
 */
public class EulerNumber_C8 extends SingleValueFeature
{
    public EulerNumber_C8()
    {
        super("Euler_Number_C8");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve Euler Number
        ImagePlus labelMap = data.labelMap;
        int[] intEuler = IntrinsicVolumes2D.eulerNumbers(labelMap.getProcessor(), data.labels, 8);
        
        // convert to array of double
        double[] eulers = new double[intEuler.length];
        for (int i = 0; i < intEuler.length; i++)
        {
            eulers[i] = intEuler[i];
        }
        return eulers;
    }
}
