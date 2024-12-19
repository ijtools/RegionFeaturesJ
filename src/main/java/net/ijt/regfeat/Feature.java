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
import ij.measure.ResultsTable;

/**
 * Abstract class for a feature that can compute something on a label map.
 * 
 * The result of the computation is provided by the <code>compute</code> method. 
 * 
 * Results obtained by several features can be aggregated via the <code>overlayResults</code> method.
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
     * Computes the feature for each of the regions specified by labels in
     * <code>labels</code>, based on the data stored in <code>results</code>.
     * All required features must have been computed.
     * 
     * @see RegionAnalysisData#process(Feature)
     * 
     * @param data
     *            a data structure containing data for computing this feature
     * @return an array the same size as labels containing result of analysis
     *         for each region
     */
    public abstract Object compute(RegionFeatures data);
    
    public abstract void updateTable(ResultsTable table, RegionFeatures data);
    
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
    public void overlayResult(ImagePlus image, RegionFeatures data)
    {
    }
    
    /**
     * Returns the list of features this feature depends on. The result is given
     * as a list of classes. Default behavior is to return an empty list (no
     * dependency).
     * 
     * @return the list of features this feature depends on
     */
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Collections.emptyList();
    }
    
    protected void ensureRequiredFeaturesAreComputed(RegionFeatures data)
    {
        Class<? extends Feature> featureClass = this.getClass();
        Feature feature = data.getFeature(featureClass);
        for (Class<? extends Feature> fClass : feature.requiredFeatures())
        {
            if (!data.isComputed(fClass))
            {
                data.process(fClass);
            }
        }
    }
    
    protected static final void addRoiToOverlay(Overlay overlay, Roi roi, Color color, double strokeWidth)
    {
        roi.setStrokeColor(color);
        roi.setStrokeWidth(strokeWidth);
        overlay.add(roi);
    }
}
