package net.ijt.regfeat.morpho3d.core;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

import ij.ImageStack;
import inra.ijpb.algo.AlgoStub;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;

/**
 * Computes the coordinates of the points located at the middle of boundary
 * faces of each region. Points are stored in pixel coordinates, using a nesting
 * of maps of coordinates.
 * 
 * This feature is used for computing 3D Feret diameters.
 */
public class BoundaryFacesMidPoints extends AlgoStub implements Feature
{
    /**
     * Empty default constructor.
     */
    public BoundaryFacesMidPoints()
    {
    }

    @Override
    public TreeMap<Double, TreeMap<Double, TreeSet<Double>>>[] compute(RegionFeatures data)
    {
        // retrieve label map data
        ImageStack labelMap = data.labelMap.getStack();
        int sizeX = labelMap.getWidth();
        int sizeY = labelMap.getHeight();
        int sizeZ = labelMap.getSize();
        
        // label data
        HashMap<Integer, Integer> labelIndices = data.labelIndices;
        int nLabels = labelIndices.size();
        
        // allocate data structure for storing results
        // for each region, organize the boundary points within a map, using the
        // y-coordinate of the points as map key, and listing all the
        // x-coordinates within the row within an ArrayList
        @SuppressWarnings("unchecked")
        TreeMap<Double, TreeMap<Double, TreeSet<Double>>>[] pointMaps = (TreeMap<Double, TreeMap<Double, TreeSet<Double>>>[]) new TreeMap<?,?>[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
            pointMaps[i] = new TreeMap<Double, TreeMap<Double, TreeSet<Double>>>();
        }
        
        // labels for current, up, and left pixels.
        int label = 0;
        int labelPrevX = 0;
        int labelPrevY = 0;
        int labelPrevZ = 0;
        
        // iterate on image voxel configurations
        for (int z = 0; z < sizeZ + 1; z++) 
        {
            this.fireProgressChanged(this, z, sizeZ);

            labelPrevX = 0;
            for (int y = 0; y < sizeY + 1; y++) 
            {
                for (int x = 0; x < sizeX + 1; x++) 
                {
                    // retrieve value of current voxel
                    label = x < sizeX && y < sizeY && z < sizeZ ? (int) labelMap.getVoxel(x, y, z) : 0;
                    
                    // retrieve values of neighbors in backward neighborhood
                    // (prevX label is updated at the end of current row processing)
                    labelPrevY = x < sizeX && y > 0 && z < sizeZ ? (int) labelMap.getVoxel(x, y - 1, z) : 0;
                    labelPrevZ = x < sizeX && y < sizeY && z > 0 ? (int) labelMap.getVoxel(x, y, z - 1): 0;

                    // check transition with voxel with decremented z-coord
                    if (labelPrevZ != label)
                    {
                        if (labelIndices.containsKey(label))
                        {
                            int index = labelIndices.get(label);
                            addPoint(pointMaps[index], x + 0.5, y + 0.5, z);
                        }
                        if (labelIndices.containsKey(labelPrevZ))
                        {
                            int index = labelIndices.get(labelPrevZ);
                            addPoint(pointMaps[index], x + 0.5, y + 0.5, z);
                        }
                    }

                    // check transition with voxel with decremented y-coord
                    if (labelPrevY != label)
                    {
                        if (labelIndices.containsKey(label))
                        {
                            int index = labelIndices.get(label);
                            addPoint(pointMaps[index], x + 0.5, y, z + 0.5);
                        }
                        if (labelIndices.containsKey(labelPrevY))
                        {
                            int index = labelIndices.get(labelPrevY);
                            addPoint(pointMaps[index], x + 0.5, y, z + 0.5);
                        }
                    }

                    // check transition with voxel with decremented x-coord
                    if (labelPrevX != label)
                    {
                        if (labelIndices.containsKey(label))
                        {
                            int index = labelIndices.get(label);
                            addPoint(pointMaps[index], x, y + 0.5, z + 0.5);
                        }
                        if (labelIndices.containsKey(labelPrevX))
                        {
                            int index = labelIndices.get(labelPrevX);
                            addPoint(pointMaps[index], x, y + 0.5, z + 0.5);
                        }
                    }

                    // update values of left label for next iteration
                    labelPrevX = label;
                }
            }
        }

        return pointMaps;
    }

    private static final void addPoint(TreeMap<Double, TreeMap<Double, TreeSet<Double>>> map, double x, double y, double z)
    {
        // retrieve map for z-coordinate
        TreeMap<Double, TreeSet<Double>> mapZ = map.get(z);
        if (mapZ == null)
        {
            mapZ = new TreeMap<Double, TreeSet<Double>>();
        }
        
        // retrieve set for y-coordinate
        TreeSet<Double> set = mapZ.get(y);
        if (set == null)
        {
            set = new TreeSet<Double>();
        }
        
        // update nested data structure
        set.add(x);
        mapZ.put(y, set);
        map.put(z, mapZ);
    }
}
