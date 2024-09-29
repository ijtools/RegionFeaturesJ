/**
 * 
 */
package net.ijt.regfeat;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

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
    
    
    /**
     * Computes the feature for each of the regions specified by labels in
     * <code>labels</code>, based on the data stored in <code>results</code>.
     * All required features must have been computed.
     * 
     * @see RegionAnalysisData#process(Feature)
     * 
     * @param results
     *            a data structure containing data for computing this feature
     * @return an array the same size as labels containing result of analysis
     *         for each region
     */
    public abstract Object compute(RegionFeatures data);
    
    public abstract void populateTable(ResultsTable table, Object obj);
    
    public Collection<Class<? extends Feature>>requiredFeatures()
    {
        return Collections.unmodifiableCollection(this.requiredFeatures);
    }
}
