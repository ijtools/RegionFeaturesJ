/**
 * 
 */
package net.ijt.regfeat.morpho3d.core;

import java.util.Collection;

import ij.ImagePlus;
import ij.ImageStack;
import inra.ijpb.algo.AlgoStub;
import inra.ijpb.binary.distmap.ChamferMask3D;
import inra.ijpb.binary.distmap.ChamferMask3D.FloatOffset;
import inra.ijpb.data.image.Image3D;
import inra.ijpb.data.image.Images3D;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;

/**
 * Computes the 3 distance map that associates to each voxel within a region,
 * the distance to the nearest pixel outside the region (can be background or
 * another region).
 * 
 * Uses a chamfer distance map computed with floating point, and a a set of four
 * weights proposed by Svensson: 3 for orthogonal neighbors, 4 for
 * square-diagonal neighbors, 5 for cube-diagonals, and 7 for (2,1,1) shifts.
 * 
 */
// Note: re-implement from MorphoLibJ, as there is a bug for versions of
// MorphoLibJ up to 1.6.4.
public class DistanceMap3D_Chamfer_Float_Svensson extends AlgoStub implements Feature
{
    /**
     * The chamfer mask used to propagate distances to neighbor voxels.
     */
    ChamferMask3D chamferMask;
    
    /**
     * Default empty constructor.
     */
    public DistanceMap3D_Chamfer_Float_Svensson()
    {
        // setup Chamfer mask to Svensson
        this.chamferMask = ChamferMask3D.SVENSSON_3_4_5_7;
    }
    
    @Override
    public ImagePlus compute(RegionFeatures data)
    {
        // create Image3D wrapper to 3D label map
        Image3D labelMap = Images3D.createWrapper(data.labelMap.getStack());
        int sizeX = labelMap.getSize(0);
        int sizeY = labelMap.getSize(1);
        int sizeZ = labelMap.getSize(2);

        // create new empty image, and fill it with black
        ImageStack resultStack = ImageStack.create(sizeX, sizeY, sizeZ, 32);
        Image3D distMap = Images3D.createWrapper(resultStack);
        
        initializeResultSlices(labelMap, distMap);
        
        // Two iterations are enough to compute distance map to boundary
        forwardScan(labelMap, distMap);
        backwardScan(labelMap, distMap);

        // Normalize values by the first weight
        normalizeResult(labelMap, distMap); 
                
        fireStatusChanged(this, "");
        
        String newName = data.labelMap.getShortTitle() + "-distMap";
        ImagePlus resultPlus = new ImagePlus(newName, resultStack);

        return resultPlus;
    }
    
    // ==================================================
    // Inner computation methods 
    
    /**
     * Fill result image with zero for background voxels, and Short.MAX for
     * foreground voxels.
     */
    private void initializeResultSlices(Image3D labels, Image3D distMap)
    {
        fireStatusChanged(this, "Initialization...");
        
        // retrieve image dimensions
        int sizeX = labels.getSize(0);
        int sizeY = labels.getSize(1);
        int sizeZ = labels.getSize(2);

        // iterate over slices
        for (int z = 0; z < sizeZ; z++) 
        {
            fireProgressChanged(this, z, sizeZ);
            
            for (int y = 0; y < sizeY; y++) 
            {
                for (int x = 0; x < sizeX; x++) 
                {
                    int label = (int) labels.getValue(x, y, z);
                    distMap.setValue(x, y, z, label == 0 ? 0 : Float.MAX_VALUE);
                }
            }
        }
        fireProgressChanged(this, 1, 1); 
    }

