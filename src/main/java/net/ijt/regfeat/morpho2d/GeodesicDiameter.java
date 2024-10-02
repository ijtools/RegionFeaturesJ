/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import ij.measure.ResultsTable;
import inra.ijpb.measure.region2d.GeodesicDiameter.Result;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.morpho2d.core.GeodesicDiameterData;

/**
 * Computes the convex area, or area of the convex hull.
 */
public class GeodesicDiameter extends Feature
{
    public GeodesicDiameter()
    {
        this.requiredFeatures.add(GeodesicDiameterData.class);
    }
    
    @Override
    public Object compute(RegionFeatures data)
    {
        // retrieve required feature values
        ensureRequiredFeaturesAreComputed(data);
        Result[] results = (Result[]) data.results.get(GeodesicDiameterData.class);
        
        // iterate over labels
        double[] geodDiams = new double[results.length];
        for (int i = 0; i < results.length; i++)
        {
            geodDiams[i] = results[i].diameter;
        }
        
        return geodDiams;
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
                table.setValue("Geodesic_Diameter", r, array[r]);
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of double");
        }
    }

}
