/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import java.util.Arrays;

import ij.measure.ResultsTable;
import inra.ijpb.geometry.Ellipse;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;

/**
 * 
 */
public class EllipseElongation extends Feature
{
    public EllipseElongation()
    {
        this.requiredFeatures.add(EquivalentEllipse.class);
    }
    

    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        Ellipse[] ellipses = (Ellipse[]) data.results.get(EquivalentEllipse.class);
        
        // iterate over labels to compute new feature
        return Arrays.stream(ellipses)
            .mapToDouble(elli -> elli.radius1() / elli.radius2())
            .toArray();
    }

    @Override
    public void populateTable(ResultsTable table, Object obj)
    {
        if (obj instanceof double[])
        {
            double[] array = (double[]) obj;
            for (int r = 0; r < array.length; r++)
            {
                table.setValue("Ellipse.Elong", r, array[r]);
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of double");
        }
    }

}
