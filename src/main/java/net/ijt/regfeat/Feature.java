/**
 * 
 */
package net.ijt.regfeat;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;

/**
 * Abstract class for a feature that can compute "something" from a label map.
 * 
 * The result of the computation is provided by the {@code compute} method.
 * 
 * Several specialization interfaces allow to further describe the expected
 * behavior of the feature:
 * <ul>
 * <li>the {@code RegionTabularFeature} expects that each region
 * may be described by a series of numerical values, that can be used to
 * populate a data table</li>
 * <li>the {@code OverlayFeature} can be used to display result of computation
 * onto another image (typically the grayscale or color image used to generate a
 * label map)</li>
 * </ul>
 * 
 * @see RegionTabularFeature
 * @see SingleValueFeature
 * @see OverlayFeature
 */
public interface Feature
{
    /**
     * Creates a new instance of the feature determined by its class. The
     * Feature must provide an empty constructor. Returns <code>null</code> if
     * the creation fails.
     * 
     * @param featureClass
     *            the class of the feature to create.
     * @return an instance of feature with the specified class
     */
    public static Feature create(Class<? extends Feature> featureClass)
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
     * Computes the feature for each of the regions specified by labels in
     * <code>labels</code>, based on the data stored in the specified instance
     * of {@code RegionFeatures}. All required features must have been computed.
     * 
     * The type of the result varies depending on the feature. In the case of a
     * {@code RegionFeature}, the result is an array with as many elements as
     * the number of regions. In the case of a {@code SingleValueFeature}, the
     * result is an array of double values.
     * 
     * @see net.ijt.regfeat.RegionFeatures#process(Class)
     * @see #requiredFeatures()
     * 
     * @param data
     *            a data structure containing data for computing this feature
     * @return the result of the computation
     */
    public abstract Object compute(RegionFeatures data);
    
    /**
     * Returns the list of features this feature depends on. The result is given
     * as a list of classes. Default behavior is to return an empty list (no
     * dependency).
     * 
     * @return the list of features this feature depends on
     */
    public default Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Collections.emptyList();
    }
}
