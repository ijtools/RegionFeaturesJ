/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import java.util.Arrays;
import java.util.Collection;

import ij.measure.ResultsTable;
import inra.ijpb.geometry.OrientedBox2D;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeature;
import net.ijt.regfeat.RegionFeatures;

/**
 * Elongation of oriented bounding box.
 * 
 * @see OrientedBoundingBox.
 */
public class OrientedBoxElongation extends RegionFeature
{
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        OrientedBox2D[] OrientedBoxs = (OrientedBox2D[]) data.results.get(OrientedBoundingBox.class);
        
        // iterate over labels to compute new feature
        return Arrays.stream(OrientedBoxs)
            .mapToDouble(box -> box.length() / box.width())
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
                table.setValue("OrientedBox.Elong", r, array[r]);
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
        return Arrays.asList(OrientedBoundingBox.class);
    }
}
