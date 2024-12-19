/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import java.util.Arrays;
import java.util.Collection;

import ij.measure.ResultsTable;
import inra.ijpb.geometry.PointPair2D;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeature;
import net.ijt.regfeat.RegionFeatures;

/**
 * The tortuosity, defined as the ratio of Geodesic diameter over Max Feret
 * Diameter.
 */
public class Tortuosity extends RegionFeature
{
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        ensureRequiredFeaturesAreComputed(data);
        double[] geodDiams = (double[]) data.results.get(GeodesicDiameter.class);
        PointPair2D[] feretDiams = (PointPair2D[]) data.results.get(MaxFeretDiameter.class);
        
        // iterate over labels to compute new feature
        int[] labels = data.labels;
        double[] res = new double[labels.length];
        for (int i = 0; i < labels.length; i++)
        {
            double gd = geodDiams[i];
            double fd = feretDiams[i].diameter();
            res[i] = gd / fd;;
        }
        return res;
    }
    
    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        Object obj = data.results.get(Tortuosity.class);
        if (obj instanceof double[])
        {
            double[] array = (double[]) obj;
            for (int r = 0; r < array.length; r++)
            {
                table.setValue("Tortuosity", r, array[r]);
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of double");
        }
    }
    
    @Override
    public Collection<Class<? extends Feature>>requiredFeatures()
    {
        return Arrays.asList(GeodesicDiameter.class, MaxFeretDiameter.class);
    }
}
