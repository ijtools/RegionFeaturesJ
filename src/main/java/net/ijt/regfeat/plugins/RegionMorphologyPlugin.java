/**
 * 
 */
package net.ijt.regfeat.plugins;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.measure.ResultsTable;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import inra.ijpb.algo.DefaultAlgoListener;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.morpho2d.Area;
import net.ijt.regfeat.morpho2d.AverageThickness;
import net.ijt.regfeat.morpho2d.Bounds;
import net.ijt.regfeat.morpho2d.Centroid;
import net.ijt.regfeat.morpho2d.Circularity;
import net.ijt.regfeat.morpho2d.Convexity;
import net.ijt.regfeat.morpho2d.EllipseElongation;
import net.ijt.regfeat.morpho2d.EquivalentEllipse;
import net.ijt.regfeat.morpho2d.EulerNumber;
import net.ijt.regfeat.morpho2d.GeodesicDiameter;
import net.ijt.regfeat.morpho2d.GeodesicElongation;
import net.ijt.regfeat.morpho2d.LargestInscribedDisk;
import net.ijt.regfeat.morpho2d.MaxFeretDiameter;
import net.ijt.regfeat.morpho2d.OrientedBoundingBox;
import net.ijt.regfeat.morpho2d.OrientedBoxElongation;
import net.ijt.regfeat.morpho2d.Perimeter;
import net.ijt.regfeat.morpho2d.Tortuosity;

/**
 * The interactive plugin for computing morphological features from 2D regions
 * represented as label maps.
 * 
 * @see RegionMorphology3DPlugin
 * @see RegionIntensitiesPlugin
 */
