/**
 * 
 */
package net.ijt.regfeat;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import ij.measure.ResultsTable;

/**
 * Abstract class for a feature that can compute something on a region. 
 */
public abstract class Feature
{
    public static final Feature create(Class<? extends Feature> featureClass)
    {
        try
        {
            Constructor<? extends Feature> cons = featureClass.getConstructor();
            return cons.newInstance();
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * The list of features that are required to compute this feature.
     */
    protected Collection<Class<? extends Feature>> requiredFeatures = new ArrayList<>(2);
    
    
    protected Feature()
    {
    }
    
    
    public void updateData(RegionAnalyisData results)
    {
        System.out.println("start computing feature: " + this.getClass().getSimpleName());
        if (results.isComputed(this.getClass())) return;
        System.out.println("  compute it");
        
        // check required features are computed
        ensureRequiredFeaturesAreComputed(results);
        
        // process computation
        Object[] res = compute(results);
        
        // update mapping into results data
        for (int i = 0; i < results.labels.length; i++)
        {
            results.regionData.get(results.labels[i]).put(this.getClass(), res[i]);
        }
        
        results.setAsComputed(this.getClass());
    }
    
    public void ensureRequiredFeaturesAreComputed(RegionAnalyisData results)
    {
        for (Class<? extends Feature> fClass : requiredFeatures)
        {
            Feature feature = create(fClass); // TODO: use a map of features to avoid new creation at each call
            if (feature != null)
            {
                feature.updateData(results);
            }
        }
    }
    
    /**
     * Computes the feature for each of the regions specified by labels in
     * <code>labels</code>, based on the data stored in <code>results</code>.
     * All required features must have been computed.
     * 
     * @see RegionAnalysisData#updateWith(Feature)
     * 
     * @param results
     *            a data structure containing data for computing this feature
     * @return an array the same size as labels containing result of analysis
     *         for each region
     */
    public abstract Object[] compute(RegionAnalyisData results);
    
    public abstract void populateTable(ResultsTable table, int row, Object value);
    
}
