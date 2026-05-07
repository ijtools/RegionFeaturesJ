/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import java.util.Arrays;
import java.util.Collection;

import ij.measure.ResultsTable;
import inra.ijpb.algo.AlgoStub;
import inra.ijpb.geometry.Box2D;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.RegionTabularFeature;

/**
 * Computes the extent of the bounds of each region within a label map.
 * 
 * @see Bounds
 */
public class BoundsExtent extends AlgoStub implements RegionTabularFeature
{
    /**
     * The names of the columns of the resulting table.
     */
    public static final String[] colNames = new String[] {"Bounds_SizeX", "Bounds_SizeY"};
    
    /**
     * Default empty constructor.
     */
    public BoundsExtent()
    {
    }
    
    @Override
    public double[][] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        Box2D[] boxes = (Box2D[]) data.results.get(Bounds.class);
        
        // iterate over labels to compute new feature
        return Arrays.stream(boxes)
                .map(box -> new double[] { box.width(), box.height() })
                .toArray(double[][]::new);
    }
    
    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        double[][] extents = (double[][]) data.results.get(this.getClass());
        for (int i = 0; i < extents.length; i++)
        {
            // current extent
            double[] ext = extents[i];
            
            // extent value along each dimension
            table.setValue(colNames[0], i, ext[0]);
            table.setValue(colNames[1], i, ext[1]);
        }
    }
    
    @Override
    public String[] columnUnitNames(RegionFeatures data)
    {
        String unit = data.labelMap.getCalibration().getUnit();
        return new String[] {unit, unit};
    }
    
    
    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(Bounds.class);
    }
}
