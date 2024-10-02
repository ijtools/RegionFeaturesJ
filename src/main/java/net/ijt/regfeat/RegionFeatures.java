/**
 * 
 */
package net.ijt.regfeat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import inra.ijpb.label.LabelImages;

/**
 * The class containing results (and data?) for the analysis of regions within an image.
 */
public class RegionFeatures
{
    public static final RegionFeatures initialize(ImagePlus imagePlus)
    {
        return new RegionFeatures(imagePlus, LabelImages.findAllLabels(imagePlus));
    }
    
    public static final RegionFeatures initialize(ImagePlus imagePlus, int[] labels)
    {
        return new RegionFeatures(imagePlus, labels);
    }
    
    /**
     * The image containing the map of region label for each pixel / voxel.
     */
    public ImagePlus labelMap;
    
    /**
     * The labels of the regions to be analyzed.
     */
    public int[] labels;
    
    /**
     * The classes of the features that will be used to populate the data table.
     */
    Collection<Class<? extends Feature>> featureClasses = new ArrayList<>();
    
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
    
    public RegionFeatures(ImagePlus imagePlus, int[] labels)
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
    public void process(Class<? extends Feature> featureClass)
    {
        if (isComputed(featureClass)) return;
        
        Feature feature = getFeature(featureClass);
        feature.ensureRequiredFeaturesAreComputed(this);
        
        // compute feature, and index into results
        this.results.put(featureClass, feature.compute(this));
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
    
    public RegionFeatures add(Class<? extends Feature> featureClass)
    {
        this.featureClasses.add(featureClass);
        return this;
    }
    
    public boolean contains(Class<? extends Feature> featureClass)
    {
        return this.featureClasses.contains(featureClass);
    }
    
    public RegionFeatures computeAll()
    {
        this.featureClasses.stream().forEach(this::process);
        return this;
    }
    
    public ResultsTable createTable()
    {
        // ensure everything is computed
        computeAll();
        
        ResultsTable table = initializeTable(this.labels);
        
        for (Class<? extends Feature> featureClass : this.featureClasses)
        {
            if (!isComputed(featureClass))
            {
                throw new RuntimeException("Feature has not been computed: " + featureClass);
            }
            
            Feature feature = getFeature(featureClass);
            feature.updateTable(table, this);
        }
        return table;
    }
    
    private static final ResultsTable initializeTable(int[] labels)
    {
        // Initialize label column in table
        ResultsTable table = new ResultsTable();
        for (int i = 0; i < labels.length; i++)
        {
            table.incrementCounter();
            table.setLabel("" + labels[i], i);
        }
        return table;
    }

    public void printComputedFeatures()
    {
        results.keySet().stream().forEach(c -> System.out.println(c.getSimpleName()));
    }
}
