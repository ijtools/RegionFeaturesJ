/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import ij.measure.ResultsTable;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionAnalyisData;

/**
 * Computes the perimeter of a 2D region. In practice, this feature is an alias
 * for the Perimeter_Crofton_D4 feature.
 */
public class Perimeter extends Feature
{
    public Perimeter()
    {
        this.requiredFeatures.add(Perimeter_Crofton_D4.class);
    }
    
    @Override
    public double[] compute(RegionAnalyisData results)
    {
        // check required features have been computed
        if (!results.isComputed(Perimeter_Crofton_D4.class))
        {
            throw new RuntimeException("Requires Perimeter_Crofton_D4 to have been computed");
        }
        
        return (double[]) results.results.get(Perimeter_Crofton_D4.class);
    }
    
    @Override
    public void populateTable(ResultsTable table, Object obj)
    {
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
