/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collection;

import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import inra.ijpb.geometry.OrientedBox2D;
import inra.ijpb.geometry.Polygon2D;
import inra.ijpb.measure.region2d.OrientedBoundingBox2D;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.morpho2d.core.ConvexHull;

/**
 * 
 */
public class OrientedBoundingBox implements RegionFeature
{
    @Override
    public OrientedBox2D[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        Polygon2D[] hulls = (Polygon2D[]) data.results.get(ConvexHull.class);
        
        // retrieve spatial calibration of image
        Calibration calib = data.labelMap.getCalibration();

        // Compute the oriented box of each set of corner points
        return Arrays.stream(hulls)
                .map(hull -> OrientedBoundingBox2D.orientedBoundingBox(hull.vertices(), calib))
                .toArray(OrientedBox2D[]::new);
    }

    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof OrientedBox2D[])
        {
            OrientedBox2D[] array = (OrientedBox2D[]) obj;
            for (int r = 0; r < array.length; r++)
            {
                OrientedBox2D obox = array[r];
                Point2D center = obox.center();
                table.setValue("Oriented_Box_Center_X",  r, center.getX());
                table.setValue("Oriented_Box_Center_Y",  r, center.getY());
                table.setValue("Oriented_Box_Length",    r, obox.length());
                table.setValue("Oriented_Box_Width",     r, obox.width());
                table.setValue("Oriented_Box_Orientation", r, obox.orientation());
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of double");
        }
    }

    public void overlayResult(RegionFeatures data, ImagePlus target)
    {
        // retrieve array of ellipses
        Object obj = data.results.get(this.getClass());
        if (!(obj instanceof OrientedBox2D[]))
        {
            throw new RuntimeException("Requires object argument to be an array of Ellipse");
        }
        OrientedBox2D[] ellipses = (OrientedBox2D[]) obj;
        
        // get spatial calibration of target image
        Calibration calib = target.getCalibration();
        
        // create overlay
        Overlay overlay = new Overlay();
        
        // add each ellipse to the overlay
        for (int i = 0; i < ellipses.length; i++) 
        {
            // Create ROI of oriented box, in pixel coordinates
            OrientedBox2D box = ellipses[i];
            Roi roi = createUncalibratedRoi(box, calib);

            // add ROI to overlay
            Color color = data.labelColors[i];
            Feature.addRoiToOverlay(overlay, roi, color, 1.5);
        }
        
        target.setOverlay(overlay);
    }
    
    /**
     * Determines the ROI corresponding to the uncalibrated version of this
     * box, assuming it was defined in calibrated coordinates.
     * 
     * @param box
     *            the oriented box in calibrated coordinates
     * @param calib
     *            the spatial calibration to consider
     * @return the ROI corresponding to the box
     */
    private final static Roi createUncalibratedRoi(OrientedBox2D box, Calibration calib)
    {
        Point2D center = box.center();
        double xc = center.getX();
        double yc = center.getY();
        double dx = box.length() / 2;
        double dy = box.width() / 2;
        double theta = Math.toRadians(box.orientation());
        double cot = Math.cos(theta);
        double sit = Math.sin(theta);
        
        // coordinates of polygon ROI
        float[] xp = new float[4];
        float[] yp = new float[4];
        
        // iterate over vertices
        double x, y;
        x = xc + dx * cot - dy * sit;
        y = yc + dx * sit + dy * cot;
        xp[0] = (float) ((x - calib.xOrigin) / calib.pixelWidth);
        yp[0] = (float) ((y - calib.yOrigin) / calib.pixelHeight);
        x = xc - dx * cot - dy * sit;
        y = yc - dx * sit + dy * cot;
        xp[1] = (float) ((x - calib.xOrigin) / calib.pixelWidth);
        yp[1] = (float) ((y - calib.yOrigin) / calib.pixelHeight);
        x = xc - dx * cot + dy * sit;
        y = yc - dx * sit - dy * cot;
        xp[2] = (float) ((x - calib.xOrigin) / calib.pixelWidth);
        yp[2] = (float) ((y - calib.yOrigin) / calib.pixelHeight);
        x = xc + dx * cot + dy * sit;
        y = yc + dx * sit - dy * cot;
        xp[3] = (float) ((x - calib.xOrigin) / calib.pixelWidth);
        yp[3] = (float) ((y - calib.yOrigin) / calib.pixelHeight);
        return new PolygonRoi(xp, yp, 4, Roi.POLYGON);
    }
    
    @Override
    public Collection<Class<? extends Feature>>requiredFeatures()
    {
        return Arrays.asList(ConvexHull.class);
    }
}
