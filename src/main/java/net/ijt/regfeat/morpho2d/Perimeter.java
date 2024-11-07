/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import ij.measure.ResultsTable;
import net.ijt.regfeat.RegionFeature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.morpho2d.core.Perimeter_Crofton_D4;

/**
 * Computes the perimeter of a 2D region. In practice, this feature is an alias
 * for the Perimeter_Crofton_D4 feature.
 */
public class Perimeter extends RegionFeature
{
    public Perimeter()
    {
        this.requiredFeatures.add(Perimeter_Crofton_D4.class);
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        ensureRequiredFeaturesAreComputed(data);
        return (double[]) data.results.get(Perimeter_Crofton_D4.class);
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
                table.setValue("Perimeter", r, array[r]);
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of double");
        }
    }
}
