/**
 * 
 */
package net.ijt.regfeat.features2d;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import ij.ImagePlus;
import ij.measure.ResultsTable;

/**
 * The class containing results (and data?) for the analysis of regions within an image.
 */
public class RegionAnalyisData
{
    /**
     * The labels of the regions to be analyzed.
     */
    int[] labels;
    
    /**
     * The image containing the map of region label for each pixel / voxel.
     */
    ImagePlus labelMap;
    
    /**
     * The features computed for each region.
     */
    Map<Integer, RegionData> regionData;
    
    /**
     * A map of boolean flags identifying which features have been already
     * computed.
     */
    Collection<Class<? extends Feature>> computedFeatures;
    
    public RegionAnalyisData(ImagePlus imagePlus, int[] labels)
    {
        this.labelMap = imagePlus;
        this.labels = labels;
        
        this.regionData = new HashMap<Integer, RegionData>();
        for (int label : labels)
        {
            this.regionData.put(label, new RegionData(label));
        }
        
        computedFeatures = new HashSet<>();
    }
    
    public void updateWith(Feature feature)
    {
        if (isComputed(feature.getClass())) return;
        
        feature.updateData(this);
        
        setAsComputed(feature.getClass());
    }
    
    public boolean isComputed(Class<? extends Feature> featureClass)
    {
        return computedFeatures.contains(featureClass);
    }
    
    public void setAsComputed(Class<? extends Feature> featureClass)
    {
        computedFeatures.add(featureClass);
    }
    
    public ResultsTable createTable(Feature... features)
    {
        // Initialize labels
        int nLabels = labels.length;
        ResultsTable table = new ResultsTable();
        for (int i = 0; i < nLabels; i++)
        {
            table.incrementCounter();
            table.setLabel("" + labels[i], i);
        }
        
        for (Feature f : features)
        {
            if (!isComputed(f.getClass()))
            {
                throw new RuntimeException("Feature has not been computed: " + f.getClass());
            }
            
            for (int i = 0; i < labels.length; i++)
            {
                f.populateTable(table, i, regionData.get(labels[i]).getFeature(f.getClass()));
            }
        }
        return table;
    }
    
    
    public void printComputedFeatures()
    {
        computedFeatures.stream().forEach(c -> System.out.println(c.getSimpleName()));
    }
}
