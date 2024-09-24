/**
 * 
 */
package net.ijt.regfeat.features2d;

import java.util.ArrayList;
import java.util.Collection;

import ij.measure.ResultsTable;

/**
 * Abstract class for a feature that can compute something on a region. 
 */
public abstract class Feature
{
    /**
     * The identifier for this feature. Must be unique within the available features.
     */
    private String id;
    
    /**
     * The list of features that are required to compute this feature.
     */
    protected Collection<Feature> requiredFeatures = new ArrayList<Feature>(2);
    
    
    protected Feature(String id)
    {
        this.id = id;
    }
    
    
    public void updateData(RegionAnalyisData results)
    {
        String id = getId();
        System.out.println("start computing feature: " + id);
        if (results.isComputed(getId())) return;
        System.out.println("  compute it");
        
        // check required features
        for (Feature f : requiredFeatures)
        {
            f.updateData(results);
        }
        
        // process computation
        Object[] res = compute(results.labels, results);
        for (int i = 0; i < results.labels.length; i++)
        {
            results.regionData.get(results.labels[i]).features.put(id, res[i]);
        }
        
        results.setAsComputed(id);
    }
    
    /**
     * Computes the feature for each of the regions specified by labels in
     * <code>labels</code>, based on the data stored in <code>results</code>.
     * 
     * @see RegionAnalysisData#updateWith(Feature)
     * 
     * @param labels
     *            the labels of the regions to analyze
     * @param results
     *            a data structure containing data for computing this feature
     * @return an array the same size as labels containing result of analysis
     *         for each region
     */
    public abstract Object[] compute(int[] labels, RegionAnalyisData results);
    
    public abstract void populateTable(ResultsTable table, int row, Object value);
    
    
    public String getId()
    {
        return this.id;
    }

}
