package net.ijt.regfeat.morpho2d;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.DoubleStream;

import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.SingleValueFeature;

/**
 * The diameter of the disk with same area as the region.
 * 
 * @see Area
 */
public class EquivalentDiameter extends SingleValueFeature
{
    /**
     * Default empty constructor.
     */
    public EquivalentDiameter()
    {
        super("Equivalent_Diameter");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        double[] areas = (double[]) data.results.get(Area.class);
        
        // iterate over labels to compute new feature
        return DoubleStream.of(areas).map(a -> 2.0 * Math.sqrt(a / Math.PI)).toArray();
    }
    
    @Override
    public String[] columnUnitNames(RegionFeatures data)
    {
        return new String[] {data.labelMap.getCalibration().getUnit()};
    }

    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(Area.class);
    }
}
