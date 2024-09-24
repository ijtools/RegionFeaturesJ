/**
 * 
 */
package net.ijt.regfeat.features2d;

import java.util.HashMap;
import java.util.Map;

/**
 * The manager of features.
 * 
 * Singleton class.
 */
public class FeatureManager
{
    private static FeatureManager instance = null;
    
    public static final FeatureManager getInstance()
    {
        // lazy instantiation of FeatureManager instance
        if (instance == null)
        {
            instance = new FeatureManager();
        }
        return instance;
    }
    
    
    Map<String, Feature> features = new HashMap<String, Feature>();
    
    
    public Feature getFeature(String featureId)
    {
        return features.get(featureId);
    }
    
    public void addFeature(Feature feature)
    {
        if (features.containsKey(feature.getId()))
        {
            throw new RuntimeException("FeatureManager already contains a feature with id: " + feature.getId());
        }
        features.put(feature.getId(), feature);
    }
    
    public void printFeatureList()
    {
        features.keySet().stream().forEach(System.out::println);
    }
    
    /**
     * Private constructor to avoid multiple instances.
     */
    private FeatureManager()
    {
    }
}

