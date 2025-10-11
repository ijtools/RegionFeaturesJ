/**
 * 
 */
package net.ijt.regfeat.plugins;

import java.util.stream.Stream;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;
import inra.ijpb.algo.DefaultAlgoListener;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.OverlayFeature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.RoiFeature;
import net.ijt.regfeat.morpho2d.Bounds;
import net.ijt.regfeat.morpho2d.EquivalentEllipse;
import net.ijt.regfeat.morpho2d.LargestInscribedDisk;
import net.ijt.regfeat.morpho2d.OrientedBoundingBox;
import net.ijt.regfeat.morpho2d.core.ConvexHull;
import net.ijt.regfeat.morpho2d.core.FurthestPointPair;
import net.ijt.regfeat.spatial.RegionAdjacencyGraph;

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
        ELLIPSE("Equivalent Ellipse", EquivalentEllipse.class), 
        ORIENTED_BOX("Oriented Bounding Box", OrientedBoundingBox.class),
        CONVEX_HULL("Convex Hull", ConvexHull.class), 
        FERET_DIAMETER("Feret Diameter", FurthestPointPair.class), 
        INSCRIBED_DISK("Inscribed Disk", LargestInscribedDisk.class),
        REGION_ADJACENCY_GRAPH("Region Adjacency Graph", RegionAdjacencyGraph.class);
        

        /** The name of the feature, for GUI display */
        private final String label;

        /** the class of the feature to compute */
        private final Class<? extends OverlayFeature> featureClass;

        private FeatureOption(String label, Class<? extends OverlayFeature> featureClass)
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
        public Class<? extends OverlayFeature> getFeatureClass() 
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
        gd.addChoice("Image To Overlay:", imageNames, imageNames[0]);
        gd.addNumericField("Roi Width:", 1.5, 1);
        gd.addCheckbox("Export to RoiManager", false);
        
        // Display dialog and wait for user validation
        gd.showDialog();
        if (gd.wasCanceled()) return;

        // retrieve user choices
        String featureName = gd.getNextChoice();
        int overlayImageIndex = gd.getNextChoiceIndex();
        double width = gd.getNextNumber();
        boolean roiManager = gd.getNextBoolean();
        
        // retrieve class of feature
        Class<? extends OverlayFeature> featureClass = FeatureOption.fromLabel(featureName).getFeatureClass();
        
        // Extract mask image
        ImagePlus imageToOverlay = WindowManager.getImage(overlayImageIndex+1);
        
        // create a Region feature analyzer from options
        RegionFeatures analyzer = RegionFeatures.initialize(imagePlus);
        DefaultAlgoListener.monitor(analyzer);
        
        // Call the main processing method
        analyzer.add(featureClass);
        analyzer.computeAll();
        
        OverlayFeature feature = (OverlayFeature) analyzer.getFeature(featureClass);
        feature.overlayResult(imageToOverlay, analyzer, width);
        
        if (roiManager)
        {
            if (!(feature instanceof RoiFeature)) return;
            
            Roi[] rois = ((RoiFeature) feature).computeRois(analyzer);
                    
            // retrieve RoiManager
            RoiManager rm = RoiManager.getInstance();
            if (rm == null)
            {
                rm = new RoiManager();
            }
            String pattern = "r%03d";
            
            // populate RoiManager with PolygonRoi
            for (int i = 0; i < analyzer.labels.length; i++)
            {
                int label = analyzer.labels[i];
                String name = String.format(pattern, label);

                Roi roi = rois[i];
                roi.setName(name);
                rm.addRoi(roi);
            }
        }
    }
}
