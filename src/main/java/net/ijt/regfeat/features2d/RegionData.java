package net.ijt.regfeat.features2d;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class that encapsulates several data computed on a single region.
 */
public class RegionData
{
    /**
     * The label of the region, as an integer.
     */
    int label;
    
    /**
     * The data computed on this region, as a map between the feature class name
     * and the feature result.
     */
    Map<String, Object> features = new HashMap<String, Object>();
    
    /**
     * Creates a new data structure for storing region analysis data for the
     * region with label <code>label</code>.
     * 
     * @param label
     *            the integer label of the region
     */
    public RegionData(int label)
    {
        this.label = label;
    }
    
    
    public Object getFeature(String featureId)
    {
        return features.get(featureId);
    }
    
    public void setFeature(String featureId, Object featureValue)
    {
        features.put(featureId, featureValue);
    }
}
