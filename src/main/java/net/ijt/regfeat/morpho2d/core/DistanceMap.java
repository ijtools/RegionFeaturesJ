/**
 * 
 */
package net.ijt.regfeat.morpho2d.core;

import java.util.Arrays;
import java.util.Collection;

import ij.ImagePlus;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;

/**
 * Computes the distance map that associates to each pixel within a region, the
 * distance to the nearest pixel outside the region.
 * 
 * This class is an alias for the {@code DistanceMap_Chamfer_ChessKnight_Float}
 * class, that computes distance map using a chamfer mask with size 5-by-5, and
 * the system of weights (5, 7, 11).
 */
public class DistanceMap implements Feature
{
    /**
     * Default empty constructor.
     */
    public DistanceMap()
    {
    }
    
   @Override
    public ImagePlus compute(RegionFeatures data)
    {
        return (ImagePlus) data.results.get(DistanceMap_Chamfer_ChessKnight_Float.class);
    }

    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(DistanceMap_Chamfer_ChessKnight_Float.class);
    }
}
