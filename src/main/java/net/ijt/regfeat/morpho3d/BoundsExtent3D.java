/**
 * 
 */
package net.ijt.regfeat.morpho3d;

import java.util.Arrays;
import java.util.Collection;

import ij.measure.ResultsTable;
import inra.ijpb.algo.AlgoStub;
import inra.ijpb.geometry.Box3D;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.RegionTabularFeature;

/**
 * Computes the extent of the bounds of each region within a label map.
 * 
 * @see Bounds
 */
public class BoundsExtent3D extends AlgoStub implements RegionTabularFeature
{
    /**
     * The names of the columns of the resulting table.
     */
    public static final String[] colNames = new String[] {"Bounds_SizeX", "Bounds_SizeY", "Bounds_SizeZ"};
    
    /**
     * Default empty constructor.
     */
    public BoundsExtent3D()
    {
    }
    
    @Override
    public double[][] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        Box3D[] boxes = (Box3D[]) data.results.get(Bounds3D.class);
        
        // iterate over labels to compute new feature
        return Arrays.stream(boxes)
                .map(box -> new double[] { box.width(), box.height(), box.depth() })
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
            table.setValue(colNames[2], i, ext[2]);
        }
    }
    
    @Override
    public String[] columnUnitNames(RegionFeatures data)
    {
        String unit = data.labelMap.getCalibration().getUnit();
        return new String[] {unit, unit, unit};
    }
    
    
    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(Bounds3D.class);
    }
}
