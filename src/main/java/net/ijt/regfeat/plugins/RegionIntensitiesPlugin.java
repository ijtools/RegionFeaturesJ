/**
 * 
 */
package net.ijt.regfeat.plugins;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.measure.ResultsTable;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.intensity.IntensityKurtosis;
import net.ijt.regfeat.intensity.IntensitySkewness;
import net.ijt.regfeat.intensity.IntensityStandardDeviation;
import net.ijt.regfeat.intensity.IntensityVariance;
import net.ijt.regfeat.intensity.MaxIntensity;
import net.ijt.regfeat.intensity.MeanIntensity;
import net.ijt.regfeat.intensity.MedianIntensity;
import net.ijt.regfeat.intensity.MinIntensity;

/**
 * The interactive plugin for computing features based on intensities from 2D or
 * 3D regions represented as label maps.
 * 
 * The plugin requires to choose two images:
 * <ul>
 * <li>the image containing the label map,</li>
 * <li>the image containing the intensities.</li>
 * </ul>
 * 
 * @see RegionFeaturesPlugin
 * @see RegionFeatures3DPlugin
 */
public class RegionIntensitiesPlugin implements PlugInFilter
{
    // ====================================================
    // Class variables

    /**
     * The image to work on.
     */
    ImagePlus imagePlus;

    /**
     * The class containing both the features to compute and the results of
     * computations.
     */
    RegionFeatures features;

    
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

        // initialize RegionFeatures instance if necessary, populated with some
        // default intensity features
        this.features = RegionFeatures.initialize(imp)
                .add(MeanIntensity.class)
                .add(MedianIntensity.class)
                .add(MinIntensity.class)
                .add(MaxIntensity.class)
                .add(IntensityStandardDeviation.class)
                ;
        return DOES_ALL | NO_CHANGES;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ij.plugin.PlugIn#run(java.lang.String)
     */
    public void run(ImageProcessor ip)
    {
        int nImages = WindowManager.getImageCount();

        if (nImages < 2)
        {
            IJ.error("Intensity Measures 2D/3D input error", "ERROR: At least two images need to be open to run " + "Intensity Measures 2D/3D");
            return;
        }

        String[] names = new String[nImages];

        for (int i = 0; i < nImages; i++)
            names[i] = WindowManager.getImage(i + 1).getTitle();

        int inputIndex = 0;
        int labelsIndex = 1;

        // open a dialog to choose user options
        GenericDialog gd = new GenericDialog("Intensity Measurements");
        gd.addChoice("Intensity Image", names, names[inputIndex]);
        gd.addChoice("Label Map", names, names[labelsIndex]);
        gd.addMessage("Measurements:");
        String[] labels = new String[] { "Mean", "Median", "Minimum", "Maximum", "Variance", "Standard Deviation", "Skewness", "Kurtosis" };
        boolean[] states = new boolean[] { 
                features.contains(MeanIntensity.class),
                features.contains(MedianIntensity.class),
                features.contains(MinIntensity.class),
                features.contains(MaxIntensity.class),
                features.contains(IntensityVariance.class),
                features.contains(IntensityStandardDeviation.class),
                features.contains(IntensitySkewness.class),
                features.contains(IntensityKurtosis.class),
        };
        gd.addCheckboxGroup(4, 2, labels, states);
        
        // create the dialog, with operator options
        gd.showDialog();

        // If cancel was clicked, do nothing
        if (gd.wasCanceled())
        { return; }
        
        // retrieve index of intensity image and label image
        inputIndex = gd.getNextChoiceIndex();
        labelsIndex = gd.getNextChoiceIndex();

        // retrieve images
        ImagePlus inputImage = WindowManager.getImage(inputIndex + 1);
        ImagePlus labelMap = WindowManager.getImage(labelsIndex + 1);

        // check if image is a label image
        // Check if image may be a label image
        if (!LabelImages.isLabelImageType(imagePlus))
        {
            IJ.showMessage("Input image should be a label image");
            return;
        }

        // check dimensionality and size
        if (inputImage.getNDimensions() != labelMap.getNDimensions())
        {
            IJ.error("Intensity Measures 2D/3D input error", "Error: input and label images must have the same dimension");
            return;
        }
        if (inputImage.getWidth() != labelMap.getWidth() || inputImage.getHeight() != labelMap.getHeight())
        {
            IJ.error("Intensity Measures 2D/3D input error", "Error: input and label images must have the same size");
            return;
        }

        // retrieve measure options, and keep them for later use
        RegionFeatures features = RegionFeatures.initialize(labelMap);
        features.addImageData("intensity", inputImage);
        if (gd.getNextBoolean()) features.add(MeanIntensity.class);
        if (gd.getNextBoolean()) features.add(MedianIntensity.class);
        if (gd.getNextBoolean()) features.add(MinIntensity.class);
        if (gd.getNextBoolean()) features.add(MaxIntensity.class);
        if (gd.getNextBoolean()) features.add(IntensityVariance.class);
        if (gd.getNextBoolean()) features.add(IntensityStandardDeviation.class);
        if (gd.getNextBoolean()) features.add(IntensitySkewness.class);
        if (gd.getNextBoolean()) features.add(IntensityKurtosis.class);
        
        
        // Call the main processing method
        // DefaultAlgoListener.monitor(morphoFeatures);
        features.computeAll();
        ResultsTable table = features.createTable();

        // show result
        String tableName = imagePlus.getShortTitle() + "-Intensity";
        table.show(tableName);

        // keep choices for next plugin call
        this.features = features;
    }
}
