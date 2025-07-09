/**
 * 
 */
package net.ijt.regfeat.morpho2d.core;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import inra.ijpb.binary.distmap.ChamferMask2D;
import inra.ijpb.label.distmap.ChamferDistanceTransform2DFloat;
import inra.ijpb.label.distmap.DistanceTransform2D;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;

/**
 * Computes the distance map that associates to each pixel within a region, the
 * distance to the nearest pixel outside the region.
 * 
 * Uses a chamfer distance map computed with floating point, and a "Chessknight"
 * chamfer mask.
 */
public class DistanceMap_Chamfer_ChessKnight_Float implements Feature
{
    /**
     * Default empty constructor.
     */
    public DistanceMap_Chamfer_ChessKnight_Float()
    {
    }
    
    @Override
    public ImagePlus compute(RegionFeatures data)
    {
        String newName = data.labelMap.getShortTitle() + "-distMap";
        DistanceTransform2D algo = new ChamferDistanceTransform2DFloat(ChamferMask2D.CHESSKNIGHT);
        ImageProcessor distanceMap = algo.distanceMap(data.labelMap.getProcessor());
        return new ImagePlus(newName, distanceMap);
    }
}