    private void forwardScan(Image3D labels, Image3D distMap) 
    {
        fireStatusChanged(this, "Forward scan..."); 
        
        // retrieve image dimensions
        int sizeX = labels.getSize(0);
        int sizeY = labels.getSize(1);
        int sizeZ = labels.getSize(2);
        
        // create array of forward shifts
        Collection<FloatOffset> offsets = this.chamferMask.getForwardFloatOffsets();

        // iterate on image voxels
        for (int z = 0; z < sizeZ; z++)
        {
            fireProgressChanged(this, z, sizeZ); 
            
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    // get current label
                    int label = (int) labels.getValue(x, y, z);
                    
                    // do not process background pixels
                    if (label == 0)
                        continue;
                    
                    // current distance value
                    double currentDist = distMap.getValue(x, y, z);
                    double newDist = currentDist;

                    // iterate over forward offsets defined by ChamferWeights
                    for (FloatOffset offset : offsets)
                    {
                        int x2 = x + offset.dx;
                        int y2 = y + offset.dy;
                        int z2 = z + offset.dz;
                        
                        // check bounds
                        if (x2 < 0 || x2 >= sizeX) continue;
                        if (y2 < 0 || y2 >= sizeY) continue;
                        if (z2 < 0 || z2 >= sizeZ) continue;
                        
                        if (((int) labels.getValue(x2, y2, z2)) != label)
                        {
                            // Update with distance to nearest different label
                            newDist = Math.min(newDist, offset.weight);
                        }
                        else
                        {
                            // Increment distance
                            newDist = Math.min(newDist, distMap.getValue(x2, y2, z2) + offset.weight);
                        }
                    }
                    
                    if (newDist < currentDist) 
                    {
                        distMap.setValue(x, y, z, newDist);
                    }
                }
            }
        }
        fireProgressChanged(this, 1, 1); 
    }
    
    private void backwardScan(Image3D labels, Image3D distMap) 
    {
        fireStatusChanged(this, "Backward scan..."); 
        
        // retrieve image dimensions
        int sizeX = labels.getSize(0);
        int sizeY = labels.getSize(1);
        int sizeZ = labels.getSize(2);
        
        // create array of backward shifts
        Collection<FloatOffset> offsets = this.chamferMask.getBackwardFloatOffsets();
        
        // iterate on image voxels in backward order
        for (int z = sizeZ - 1; z >= 0; z--)
        {
            fireProgressChanged(this, sizeZ-1-z, sizeZ);
            
            for (int y = sizeY - 1; y >= 0; y--)
            {
                for (int x = sizeX - 1; x >= 0; x--)
                {
                    // get current label
                    int label = (int) labels.getValue(x, y, z);
                    
                    // do not process background pixels
                    if (label == 0)
                        continue;
                    
                    // current distance value
                    double currentDist = distMap.getValue(x, y, z);
                    double newDist = currentDist;
                    
                    // iterate over backward offsets defined by ChamferWeights
                    for (FloatOffset offset : offsets)
                    {
                        int x2 = x + offset.dx;
                        int y2 = y + offset.dy;
                        int z2 = z + offset.dz;
                        
                        // check bounds
                        if (x2 < 0 || x2 >= sizeX) continue;
                        if (y2 < 0 || y2 >= sizeY) continue;
                        if (z2 < 0 || z2 >= sizeZ) continue;
                        
                        if (((int) labels.getValue(x2, y2, z2)) != label)
                        {
                            // Update with distance to nearest different label
                            newDist = Math.min(newDist, offset.weight);
                        }
                        else
                        {
                            // Increment distance
                            newDist = Math.min(newDist, distMap.getValue(x2, y2, z2) + offset.weight);
                        }
                    }
                    
                    if (newDist < currentDist) 
                    {
                        distMap.setValue(x, y, z, newDist);
                    }
                }
            }
        }
        fireProgressChanged(this, 1, 1); 
    }
    
    private void normalizeResult(Image3D labels, Image3D distMap)
    {
        fireStatusChanged(this, "Normalize map..."); 
        
        // retrieve the minimum weight
        double w0 = this.chamferMask.getNormalizationWeight();
        
        // retrieve image dimensions
        int sizeX = labels.getSize(0);
        int sizeY = labels.getSize(1);
        int sizeZ = labels.getSize(2);

        for (int z = 0; z < sizeZ; z++) 
        {
            fireProgressChanged(this, z, sizeZ);
            
            for (int y = 0; y < sizeY; y++) 
            {
                for (int x = 0; x < sizeX; x++) 
                {
                    if (((int) labels.get(x, y, z)) != 0)
                    {
                        distMap.setValue(x, y, z, distMap.getValue(x, y, z) / w0);
                    }
                }
            }
        }
        
        fireProgressChanged(this, 1, 1); 
    }
}
