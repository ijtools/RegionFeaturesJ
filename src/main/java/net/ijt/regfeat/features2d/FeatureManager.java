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
    
    
    Map<Class<? extends Feature>, Feature> features = new HashMap<Class<? extends Feature>, Feature>();
    
    
    public Feature getFeature(Class<? extends Feature> featureClass)
    {
        return features.get(featureClass);
    }
    
    public void addFeature(Feature feature)
    {
        if (features.containsKey(feature.getClass()))
        {
            throw new RuntimeException("FeatureManager already contains a feature with class: " + feature.getClass());
        }
        features.put(feature.getClass(), feature);
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

