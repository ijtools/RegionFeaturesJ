/**
 * 
 */
package net.ijt.regfeat;

import java.awt.Color;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;

import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.Roi;

/**
 * Abstract class for a feature that can compute something on a label map.
 * 
 * The result of the computation is provided by the <code>compute</code> method. 
 * 
 * Results obtained by several features can be aggregated via the <code>overlayResults</code> method.
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
     * <code>labels</code>, based on the data stored in <code>results</code>.
     * All required features must have been computed.
     * 
     * The type of the result varies depending on the feature. In the case of a
     * {@code RegionFeature}, the result is an array with as many elements as
     * the number of regions. In the case of a {@code SingleValueFeature}, the
     * result is an array of double values.
     * 
     * @see RegionFeatures#process(Feature)
     * 
     * @param data
     *            a data structure containing data for computing this feature
     * @return the result of the computation
     */
    public abstract Object compute(RegionFeatures data);
    
    /**
     * Overlay the result of feature computation on the specified image
     * (optional operation). This method must be called after the features have
     * been computed.
     * 
     * By default, this method does nothing. It is expected that mostly features
     * computed on 2D regions can be displayed.
     * 
     * @param image
     *            the instance of ImagePlus to display to result on.
     * @param data
     *            the data structure containing results of features computed on
     *            regions
     */
    public default void overlayResult(ImagePlus image, RegionFeatures data)
    {
    }
    
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
    
    public static void addRoiToOverlay(Overlay overlay, Roi roi, Color color, double strokeWidth)
    {
        roi.setStrokeColor(color);
        roi.setStrokeWidth(strokeWidth);
        overlay.add(roi);
    }
}
