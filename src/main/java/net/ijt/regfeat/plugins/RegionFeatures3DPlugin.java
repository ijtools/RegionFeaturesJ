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
import net.ijt.regfeat.ElementCount;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.morpho3d.Bounds3D;
import net.ijt.regfeat.morpho3d.Centroid3D;
import net.ijt.regfeat.morpho3d.EllipsoidElongations;
import net.ijt.regfeat.morpho3d.EquivalentEllipsoid;
import net.ijt.regfeat.morpho3d.EulerNumber;
import net.ijt.regfeat.morpho3d.MeanBreadth;
import net.ijt.regfeat.morpho3d.Sphericity;
import net.ijt.regfeat.morpho3d.SurfaceArea;
import net.ijt.regfeat.morpho3d.Volume;

/**
 * The interactive plugin for computing morphological features from 3D regions
 * represented as label maps.
 * 
 * @see RegionFeaturesPlugin
 * @see RegionIntensitiesPlugin
 */
public class RegionFeatures3DPlugin implements PlugInFilter
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
            initialOptions.features.add(Volume.class);
            initialOptions.features.add(SurfaceArea.class);
            initialOptions.features.add(Sphericity.class);
            initialOptions.features.add(EquivalentEllipsoid.class);
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

        // show result
        String tableName = imagePlus.getShortTitle() + "-Morphometry";
        table.show(tableName);
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

    private static final Options chooseOptions(ImagePlus labelMap, Options initialChoice)
    {
        GenericDialog gd = new GenericDialog("Region Features 3D");
        
        // a collection of check boxes to choose features
        Collection<Class<? extends Feature>> features = initialChoice.features;
        gd.addCheckbox("Voxel_Count", features.contains(ElementCount.class));
        gd.addCheckbox("Volume", features.contains(Volume.class));
        gd.addCheckbox("Surface Area", features.contains(SurfaceArea.class));
        gd.addCheckbox("Mean_Breadth", features.contains(MeanBreadth.class));
        gd.addCheckbox("Euler_Number", features.contains(EulerNumber.class));
        gd.addCheckbox("Sphericity", features.contains(Sphericity.class));
        gd.addCheckbox("Bounding_Box", features.contains(Bounds3D.class));
        gd.addCheckbox("Centroid", features.contains(Centroid3D.class));
        gd.addCheckbox("Equivalent_Ellipsoid", features.contains(EquivalentEllipsoid.class));
        gd.addCheckbox("Ellipsoid_Elongations", features.contains(EllipsoidElongations.class));
        gd.showDialog();

        // If cancel was clicked, do nothing
        if (gd.wasCanceled())
        { return null; }

        // Extract features to quantify from image
        Options options = new Options();
        features = options.features;
        if (gd.getNextBoolean()) features.add(ElementCount.class);
        if (gd.getNextBoolean()) features.add(Volume.class);
        if (gd.getNextBoolean()) features.add(SurfaceArea.class);
        if (gd.getNextBoolean()) features.add(MeanBreadth.class);
        if (gd.getNextBoolean()) features.add(EulerNumber.class);
        if (gd.getNextBoolean()) features.add(Sphericity.class);
        if (gd.getNextBoolean()) features.add(Bounds3D.class);
        if (gd.getNextBoolean()) features.add(Centroid3D.class);
        if (gd.getNextBoolean()) features.add(EquivalentEllipsoid.class);
        if (gd.getNextBoolean()) features.add(EllipsoidElongations.class);

        return options;
    }
    
    static class Options
    {
        /**
         * The list of features to compute.
         */
        ArrayList<Class<? extends Feature>> features = new ArrayList<>();
        
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
