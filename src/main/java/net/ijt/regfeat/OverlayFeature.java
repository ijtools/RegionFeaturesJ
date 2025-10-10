/**
 * 
 */
package net.ijt.regfeat;

import java.awt.Color;

import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.Roi;

/**
 * A specialization of the (Region) {@code Feature} interface that can be
 * overlaid over another image.
 * 
 * <pre>
 * {@code
    // create a Region feature analyzer
    RegionFeatures analyzer = RegionFeatures.initialize(imagePlus);
    Class<? extends OverlayFeature> featureClass = EquivalentEllipse.class;
    analyzer.add(featureClass);
    
    // Call the main processing method
    analyzer.computeAll();
    
    // compute overlay of the specified feature
    OverlayFeature feature = (OverlayFeature) analyzer.getFeature(featureClass);
    feature.overlayResult(imageToOverlay, analyzer);
} </pre>
 */
public interface OverlayFeature extends Feature
{
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
    public void overlayResult(ImagePlus image, RegionFeatures data, double strokeWidth);
    
    /**
     * Utility method that updates the overlay by adding the specified ROI,
     * using the specified display options.
     * 
     * @param overlay
     *            the overlay to update.
     * @param roi
     *            the instance of ImageJ {@code Roi} to add to the overlay
     * @param color
     *            the color of overlay to add
     * @param strokeWidth
     *            the line width of the overlay to add
     */
    public static void addRoiToOverlay(Overlay overlay, Roi roi, Color color, double strokeWidth)
    {
        roi.setStrokeColor(color);
        roi.setStrokeWidth(strokeWidth);
        overlay.add(roi);
    }
}
