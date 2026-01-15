/**
 * 
 */
package net.ijt.regfeat.morpho3d.core;

import java.util.Arrays;
import java.util.Collection;

import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.ResultsTable;
import inra.ijpb.algo.AlgoStub;
import inra.ijpb.data.Cursor3D;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.RegionTabularFeature;

/**
 * Computes the position of the maximum values within the 3D distance map.
 * 
 * @see DistanceMap3D
 */
public class DistanceMap3DMaximaPosition extends AlgoStub implements RegionTabularFeature
{
    /**
     * Default empty constructor.
     */
    public DistanceMap3DMaximaPosition()
    {
    }
    
    @Override
    public Cursor3D[] compute(RegionFeatures data)
    {
        // retrieve label map and list of labels
        ImageStack labelMap = data.labelMap.getStack();
        int[] labels = data.labels;
        
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        ImageStack distanceMap = ((ImagePlus) data.results.get(DistanceMap3D.class)).getStack();
        
        // Init Position and value of maximum for each label
        int nLabels = labels.length;
        Cursor3D[] posMax  = new Cursor3D[nLabels];
        double[] maxValues = new double[nLabels];
        for (int i = 0; i < nLabels; i++) 
        {
            maxValues[i] = Float.NEGATIVE_INFINITY;
            posMax[i] = new Cursor3D(-1, -1, -1);
        }
        
        // iterate on image pixels
        int sizeX = labelMap.getWidth();
        int sizeY = labelMap.getHeight();
        int sizeZ = labelMap.getSize();
        for (int z = 0; z < sizeZ; z++) 
        {
            for (int y = 0; y < sizeY; y++) 
            {
                for (int x = 0; x < sizeX; x++) 
                {
                    // retrieve current label
                    int label = (int) labelMap.getVoxel(x, y, z);

                    // do not process pixels that do not belong to any particle
                    if (label == 0)
                        continue;
                    // do not process labels not in the list
                    if (!data.labelIndices.containsKey(label))
                        continue;

                    int index = data.labelIndices.get(label);

                    // update values and positions
                    double value = distanceMap.getVoxel(x, y, z);
                    if (value > maxValues[index]) 
                    {
                        posMax[index].set(x, y, z);
                        maxValues[index] = value;
                    }
                }
            }
        }                
        return posMax;
    }
    
    
    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof Cursor3D[])
        {
            Cursor3D[] array = (Cursor3D[]) obj;
            for (int r = 0; r < array.length; r++)
            {
                Cursor3D pos = array[r];
                if (pos != null)
                {
                    // coordinates of inscribed disk center
                    table.setValue("DistanceMap_Maxima_X", r, pos.getX());
                    table.setValue("DistanceMap_Maxima_Y", r, pos.getY());
                    table.setValue("DistanceMap_Maxima_Z", r, pos.getZ());
                }
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of Cursor3D");
        }
    }

    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(DistanceMap3D.class);
    }
}
