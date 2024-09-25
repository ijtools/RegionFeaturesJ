/**
 * 
 */
package net.ijt.regfeat.features2d;

import java.util.Map;

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
    public Object[] compute(int[] labels, RegionAnalyisData results)
    {
        Double[] res = new Double[labels.length];
        for (int i = 0; i < labels.length; i++)
        {
            Map<Class<? extends Feature>, Object> data = results.regionData.get(labels[i]);
            double a = (double) data.get(Area.class);
            double p = (double) data.get(Perimeter.class);
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
