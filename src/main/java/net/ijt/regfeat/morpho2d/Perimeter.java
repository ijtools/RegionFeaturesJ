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
    public Object[] compute(RegionAnalyisData results)
    {
        // check required features have been computed
        if (!results.isComputed(Perimeter_Crofton_D4.class))
        {
            throw new RuntimeException("Requires Perimeter_Crofton_D4 to have been computed");
        }
        
        // convert into an array of double
        int[] labels = results.labels;
        Double[] res = new Double[labels.length];
        for (int i = 0; i < labels.length; i++)
        {
            res[i] = (double) results.regionData.get(labels[i]).get(Perimeter_Crofton_D4.class);
        }
        return res;
    }
    
    @Override
    public void populateTable(ResultsTable table, int row, Object obj)
    {
        table.setValue("Perimeter", row, (double) obj);
    }
}
