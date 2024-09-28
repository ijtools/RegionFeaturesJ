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
    public double[] compute(RegionAnalyisData data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        double[] areas = (double[]) data.results.get(Area.class);
        double[] perims = (double[]) data.results.get(Perimeter.class);
        
        // iterate over labels to compute new feature
        int[] labels = data.labels;
        double[] res = new double[labels.length];
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
    public void populateTable(ResultsTable table, Object obj)
    {
        if (obj instanceof double[])
        {
            double[] array = (double[]) obj;
            for (int r = 0; r < array.length; r++)
            {
                table.setValue("Circ.", r, array[r]);
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of double");
        }
    }
}
