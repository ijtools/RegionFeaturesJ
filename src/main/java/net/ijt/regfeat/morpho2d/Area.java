/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import ij.measure.ResultsTable;
import inra.ijpb.measure.region2d.IntrinsicVolumes2D;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;

/**
 * A feature that computes the area of 2D regions.
 */
public class Area extends Feature
{
    public Area()
    {
        this.requiredFeatures.add(IntrinsicVolumes.class);
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        ensureRequiredFeaturesAreComputed(data);
        IntrinsicVolumes2D.Result[] res = (IntrinsicVolumes2D.Result[]) data.results.get(IntrinsicVolumes.class);
        
        double[] areas = new double[res.length];
        for (int i = 0; i < res.length; i++)
        {
            areas[i] = res[i].area;
        }
        return areas;
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
                table.setValue("Area", r, array[r]);
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of double");
        }
    }
}
