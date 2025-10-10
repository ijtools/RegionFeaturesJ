/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import inra.ijpb.geometry.AngleDiameterPair;
import inra.ijpb.geometry.FeretDiameters;
import inra.ijpb.geometry.OrientedBox2D;
import inra.ijpb.geometry.Polygon2D;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.OverlayFeature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.RegionTabularFeature;
import net.ijt.regfeat.RoiFeature;
import net.ijt.regfeat.morpho2d.core.ConvexHull;

/**
 * The object-oriented bounding box of each region.
 */
public class OrientedBoundingBox implements RegionTabularFeature, OverlayFeature, RoiFeature
{
    /**
     * The names of the columns of the resulting table.
     */
    public static final String[] colNames = new String[] { 
            "Oriented_Box_Center_X", 
            "Oriented_Box_Center_Y", 
            "Oriented_Box_Length", 
            "Oriented_Box_Width", 
            "Oriented_Box_Orientation" };

    /**
     * Default empty constructor.
     */
    public OrientedBoundingBox()
    {
    }
    
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
                .map(hull -> compute(hull, calib))
                .toArray(OrientedBox2D[]::new);
    }
    
    private static final OrientedBox2D compute(Polygon2D convexHull, Calibration calib)
    {
        // avoid null references
        if (convexHull == null) return null;
        
        // calibrate
        Polygon2D calibratedHull = new Polygon2D(calibrate(convexHull.vertices(), calib));
                
        // compute convex hull centroid
        Point2D center = calibratedHull.centroid();
        double cx = center.getX();
        double cy = center.getY();
        
        // coordinates of convex hull after spatial calibration and recentering
        ArrayList<Point2D> centeredHull = new ArrayList<Point2D>(convexHull.vertexNumber());
        for (Point2D p : calibratedHull)
        {
            double x = p.getX() - cx;
            double y = p.getY() - cy;
            centeredHull.add(new Point2D.Double(x, y));
        }

        AngleDiameterPair minFeret = FeretDiameters.minFeretDiameter(centeredHull);
        
        // orientation of the main axis
        // pre-compute trigonometric functions
        double cot = Math.cos(minFeret.angle);
        double sit = Math.sin(minFeret.angle);

        // compute elongation in direction of rectangle length and width
        double xmin = Double.MAX_VALUE;
        double ymin = Double.MAX_VALUE;
        double xmax = Double.MIN_VALUE;
        double ymax = Double.MIN_VALUE;
        for (Point2D p : centeredHull)
        {
            // coordinates of current point
            double x = p.getX(); 
            double y = p.getY();
            
            // compute rotated coordinates
            double x2 = x * cot + y * sit; 
            double y2 = - x * sit + y * cot;
            
            // update bounding box
            xmin = Math.min(xmin, x2);
            ymin = Math.min(ymin, y2);
            xmax = Math.max(xmax, x2);
            ymax = Math.max(ymax, y2);
        }
        
        // position of the center with respect to the centroid computed before
        double dl = (xmax + xmin) / 2;
        double dw = (ymax + ymin) / 2;

        // change coordinates from rectangle to user-space
        double dx  = dl * cot - dw * sit;
        double dy  = dl * sit + dw * cot;

        // coordinates of oriented box center
        cx += dx;
        cy += dy;

        // size of the rectangle
        double length = ymax - ymin;
        double width  = xmax - xmin;
        
        // store angle in degrees, between 0 and 180
        double angle = (Math.toDegrees(minFeret.angle) + 270) % 180;

        // Store results in a new instance of OrientedBox2D
        return new OrientedBox2D(cx, cy, length, width, angle);
    }
    
    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof OrientedBox2D[])
        {
            OrientedBox2D[] boxes = (OrientedBox2D[]) obj;
            for (int r = 0; r < boxes.length; r++)
            {
                OrientedBox2D obox = boxes[r];
                if (obox != null)
                {
                    Point2D center = obox.center();
                    table.setValue(colNames[0], r, center.getX());
                    table.setValue(colNames[1], r, center.getY());
                    table.setValue(colNames[2], r, obox.length());
                    table.setValue(colNames[3], r, obox.width());
                    table.setValue(colNames[4], r, obox.orientation());
                }
                else
                {
                    // populate columns of non-existing regions with NaN
                    for (String colName : colNames)
                    {
                        table.setValue(colName, r, Double.NaN);
                    }
                }
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of double");
        }
    }
    
    private static final ArrayList<Point2D> calibrate(ArrayList<Point2D> points, Calibration calib)
    {
        if (!calib.scaled())
        {
            return points;
        }
        
        ArrayList<Point2D> res = new ArrayList<Point2D>(points.size());
        for (Point2D point : points)
        {
            double x = point.getX() * calib.pixelWidth + calib.xOrigin;
            double y = point.getY() * calib.pixelHeight + calib.yOrigin;
            res.add(new Point2D.Double(x, y));
        }
        return res;
    }

    @Override
    public String[] columnUnitNames(RegionFeatures data)
    {
        String[] unitNames = new String[colNames.length];
        
        // setup table info
        Calibration calib = data.labelMap.getCalibration();
        String unitName = calib.getUnit();
        for (int c = 0; c < 4; c++)
        {
            unitNames[c] = unitName;
        }
        unitNames[4] = "degree";
        
        return unitNames;
    }
    
    /**
     * Displays results as overlay on the specified image.
     * 
     * @param target
     *            the {@code ImagePlus} whose overlay will be updated
     * @param data
     *            the instance of {@code RegionFeatures} containing the data
     */
    @Override
    public void overlayResult(ImagePlus target, RegionFeatures data, double strokeWidth)
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
            OverlayFeature.addRoiToOverlay(overlay, roi, color, strokeWidth);
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
    public Roi[] computeRois(RegionFeatures data)
    {
        // retrieve array of ellipses
        Object obj = data.results.get(this.getClass());
        if (!(obj instanceof OrientedBox2D[]))
        {
            throw new RuntimeException("Requires object argument to be an array of OrientedBox2D");
        }
        
        // convert each ellipse into a ROI
        return Stream.of((OrientedBox2D[]) obj)
                .map(box -> createRoi(box))
                .toArray(Roi[]::new);
    }

    private final static Roi createRoi(OrientedBox2D box)
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
        xp[0] = (float) (xc + dx * cot - dy * sit);
        yp[0] = (float) (yc + dx * sit + dy * cot);
        xp[1] = (float) (xc - dx * cot - dy * sit);
        yp[1] = (float) (yc - dx * sit + dy * cot);
        xp[2] = (float) (xc - dx * cot + dy * sit);
        yp[2] = (float) (yc - dx * sit - dy * cot);
        xp[3] = (float) (xc + dx * cot + dy * sit);
        yp[3] = (float) (yc + dx * sit - dy * cot);
        return new PolygonRoi(xp, yp, 4, Roi.POLYGON);
    }
    
    @Override
    public Collection<Class<? extends Feature>>requiredFeatures()
    {
        return Arrays.asList(ConvexHull.class);
    }
}
