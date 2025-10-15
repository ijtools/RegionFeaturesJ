/**
 * 
 */
package net.ijt.regfeat.plugins;

import java.util.ArrayList;
import java.util.Collection;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.measure.ResultsTable;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.RegionFeatures.UnitDisplay;
import net.ijt.regfeat.intensity.CenterOfMass;
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
 * @see RegionMorphologyPlugin
 * @see RegionMorphology3DPlugin
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
     * The options to create the region feature analyzer
     */
    static Options initialOptions = null;

    
    // ====================================================
    // Constructor

    /**
     * Default empty constructor.
     */
    public RegionIntensitiesPlugin()
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

        // initialize options if necessary
        if (initialOptions == null)
        {
            initialOptions = new Options();
            // populate options with a selection of intensity features
            initialOptions.features.add(MeanIntensity.class);
            initialOptions.features.add(MedianIntensity.class);
            initialOptions.features.add(MinIntensity.class);
            initialOptions.features.add(MaxIntensity.class);
            initialOptions.features.add(IntensityStandardDeviation.class);
        }
        
        return DOES_ALL | NO_CHANGES;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ij.plugin.PlugIn#run(java.lang.String)
     */
    public void run(ImageProcessor ip)
    {
        Options options = chooseOptions(initialOptions);
        
        // If cancel was clicked, features is null
        if (options == null) return;
        
        // keep choices for next plugin call
        initialOptions = options;
        
        // retrieve images
        ImagePlus inputImage = options.intensityImage;
        ImagePlus labelMap = options.labelMapImage;
        
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


        // create analyzer based on options list
        RegionFeatures analyzer = options.createAnalyzer();
        
        // compute features and populate results table
        ResultsTable[] tables = analyzer.createTables();
        ResultsTable featuresTable = tables[0];
        if (options.includeImageName)
        {
            featuresTable = RegionFeatures.insertImageNameColumn(featuresTable, imagePlus.getShortTitle());
        }

        // show result
        String tableName = imagePlus.getShortTitle() + "-Intensity";
        featuresTable.show(tableName);
    }
    
    private Options chooseOptions(Options initialChoice)
    {
        int nImages = WindowManager.getImageCount();

        if (nImages < 2)
        {
            IJ.error("Intensity Measures 2D/3D input error", "ERROR: At least two images need to be open to run " + "Intensity Measures 2D/3D");
            return null;
        }
        
        // build array of image names
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
        Collection<Class<? extends Feature>> features = initialChoice.features;
        String[] labels = new String[] { 
                "Mean", "Median", 
                "Minimum", "Maximum", 
                "Variance", "Standard_Deviation", 
                "Skewness", "Kurtosis", 
                "Center_Of_Mass"};
        
        boolean[] states = new boolean[] { 
                features.contains(MeanIntensity.class),
                features.contains(MedianIntensity.class),
                features.contains(MinIntensity.class),
                features.contains(MaxIntensity.class),
                features.contains(IntensityVariance.class),
                features.contains(IntensityStandardDeviation.class),
                features.contains(IntensitySkewness.class),
                features.contains(IntensityKurtosis.class),
                features.contains(CenterOfMass.class),
        };
        gd.addCheckboxGroup(5, 2, labels, states);
        
        // create the dialog, with operator options
        gd.showDialog();

        // If cancel was clicked, do nothing
        if (gd.wasCanceled())
        { return null; }
        
        // retrieve index of intensity image and label image
        inputIndex = gd.getNextChoiceIndex();
        labelsIndex = gd.getNextChoiceIndex();

        // populate new options class
        Options options = new Options();
        options.labelMapImage = WindowManager.getImage(labelsIndex + 1);
        options.intensityImage = WindowManager.getImage(inputIndex + 1);
        features = options.features;
        if (gd.getNextBoolean()) features.add(MeanIntensity.class);
        if (gd.getNextBoolean()) features.add(MedianIntensity.class);
        if (gd.getNextBoolean()) features.add(MinIntensity.class);
        if (gd.getNextBoolean()) features.add(MaxIntensity.class);
        if (gd.getNextBoolean()) features.add(IntensityVariance.class);
        if (gd.getNextBoolean()) features.add(IntensityStandardDeviation.class);
        if (gd.getNextBoolean()) features.add(IntensitySkewness.class);
        if (gd.getNextBoolean()) features.add(IntensityKurtosis.class);
        if (gd.getNextBoolean()) features.add(CenterOfMass.class);
        
//        options.unitDisplay = unitDisplayValues[gd.getNextChoiceIndex()];
//        options.includeImageName = gd.getNextBoolean();

        return options;
    }
    
    /**
     * Inner utility class that contains all the information necessary to
     * perform an analysis: list of features to compute, options for building
     * results table...
     */
    static class Options
    {
        ImagePlus labelMapImage;
        
        ImagePlus intensityImage;
        
        /**
         * The list of features to compute.
         */
        ArrayList<Class<? extends Feature>> features = new ArrayList<>();
        
        /**
         * The strategy for displaying calibration units. Default is to append
         * to column names.
         */
        UnitDisplay unitDisplay = UnitDisplay.COLUMN_NAMES;
        
        /**
         * Can be useful when concatenating results obtained on different images
         * into a single table.
         */
        boolean includeImageName = false;
        
        /**
         * Creates a new Region Feature Analyzer for the specified image.
         * 
         * @param imagePlus
         *            the image containing the label map.
         * @return a new RegionFeatures instance.
         */
        public RegionFeatures createAnalyzer()
        {
            RegionFeatures analyzer = RegionFeatures.initialize(labelMapImage);
            analyzer.imageData.put("intensity", intensityImage);
            features.stream().forEachOrdered(feature -> analyzer.add(feature));
            analyzer.unitDisplay = this.unitDisplay;
            return analyzer;
        }
    }
}
