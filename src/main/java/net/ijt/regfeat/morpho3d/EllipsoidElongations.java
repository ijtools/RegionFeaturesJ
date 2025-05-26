/**
 * 
 */
package net.ijt.regfeat.morpho3d;

import java.util.Arrays;
import java.util.Collection;

import ij.measure.ResultsTable;
import inra.ijpb.geometry.Ellipsoid;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionTabularFeature;
import net.ijt.regfeat.RegionFeatures;

/**
 * Elongation of Equivalent ellipse.
 * 
 * @see EquivalentEllipse.
 */
public class EllipsoidElongations implements RegionTabularFeature
{
    @Override
    public double[][] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        Ellipsoid[] ellipsoids = (Ellipsoid[]) data.results.get(EquivalentEllipsoid.class);
        
        // iterate over labels to compute new feature
        return Ellipsoid.elongations(ellipsoids);
    }

    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof double[][])
        {
            double[][] array = (double[][]) obj;
            for (int r = 0; r < array.length; r++)
            {
                // current ellipsoid
                double[] elongs = array[r];
                
                table.setValue("Ellipsoid_R1/R2", r, elongs[0]);
                table.setValue("Ellipsoid_R1/R3", r, elongs[1]);
                table.setValue("Ellipsoid_R2/R3", r, elongs[2]); 
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of double[]");
        }
    }

    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(EquivalentEllipsoid.class);
    }
}
