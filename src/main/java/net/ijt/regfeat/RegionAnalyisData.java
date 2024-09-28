/**
 * 
 */
package net.ijt.regfeat;

import java.util.HashMap;
import java.util.Map;

import ij.ImagePlus;
import ij.measure.ResultsTable;

/**
 * The class containing results (and data?) for the analysis of regions within an image.
 */
public class RegionAnalyisData
{
    /**
     * The image containing the map of region label for each pixel / voxel.
     */
    public ImagePlus labelMap;
    
    /**
     * The labels of the regions to be analyzed.
     */
    public int[] labels;
    
    /**
     * The map of features indexed by their class. When feature is created for
     * the first time, it is indexed within the results class to retrieve it in
     * case it is requested later.
     */
    public Map<Class<? extends Feature>, Feature> features;
    
    /**
     * The results computed for each feature. 
     */
    public Map<Class<? extends Feature>, Object> results;
    
    public RegionAnalyisData(ImagePlus imagePlus, int[] labels)
    {
        this.labelMap = imagePlus;
        this.labels = labels;
        
        this.features = new HashMap<Class<? extends Feature>, Feature>();
        this.results = new HashMap<Class<? extends Feature>, Object>();
    }
    
    /**
     * Updates the informations stored within this result class with the feature
     * identified by the specified class, if it is not already computed.
     * 
     * @param featureClass
     *            the class to compute
     */
    public void updateWith(Class<? extends Feature> featureClass)
    {
        if (isComputed(featureClass)) return;
        
        Feature feature = getFeature(featureClass);
        
        ensureRequiredFeaturesAreComputed(feature);
        
        // compute feature, and index into results
        this.results.put(featureClass, feature.compute(this));
    }
    
    public void ensureRequiredFeaturesAreComputed(Feature feature)
    {
        for (Class<? extends Feature> fClass : feature.requiredFeatures())
        {
            if (!isComputed(fClass))
            {
                updateWith(fClass);
            }
        }
    }
    
    public boolean isComputed(Class<? extends Feature> featureClass)
    {
        return results.containsKey(featureClass);
    }
    
    public Feature getFeature(Class<? extends Feature> featureClass)
    {
        Feature feature = this.features.get(featureClass);
        if (feature == null)
        {
            feature = Feature.create(featureClass);
            this.features.put(featureClass, feature);
        }
        return feature;
    }
    
    @SuppressWarnings("unchecked")
    public ResultsTable createTable(Class<? extends Feature>... classes)
    {
        // manually check validity of input parameters
        for (Class<?> clazz : classes)
        {
            if (!Feature.class.isAssignableFrom(clazz))
            {
                throw new RuntimeException("Requires the class arguments to inherits the Feature class");
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
                Feature feature = getFeature(featureClass);
                Object res = results.get(featureClass);
                feature.populateTable(table, res);
            }
        }
        return table;
    }
    
    public void printComputedFeatures()
    {
        results.keySet().stream().forEach(c -> System.out.println(c.getSimpleName()));
    }
}
