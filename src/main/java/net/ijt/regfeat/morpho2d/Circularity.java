/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import ij.measure.ResultsTable;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionAnalyisData;

/**
 * The circularity of a region, defined from normalized ratio of area and
 * squared perimeter.
 */
public class Circularity extends Feature
{
    public Circularity()
    {
        this.requiredFeatures.add(Area.class);
        this.requiredFeatures.add(Perimeter.class);
    }
    
    @Override
    public Object[] compute(RegionAnalyisData results)
    {
        ensureRequiredFeaturesAreComputed(results);
        
        Double[] areas = (Double[]) results.results.get(Area.class);
        Double[] perims = (Double[]) results.results.get(Perimeter.class);
        int[] labels = results.labels;
        Double[] res = new Double[labels.length];
        for (int i = 0; i < labels.length; i++)
        {
            double a = areas[i];
            double p = perims[i];
            double circ = 4 * Math.PI * a / (p * p);
            res[i] = circ;
        }
        return res;
    }
    
    @Override
    public void populateTable(ResultsTable table, int row, Object value)
    {
        table.setValue("Circ.", row, (Double) value);
    }
}