public class RegionMorphologyPlugin implements PlugInFilter
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
            initialOptions.features.add(Area.class);
            initialOptions.features.add(Perimeter.class);
            initialOptions.features.add(EulerNumber.class);
            initialOptions.features.add(Centroid.class);
            initialOptions.features.add(EllipseElongation.class);
            initialOptions.features.add(GeodesicDiameter.class);
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
        // Check if image may be a label image
        if (!LabelImages.isLabelImageType(imagePlus))
        {
            IJ.showMessage("Input image should be a label image");
            return;
        }

        // Choose analysis options from interactive dialog
        Options options = chooseOptions(imagePlus, initialOptions);
        // If cancel was clicked, features is null
        if (options == null) return;
        // keep choices for next plugin call
        initialOptions = options;
        
        ResultsTable table = analyze(imagePlus, options);
        if (options.includeImageName)
        {
            table = insertImageNameColumn(table, imagePlus.getShortTitle());
        }
        
        // show result
        String tableName = imagePlus.getShortTitle() + "-Morphometry";
        table.show(tableName);
    }
    
    private static final Options chooseOptions(ImagePlus labelMap, Options initialChoice)
        {
            GenericDialog gd = new GenericDialog("Region Features");
            
            // a collection of check boxes to choose features
            Collection<Class<? extends Feature>> features = initialChoice.features;
            String[] featureNames = new String[] {
                    "Area", "Perimeter", 
                    "Circularity", "Euler_Number",
                    "Bounding_Box", "Centroid",
                    "Equivalent_Ellipse", "Ellipse_Elongation",
                    "Convexity", "Max_Feret_Diameter",
                    "Oriented_Box", "Oriented_Box_Elongation",
                    "Geodesic_Diameter", "Tortuosity",
                    "Max_Inscribed_Disk", "Average_Thickness",
                    "Geodesic_Elongation",
            };
            boolean[] states = new boolean[] {
                    features.contains(Area.class), features.contains(Perimeter.class),
                    features.contains(Circularity.class), features.contains(EulerNumber.class),
                    features.contains(Bounds.class), features.contains(Centroid.class),
                    features.contains(EquivalentEllipse.class), features.contains(EllipseElongation.class),
                    features.contains(Convexity.class), features.contains(MaxFeretDiameter.class),
                    features.contains(OrientedBoundingBox.class), features.contains(OrientedBoxElongation.class),
                    features.contains(GeodesicDiameter.class), features.contains(Tortuosity.class),
                    features.contains(LargestInscribedDisk.class), features.contains(AverageThickness.class),
                    features.contains(GeodesicElongation.class),
            };
            gd.addCheckboxGroup(featureNames.length / 2 + 1, 2, featureNames, states, new String[] {"Features:", ""});
            gd.addMessage("");
            gd.addCheckbox("Include_Image_Name", initialChoice.includeImageName);
            gd.showDialog();
    
            // If cancel was clicked, do nothing
            if (gd.wasCanceled())
            { return null; }
    
            // Extract features to quantify from image
            Options options = new Options();
            features = options.features;
            // if (gd.getNextBoolean()) features.add(Feature.PIXEL_COUNT);
            if (gd.getNextBoolean()) features.add(Area.class);
            if (gd.getNextBoolean()) features.add(Perimeter.class);
            if (gd.getNextBoolean()) features.add(Circularity.class);
            if (gd.getNextBoolean()) features.add(EulerNumber.class);
            if (gd.getNextBoolean()) features.add(Bounds.class);
            if (gd.getNextBoolean()) features.add(Centroid.class);
            if (gd.getNextBoolean()) features.add(EquivalentEllipse.class);
            if (gd.getNextBoolean()) features.add(EllipseElongation.class);
            if (gd.getNextBoolean()) features.add(Convexity.class);
            if (gd.getNextBoolean()) features.add(MaxFeretDiameter.class);
            if (gd.getNextBoolean()) features.add(OrientedBoundingBox.class);
            if (gd.getNextBoolean()) features.add(OrientedBoxElongation.class);
            if (gd.getNextBoolean()) features.add(GeodesicDiameter.class);
            if (gd.getNextBoolean()) features.add(Tortuosity.class);
            if (gd.getNextBoolean()) features.add(LargestInscribedDisk.class);
            if (gd.getNextBoolean()) features.add(AverageThickness.class);
            if (gd.getNextBoolean()) features.add(GeodesicElongation.class);
            
            options.includeImageName = gd.getNextBoolean();
    
            return options;
        }

    private static final ResultsTable analyze(ImagePlus imagePlus, Options options)
    {
        // retrieve dimensions
        int nChannels = imagePlus.getNChannels();
        int nFrames = imagePlus.getNFrames();
        
        // process simple case
        if (nChannels * nFrames == 1)
        {
            return analyzeSingleSlice(imagePlus, options);
        }
        
        ImageStack stack = imagePlus.getStack();
        ArrayList<ResultsTable> allTables = new ArrayList<ResultsTable>(nChannels * nFrames);

        // iterate over slices 
        for (int iFrame = 0; iFrame < nFrames; iFrame++)
        {
            for (int iChannel = 0; iChannel < nChannels; iChannel++)
            {
                int index = imagePlus.getStackIndex(iChannel, 0, iFrame);
                ImageProcessor array = stack.getProcessor(index);
                ImagePlus sliceImage = new ImagePlus(imagePlus.getTitle(), array);
                sliceImage.copyScale(imagePlus);

                allTables.add(analyzeSingleSlice(sliceImage, options));
            }
        }

        ResultsTable res = new ResultsTable();
        Iterator<ResultsTable> iter = allTables.iterator();
        
        // create string patterns
        String pattC = "_c%0" + Math.max((int) Math.ceil(Math.log10(nChannels-1)), 1) + "d";
        String pattT = "_t%0" + Math.max((int) Math.ceil(Math.log10(nFrames-1)), 1) + "d";
        StringBuilder sb = new StringBuilder();
        
        // iterate over individual tables
        for (int iFrame = 0; iFrame < nFrames; iFrame++)
        {
            String tStr = String.format(pattT, iFrame);
            
            for (int iChannel = 0; iChannel < nChannels; iChannel++)
            {
                String cStr = String.format(pattC, iChannel);
                
                ResultsTable tbl = iter.next();
                for (int iRow = 0; iRow < tbl.getCounter(); iRow++)
                {
                    // start new row
                    res.incrementCounter();
                    
                    String labelString = tbl.getLabel(iRow);
                    
                    // create label for the new row
                    sb.setLength(0);
                    sb.append("L" + labelString);
                    if (nFrames > 1) sb.append(tStr);
                    if (nChannels > 1) sb.append(cStr);
                    res.addLabel(sb.toString());
                    
                    // add columns for meta-data
                    res.addValue("Region", Integer.parseInt(labelString));
                    if (nChannels > 1) res.addValue("Channel", iChannel);
                    if (nFrames > 1) res.addValue("Frame", iFrame);
                    
                    // copy all column values
                    for (String colName : tbl.getHeadings())
                    {
                        if ("Label".equalsIgnoreCase(colName)) continue;
                        res.addValue(colName, tbl.getValue(colName, iRow));
                    }
                }
            }
        }
        
        return res;
    }
    
    private static final ResultsTable analyzeSingleSlice(ImagePlus imagePlus, Options options)
    {
        // create a Region feature analyzer from options
        RegionFeatures analyzer = options.createAnalyzer(imagePlus);
        
        // Call the main processing method
        DefaultAlgoListener.monitor(analyzer);
        return analyzer.createTable();
    }

    private static final ResultsTable insertImageNameColumn(ResultsTable table, String imageName)
    {
        ResultsTable res = new ResultsTable();
        
        for (int iRow = 0; iRow < table.getCounter(); iRow++)
        {
            // start new row
            res.incrementCounter();
            
            // add row meta-data
            res.addLabel(table.getLabel(iRow));
            res.setValue("Image", iRow, imageName);
            
            // copy all column values
            for (String colName : table.getHeadings())
            {
                if ("Label".equalsIgnoreCase(colName)) continue;
                res.setValue(colName, iRow, table.getValue(colName, iRow));
            }
        }
        
        return res;
    }
    
    static class Options
    {
        /**
         * The list of features to compute.
         */
        ArrayList<Class<? extends Feature>> features = new ArrayList<>();
        
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
        public RegionFeatures createAnalyzer(ImagePlus imagePlus)
        {
            RegionFeatures analyzer = RegionFeatures.initialize(imagePlus);
            features.stream().forEachOrdered(feature -> analyzer.add(feature));
            return analyzer;
        }
    }
}
