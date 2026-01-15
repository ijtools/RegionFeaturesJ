/**
 * 
 */
package net.ijt.regfeat.morpho3d;

import java.util.Arrays;
import java.util.Collection;

import inra.ijpb.geometry.PointPair3D;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.SingleValueFeature;
import net.ijt.regfeat.morpho3d.core.FurthestBoundaryPoints;

/**
 * Computes the largest Feret diameter of each region within a 3D label map.
 */
public class MaxFeretDiameter3D extends SingleValueFeature
{
    /**
     * Default empty constructor.
     */
    public MaxFeretDiameter3D()
    {
        super("Max_Feret_Diameter");
    }

    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        PointPair3D[] pairs = (PointPair3D[]) data.results.get(FurthestBoundaryPoints.class);

        // simply converts each pair of points to their Euclidean distance
        return Arrays.stream(pairs)
                .mapToDouble(pair -> pair != null ? pair.diameter() : Double.NaN)
                .toArray();
    }

    @Override
    public Collection<Class<? extends Feature>>requiredFeatures()
    {
        return Arrays.asList(FurthestBoundaryPoints.class);
    }

    @Override
    public String[] columnUnitNames(RegionFeatures data)
    {
        return new String[] {data.labelMap.getCalibration().getUnit()};
    }
}
