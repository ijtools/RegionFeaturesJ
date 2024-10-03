/**
 * 
 */
package net.ijt.regfeat.plugins;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.measure.ResultsTable;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import inra.ijpb.label.LabelImages;
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
 * 
 */
public class RegionFeaturesPlugin implements PlugInFilter
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

        // initialize MorphometricFeatures2D instance if necessary
        this.features = RegionFeatures.initialize(imp)
                .add(Area.class)
                .add(Perimeter.class)
                .add(EulerNumber.class)
                .add(Centroid.class)
                .add(EllipseElongation.class)
                .add(GeodesicDiameter.class);
        return DOES_ALL | NO_CHANGES;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ij.plugin.PlugIn#run(java.lang.String)
     */
    public void run(ImageProcessor ip)
    {
        // check if image is a label image
        // Check if image may be a label image
        if (!LabelImages.isLabelImageType(imagePlus))
        {
            IJ.showMessage("Input image should be a label image");
            return;
        }

        // create the dialog, with operator options
        RegionFeatures morphoFeatures = chooseFeatures(imagePlus, features);
        // If cancel was clicked, features is null
        if (morphoFeatures == null)
        { return; }

        // Call the main processing method
        // DefaultAlgoListener.monitor(morphoFeatures);
        morphoFeatures.computeAll();
        ResultsTable table = morphoFeatures.createTable();

        // show result
        String tableName = imagePlus.getShortTitle() + "-Morphometry";
        table.show(tableName);

        // keep choices for next plugin call
        this.features = morphoFeatures;
    }

    private static final RegionFeatures chooseFeatures(ImagePlus labelMap, RegionFeatures initialChoice)
    {
        GenericDialog gd = new GenericDialog("Analyze Regions");
        // gd.addCheckbox("Pixel_Count",
        // initialChoice.contains(Feature.PIXEL_COUNT));
        gd.addCheckbox("Area", initialChoice.contains(Area.class));
        gd.addCheckbox("Perimeter", initialChoice.contains(Perimeter.class));
        gd.addCheckbox("Circularity", initialChoice.contains(Circularity.class));
        gd.addCheckbox("Euler_Number", initialChoice.contains(EulerNumber.class));
        gd.addCheckbox("Bounding_Box", initialChoice.contains(Bounds.class));
        gd.addCheckbox("Centroid", initialChoice.contains(Centroid.class));
        gd.addCheckbox("Equivalent_Ellipse", initialChoice.contains(EquivalentEllipse.class));
        gd.addCheckbox("Ellipse_Elong.", initialChoice.contains(EllipseElongation.class));
        gd.addCheckbox("Convexity", initialChoice.contains(Convexity.class));
        gd.addCheckbox("Max._Feret Diameter", initialChoice.contains(MaxFeretDiameter.class));
        gd.addCheckbox("Oriented_Box", initialChoice.contains(OrientedBoundingBox.class));
        gd.addCheckbox("Oriented_Box_Elong.", initialChoice.contains(OrientedBoxElongation.class));
        gd.addCheckbox("Geodesic Diameter", initialChoice.contains(GeodesicDiameter.class));
        gd.addCheckbox("Tortuosity", initialChoice.contains(Tortuosity.class));
        gd.addCheckbox("Max._Inscribed_Disc", initialChoice.contains(LargestInscribedDisk.class));
        gd.addCheckbox("Average_Thickness", initialChoice.contains(AverageThickness.class));
        gd.addCheckbox("Geodesic_Elong.", initialChoice.contains(GeodesicElongation.class));
        gd.showDialog();

        // If cancel was clicked, do nothing
        if (gd.wasCanceled())
        { return null; }

        // Extract features to quantify from image
        RegionFeatures features = RegionFeatures.initialize(labelMap);
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

        return features;
    }
}
