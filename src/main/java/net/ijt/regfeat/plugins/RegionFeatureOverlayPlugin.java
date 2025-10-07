/**
 * 
 */
package net.ijt.regfeat.plugins;

import java.util.stream.Stream;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import inra.ijpb.algo.DefaultAlgoListener;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.morpho2d.Bounds;
import net.ijt.regfeat.morpho2d.EquivalentEllipse;
import net.ijt.regfeat.morpho2d.OrientedBoundingBox;

/**
 * Compute a geometric feature from a label map image, and updates the overlay
 * of the specified image.
 * 
 * @see RegionMorphologyPlugin
 * @see RegionMorphology3DPlugin
 * @see RegionIntensitiesPlugin
 */
public class RegionFeatureOverlayPlugin implements PlugInFilter
{
    enum FeatureOption
    {
        BOUNDING_BOX("Bounding Box", Bounds.class), 
        ELLIPSE("Ellipse", EquivalentEllipse.class), 
        ORIENTED_BOX("Oriented Bounding Box", OrientedBoundingBox.class);

        /** The name of the feature, for GUI display */
        private final String label;

        /** the class of the feature to compute */
        private final Class<? extends Feature> featureClass;

        private FeatureOption(String label, Class<? extends Feature> featureClass)
        {
            this.label = label;
            this.featureClass = featureClass;
        }
        
        /**
         * @return the label associated to this enumeration item
         */
        public String getLabel() 
        {
            return label;
        }

        /**
         * @return the featureClass corresponding to this enumeration item
         */
        public Class<? extends Feature> getFeatureClass() 
        {
            return featureClass;
        }
        
        /**
         * @return a string representation of this enumeration item
         */
        public String toString() 
        {
            return label;
        }
        
        /**
         * @return the array of labels for the colors within this enumeration
         */
        public static String[] getAllLabels()
        {
            return Stream.of(FeatureOption.values())
                    .map(item -> item.label)
                    .toArray(String[]::new);
        }
        
        /**
         * Determines the FeatureOption from its label.
         * 
         * @param label
         *            the name of the FeatureOption
         * @return the FeatureOption enumeration corresponding to the name
         * @throws IllegalArgumentException
         *             if FeatureOption name is not recognized.
         */
        public static FeatureOption fromLabel(String label) 
        {
            if (label != null)
                label = label.toLowerCase();
            for (FeatureOption value : FeatureOption.values()) 
            {
                String cmp = value.label.toLowerCase();
                if (cmp.equals(label))
                    return value;
            }
            throw new IllegalArgumentException("Unable to parse FeatureOption with label: " + label);
        }

    }
    
    // ====================================================
    // Class variables

    /**
     * The image to work on.
     */
    ImagePlus imagePlus;

    /**
     * Default empty constructor.
     */
    public RegionFeatureOverlayPlugin()
    {
    }
    
    
    // ====================================================
    // Implementation of Plugin and PluginFilter interface

    /*
     * (non-Javadoc)
     * 
     * @see ij.plugin.filter.PlugInFilter#setup(java.lang.String, ij.ImagePlus)
     */
    @Override
    public int setup(String arg, ImagePlus imp)
    {
        if (imp == null)
        {
            IJ.noImage();
            return DONE;
        }
        this.imagePlus = imp;

        return DOES_ALL | NO_CHANGES;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ij.plugin.PlugIn#run(java.lang.String)
     */
    public void run(ImageProcessor ip)
    {
        int[] indices = WindowManager.getIDList();
        if (indices == null) {
            IJ.error("No image", "Need at least one image to work");
            return;
        }
    
        // create the list of image names
        String[] imageNames = WindowManager.getImageTitles();
        
        // Check if image may be a label image
        if (!LabelImages.isLabelImageType(imagePlus))
        {
            IJ.showMessage("Input image should be a label image");
            return;
        }

        GenericDialog gd = new GenericDialog("Region Feature Overlay");
        
        String[] featureNames = FeatureOption.getAllLabels();
        gd.addChoice("Feature:", featureNames, featureNames[0]);
        
        gd.addMessage("");
        gd.addChoice("Image To Overlay:", imageNames, imageNames[0]);
        
        // Display dialog and wait for user validation
        gd.showDialog();
        if (gd.wasCanceled()) return;

        // retrieve class of feature
        String featureName = gd.getNextChoice();
        Class<? extends Feature> featureClass = FeatureOption.fromLabel(featureName).getFeatureClass();
        
        // Extract mask image
        int overlayImageIndex = gd.getNextChoiceIndex();
        ImagePlus imageToOverlay = WindowManager.getImage(overlayImageIndex+1);
        
        // create a Region feature analyzer from options
        RegionFeatures analyzer = RegionFeatures.initialize(imagePlus);
        analyzer.add(featureClass);
        
        // Call the main processing method
        DefaultAlgoListener.monitor(analyzer);
        
        analyzer.computeAll();
        
        analyzer.getFeature(featureClass).overlayResult(imageToOverlay, analyzer);
    }
    
 }
