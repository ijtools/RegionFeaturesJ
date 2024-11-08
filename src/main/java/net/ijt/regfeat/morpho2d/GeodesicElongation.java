/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import ij.measure.ResultsTable;
import inra.ijpb.geometry.Circle2D;
import net.ijt.regfeat.RegionFeature;
import net.ijt.regfeat.RegionFeatures;

/**
 * The GeodesicElongation, defined as the ratio of Geodesic diameter over
 * diameter of largest inscribed disk.
 */
public class GeodesicElongation extends RegionFeature
{
    public GeodesicElongation()
    {
        this.requiredFeatures.add(GeodesicDiameter.class);
        this.requiredFeatures.add(LargestInscribedDisk.class);
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        ensureRequiredFeaturesAreComputed(data);
        double[] geodDiams = (double[]) data.results.get(GeodesicDiameter.class);
        Circle2D[] circles = (Circle2D[]) data.results.get(LargestInscribedDisk.class);
        
        // iterate over labels to compute new feature
        int[] labels = data.labels;
        double[] res = new double[labels.length];
        for (int i = 0; i < labels.length; i++)
        {
            double gd = geodDiams[i];
            double cd = circles[i].getRadius() * 2;
            res[i] = gd / cd;
        }
        return res;
    }
    
    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        Object obj = data.results.get(GeodesicElongation.class);
        if (obj instanceof double[])
        {
            double[] array = (double[]) obj;
            for (int r = 0; r < array.length; r++)
            {
                table.setValue("GeodesicElongation", r, array[r]);
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of double");
        }
    }
}
