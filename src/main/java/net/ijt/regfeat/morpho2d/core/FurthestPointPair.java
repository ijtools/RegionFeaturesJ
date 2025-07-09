/**
 * 
 */
package net.ijt.regfeat.morpho2d.core;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import inra.ijpb.algo.AlgoStub;
import inra.ijpb.geometry.FeretDiameters;
import inra.ijpb.geometry.PointPair2D;
import inra.ijpb.geometry.Polygon2D;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionTabularFeature;
import net.ijt.regfeat.RegionFeatures;

/**
 * Utility feature for MaxFeretDiameter, that computes the pair of points within
 * the region that are the furthest to each other.
 */
public class FurthestPointPair extends AlgoStub implements RegionTabularFeature
{
    @Override
    public PointPair2D[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        Polygon2D[] hulls = (Polygon2D[]) data.results.get(ConvexHull.class);
        int nLabels = hulls.length;
        
        // retrieve spatial calibration of image
        Calibration calib = data.labelMap.getCalibration();
        double sx = 1, sy = 1;
        double ox = 0, oy = 0;
        if (calib != null)
        {
            sx = calib.pixelWidth;
            sy = calib.pixelHeight;
            ox = calib.xOrigin;
            oy = calib.yOrigin;
        }

        // Compute the oriented box of each set of corner points
        PointPair2D[] labelMaxDiams = new PointPair2D[nLabels];

        // iterate over label
        for (int i = 0; i < nLabels; i++)
        {
            this.fireProgressChanged(this, i, nLabels);
            
            // calibrate the convex hull
            Polygon2D hull = hulls[i];
            ArrayList<Point2D> corners = new ArrayList<Point2D>(hull.vertexNumber());
            for (Point2D vertex : hull.vertices())
            {
                vertex = new Point2D.Double(vertex.getX() * sx + ox, vertex.getY() * sy + oy);
                corners.add(vertex);
            }

            // compute Feret diameter of calibrated hull
            labelMaxDiams[i] = FeretDiameters.maxFeretDiameter(corners);
        }
        
        return labelMaxDiams;
    }

    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof PointPair2D[])
        {
            PointPair2D[] array = (PointPair2D[]) obj;
            for (int r = 0; r < array.length; r++)
            {
                PointPair2D maxDiam = array[r];
                table.setValue("Max_Feret_Diameter", r, maxDiam.diameter());
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of PointPair2D");
        }
    }

    @Override
    public void overlayResult(ImagePlus image, RegionFeatures data)
    {
        // retrieve the result of computation
        PointPair2D[] diameters = (PointPair2D[]) data.results.get(this.getClass());
                
        // get spatial calibration of target image
        Calibration calib = image.getCalibration();
        
        // create overlay
        Overlay overlay = new Overlay();
        Roi roi;
        
        // add each box to the overlay
        for (int i = 0; i < diameters.length; i++) 
        {
            // Create ROI corresponding to diameter, in pixel coordinates
            roi = createDiametersRoi(diameters[i], calib);
            
            // add ROI to overlay
            Color color = data.labelColors[i];
            Feature.addRoiToOverlay(overlay, roi, color, 1.5);
        }
        
        image.setOverlay(overlay);
    }
    
    private Roi createDiametersRoi(PointPair2D pointPair, Calibration calib)
    {
        if (pointPair == null)
        {
            return null;
        }

        Point2D p1 = calibToPixel(pointPair.p1, calib);
        Point2D p2 = calibToPixel(pointPair.p2, calib);
        
        // Convert to Polyline ROI
        float[] x = new float[2];
        float[] y = new float[2];
        x[0] = (float) p1.getX();
        y[0] = (float) p1.getY();
        x[1] = (float) p2.getX();
        y[1] = (float) p2.getY();
        return new PolygonRoi(x, y, 2, Roi.POLYLINE);
    }
    
    private Point2D calibToPixel(Point2D point, Calibration calib)
    {
        double x = (point.getX() - calib.xOrigin) / calib.pixelWidth;
        double y = (point.getY() - calib.yOrigin) / calib.pixelHeight;
        return new Point2D.Double(x, y);
    }
    
    @Override
    public Collection<Class<? extends Feature>>requiredFeatures()
    {
        return Arrays.asList(ConvexHull.class);
    }
}
