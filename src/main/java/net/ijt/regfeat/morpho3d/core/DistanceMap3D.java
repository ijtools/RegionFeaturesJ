/**
 * 
 */
package net.ijt.regfeat.morpho3d.core;

import java.util.Arrays;
import java.util.Collection;

import ij.ImagePlus;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;

/**
 * Computes the 3 distance map that associates to each voxel within a region,
 * the distance to the nearest pixel outside the region (can be background or
 * another region).
 * 
 */
public class DistanceMap3D implements Feature
{
    /**
     * Default empty constructor.
     */
    public DistanceMap3D()
    {
    }

    @Override
    public ImagePlus compute(RegionFeatures data)
    {
        return (ImagePlus) data.results.get(DistanceMap3D_Chamfer_Float_Svensson.class);
    }

    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(DistanceMap3D_Chamfer_Float_Svensson.class);
    }

}
