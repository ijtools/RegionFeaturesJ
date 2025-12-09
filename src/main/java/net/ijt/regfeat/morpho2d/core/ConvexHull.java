/**
 * 
 */
package net.ijt.regfeat.morpho2d.core;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;

import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import inra.ijpb.algo.AlgoStub;
import inra.ijpb.geometry.Polygon2D;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.OverlayFeature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.RoiFeature;

/**
 * Computes the convex hull of each region in pixel coordinates.
 */
public class ConvexHull extends AlgoStub implements OverlayFeature, RoiFeature
{
    private static final double TWO_PI = 2 * Math.PI;
    
    private static final Polygon2D convexHullFromMap(TreeMap<Double, TreeSet<Double>> coordsData)
    {
        // Init iteration on points
        double yMin = coordsData.firstKey();
        double xMax = coordsData.firstEntry().getValue().last();
        Point2D lowestPoint = new Point2D.Double(xMax, yMin);

        // initialize an empty array of points for hull vertices
        ArrayList<Point2D> vertices = new ArrayList<Point2D>();

        // Init iteration on points
        Point2D currentPoint = lowestPoint;
        Point2D nextPoint = null;
        double angle = 0;

        // Iterate on point set to find point with smallest angle with respect
        // to previous line
        do 
        {
            vertices.add(currentPoint);
            nextPoint = findNextPoint(currentPoint, angle, coordsData);
            angle = computeAngle(currentPoint, nextPoint);
            currentPoint = nextPoint;
        }
        while (currentPoint.getX() != lowestPoint.getX() || currentPoint.getY() != lowestPoint.getY());

        // Create a polygon with points located on the convex hull
        return new Polygon2D(vertices);
    }
    
    private static final Point2D findNextPoint(Point2D basePoint, double startAngle, TreeMap<Double, TreeSet<Double>> coordsData)
    {
        double minAngle = Double.MAX_VALUE;
        double minX  = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double angle;
        
        for(Map.Entry<Double, TreeSet<Double>> entry : coordsData.entrySet())
        {
            double y = entry.getKey();
            // consider only extreme x-coordinates on the current row
            double[] xRange = new double[] {entry.getValue().getFirst(), entry.getValue().getLast()};
            for (double x : xRange)
            {
                // Avoid to test same point
                if (basePoint.getX() == x && basePoint.getY() == y) continue;
                
                // Compute angle between current direction and next point
                angle = computeAngle(basePoint, x, y);
                angle = diffAngle(startAngle, angle);
                
                // Keep current point if angle is minimal
                if (angle < minAngle)
                {
                    minAngle = angle;
                    minX = x;
                    minY = y;
                }
                else if (angle == minAngle && basePoint.distance(x, y) > basePoint.distance(minX, minY))
                {
                    // if angle is the same, keep only the furthest point
                    minAngle = angle;
                    minX = x;
                    minY = y;
                }
            }
        }
        
        return new Point2D.Double(minX, minY);
    }
    
    /**
     * Computes the horizontal angle of the straight line going through two
     * points.
     * 
     * @param p1
     *            the first point
     * @param p2
     *            the second point
     * @return the horizontal angle of the straight line going through the two
     *         points, between 0 and 2*PI.
     */
    private static final double computeAngle(Point2D p1, Point2D p2)
    {
        return (Math.atan2(p2.getY() - p1.getY(), p2.getX() - p1.getX()) + TWO_PI) % TWO_PI;
    }
    
    private static final double computeAngle(Point2D p1, double x2, double y2)
    {
        return (Math.atan2(y2 - p1.getY(), x2 - p1.getX()) + TWO_PI) % TWO_PI;
    }
    
    private static final double diffAngle(double startAngle, double endAngle)
    {
        return (endAngle - startAngle + TWO_PI) % TWO_PI; 
    }

    /**
     * Default empty constructor.
     */
    public ConvexHull()
    {
    }
    
    @Override
    public Polygon2D[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        @SuppressWarnings("unchecked")
        TreeMap<Double, TreeSet<Double>>[] coordsData = (TreeMap<Double, TreeSet<Double>>[]) data.results.get(BoundaryEdgesMidPoints.class);

        // compute convex hull of boundary points around each region
        Polygon2D[] hulls = Arrays.stream(coordsData)
                .map(m -> convexHullFromMap(m))
                .toArray(Polygon2D[]::new);
        
        return hulls;
    }
    
    @Override
    public void overlayResult(ImagePlus image, RegionFeatures data, double strokeWidth)
    {
        // retrieve the result of computation
        Polygon2D[] polygons = (Polygon2D[]) data.results.get(this.getClass());
                
        // create overlay
        Overlay overlay = new Overlay();
        
        // add each box to the overlay
        for (int i = 0; i < polygons.length; i++) 
        {
            Roi roi = convertToRoi(polygons[i]);
            
            // add ROI to overlay
            Color color = data.labelColors[i];
            OverlayFeature.addRoiToOverlay(overlay, roi, color, strokeWidth);
        }
        
        image.setOverlay(overlay);
    }
    
    @Override
    public Roi[] computeRois(RegionFeatures data)
    {
        // retrieve array of ellipses
        Object obj = data.results.get(this.getClass());
        if (!(obj instanceof Polygon2D[]))
        {
            throw new RuntimeException("Requires object argument to be an array of Polygon2D");
        }
        
        // convert each polygon into a ROI
        return Stream.of((Polygon2D[]) obj)
                .map(poly -> convertToRoi(poly))
                .toArray(Roi[]::new);
    }

    private Roi convertToRoi(Polygon2D poly)
    {
        int nv = poly.vertexNumber();
        float[] xdata = new float[nv];
        float[] ydata = new float[nv];
        for (int i = 0; i < nv; i++)
        {
            Point2D p = poly.getVertex(i);
            xdata[i] = (float) p.getX();
            ydata[i] = (float) p.getY();
        }
        
        return new PolygonRoi(xdata, ydata, nv, Roi.POLYGON);
    }
    
    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(BoundaryEdgesMidPoints.class);
    }
}
