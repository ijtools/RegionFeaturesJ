/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import java.util.Arrays;

import ij.measure.ResultsTable;
import inra.ijpb.geometry.Ellipse;
import net.ijt.regfeat.RegionFeature;
import net.ijt.regfeat.RegionFeatures;

/**
 * Elongation of Equivalent ellipse.
 * 
 * @see EquivalentEllipse.
 */
public class EllipseElongation extends RegionFeature
{
    public EllipseElongation()
    {
        this.requiredFeatures.add(EquivalentEllipse.class);
    }
    

    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        ensureRequiredFeaturesAreComputed(data);
        Ellipse[] ellipses = (Ellipse[]) data.results.get(EquivalentEllipse.class);
        
        // iterate over labels to compute new feature
        return Arrays.stream(ellipses)
            .mapToDouble(elli -> elli.radius1() / elli.radius2())
            .toArray();
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
                table.setValue("Ellipse.Elong", r, array[r]);
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of double");
        }
    }

}
