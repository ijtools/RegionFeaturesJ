/**
 * 
 */
package net.ijt.regfeat.morpho2d.core;

import ij.measure.ResultsTable;
import inra.ijpb.measure.region2d.IntrinsicVolumes2D;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.morpho2d.IntrinsicVolumes;

/**
 * Computation of perimeter using discretization of Crofton formula with four
 * directions.
 */
public class Perimeter_Crofton_D4 extends Feature
{
    public Perimeter_Crofton_D4()
    {
        this.requiredFeatures.add(IntrinsicVolumes.class);
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        ensureRequiredFeaturesAreComputed(data);
        IntrinsicVolumes2D.Result[] res = (IntrinsicVolumes2D.Result[]) data.results.get(IntrinsicVolumes.class);
        
        double[] perims = new double[res.length];
        for (int i = 0; i < res.length; i++)
        {
            perims[i] = res[i].perimeter;
        }
        return perims;
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
                table.setValue("Perimeter_Crofton_D4", r, array[r]);
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of double");
        }
    }
}
