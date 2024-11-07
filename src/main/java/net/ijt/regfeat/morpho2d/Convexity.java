/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import ij.measure.ResultsTable;
import net.ijt.regfeat.RegionFeature;
import net.ijt.regfeat.RegionFeatures;

/**
 * Computes the convexity, as the ratio of area over convex area.
 * 
 * @see Area
 * @see ConvexArea
 */
public class Convexity extends RegionFeature
{
    public Convexity()
    {
        this.requiredFeatures.add(Area.class);
        this.requiredFeatures.add(ConvexArea.class);
    }
    
    @Override
    public Object compute(RegionFeatures data)
    {
        // retrieve required feature values
        ensureRequiredFeaturesAreComputed(data);
        double[] areas = (double[]) data.results.get(Area.class);
        double[] convexAreas = (double[]) data.results.get(ConvexArea.class);
        
        // iterate over labels
        double[] convexities = new double[areas.length];
        for (int i = 0; i < areas.length; i++)
        {
            convexities[i] = areas[i] / convexAreas[i];
        }
        
        return convexities;
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
                table.setValue("Convexity", r, array[r]);
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of double");
        }
    }

}
