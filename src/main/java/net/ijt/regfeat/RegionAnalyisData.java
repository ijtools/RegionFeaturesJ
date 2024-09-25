/**
 * 
 */
package net.ijt.regfeat;

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
    public int[] labels;
    
    /**
     * The image containing the map of region label for each pixel / voxel.
     */
    public ImagePlus labelMap;
    
    /**
     * The features computed for each region.
     */
    public Map<Integer, Map<Class<? extends Feature>, Object>> regionData;
    
    /**
     * The list of features that have been computed.
     */
    Collection<Class<? extends Feature>> computedFeatures;
    
    public RegionAnalyisData(ImagePlus imagePlus, int[] labels)
    {
        this.labelMap = imagePlus;
        this.labels = labels;
        
        this.regionData = new HashMap<Integer, Map<Class<? extends Feature>, Object>>();
        for (int label : labels)
        {
            this.regionData.put(label, new HashMap<Class<? extends Feature>, Object>());
        }
        
        computedFeatures = new HashSet<>();
    }
    
    public void updateWith(Class<? extends Feature> featureClass)
    {
        if (isComputed(featureClass)) return;
        
        Feature feature = Feature.create(featureClass);
        feature.updateData(this);
        
        setAsComputed(featureClass);
    }
    
    public boolean isComputed(Class<? extends Feature> featureClass)
    {
        return computedFeatures.contains(featureClass);
    }
    
    public void setAsComputed(Class<? extends Feature> featureClass)
    {
        computedFeatures.add(featureClass);
    }
    
    @SuppressWarnings("unchecked")
    public ResultsTable createTable(Class<? extends Feature>... classes)
    {
        // manually check validity of input parameters
        for (Class<?> clazz : classes)
        {
            if (!Feature.class.isAssignableFrom(clazz))
            {
                throw new RuntimeException("Requires the class arguments to inherots the Feature class");
            }
        }
        
        // Initialize labels
        int nLabels = labels.length;
        ResultsTable table = new ResultsTable();
        for (int i = 0; i < nLabels; i++)
        {
            table.incrementCounter();
            table.setLabel("" + labels[i], i);
        }
        
        for (Class<? extends Feature> featureClass : classes)
        {
            if (!isComputed(featureClass))
            {
                throw new RuntimeException("Feature has not been computed: " + featureClass);
            }
            
            for (int i = 0; i < labels.length; i++)
            {
                Feature feature = Feature.create(featureClass);
                feature.populateTable(table, i, regionData.get(labels[i]).get(featureClass));
            }
        }
        return table;
    }
    
    public void printComputedFeatures()
    {
        computedFeatures.stream().forEach(c -> System.out.println(c.getSimpleName()));
    }
}
