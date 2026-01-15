/**
 * 
 */
package net.ijt.regfeat.morpho3d.core;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.TreeMap;
import java.util.TreeSet;

import ij.measure.Calibration;
import inra.ijpb.algo.AlgoStub;
import inra.ijpb.geometry.Point3D;
import inra.ijpb.geometry.PointPair3D;
import inra.ijpb.geometry.Polygon2D;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.morpho2d.core.ConvexHull;

/**
 * Utility feature for MaxFeretDiameter3D, that computes the pair of region's
 * boundary points that are the furthest to each other.
 * 
 * The resulting points are in calibrated coordinates.
 */
public class FurthestBoundaryPoints extends AlgoStub implements Feature
{
    /**
     * Default empty constructor.
     */
    public FurthestBoundaryPoints()
    {
    }

    @Override
    public PointPair3D[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        @SuppressWarnings("unchecked")
        TreeMap<Double, TreeMap<Double, TreeSet<Double>>>[] boundaryPointCoords = (TreeMap<Double, TreeMap<Double, TreeSet<Double>>>[]) data.results.get(BoundaryFacesMidPoints.class);
        int nLabels = boundaryPointCoords.length;
        
        // retrieve spatial calibration of image
        Calibration calib = data.labelMap.getCalibration();
        double sx = 1, sy = 1, sz = 1;
        double ox = 0, oy = 0, oz = 0;
        if (calib != null)
        {
            sx = calib.pixelWidth;
            sy = calib.pixelHeight;
            sz = calib.pixelDepth;
            ox = calib.xOrigin;
            oy = calib.yOrigin;
            oz = calib.zOrigin;
        }

        // Compute the oriented box of each set of corner points
        PointPair3D[] labelMaxDiams = new PointPair3D[nLabels];

        // iterate over regions
        for (int i = 0; i < nLabels; i++)
        {
            this.fireProgressChanged(this, i, nLabels);
            
            if (boundaryPointCoords[i] != null)
            {
                Collection<Point3D> boundaryPoints = reducePoints(boundaryPointCoords[i]);
                
                // calibrate the convex hull
                ArrayList<Point3D> corners = new ArrayList<Point3D>(boundaryPoints.size());
                for (Point3D p : boundaryPoints)
                {
                    corners.add(new Point3D(p.getX() * sx + ox, p.getY() * sy + oy, p.getZ() * sz + oz));
                }

                // compute Feret diameter of calibrated hull
                labelMaxDiams[i] = furthestPoints(corners);
            }
        }
        
        return labelMaxDiams;
    }
    
    private static final Collection<Point3D> reducePoints(TreeMap<Double, TreeMap<Double, TreeSet<Double>>> boundaryPoints)
    {
        Collection<Point3D> points = new ArrayList<Point3D>();
        
        for (double z : boundaryPoints.keySet())
        {
            // compute convex hull of points on current z-slice
            TreeMap<Double, TreeSet<Double>> mapZ = boundaryPoints.get(z);
            
            if (mapZ.size() > 1)
            {
                Polygon2D sliceHull = ConvexHull.convexHullFromMap(mapZ);

                // convert vertices of convex hull to 3D points
                for (Point2D p : sliceHull.vertices())
                {
                    points.add(new Point3D(p.getX(), p.getY(), z));
                }
            }
            else
            {
                // points are located only on one row, 
                // we just need to extract extreme point(s)
                double y = mapZ.firstKey();
                TreeSet<Double> xCoords = mapZ.firstEntry().getValue();
                points.add(new Point3D(xCoords.first(), y, z));
                if (xCoords.size() > 1)
                {
                    points.add(new Point3D(xCoords.last(), y, z));
                }
            }
        }
        
        return points;
    }
    
    
    
    /**
     * Computes Maximum Feret diameter of a set of points.
     * 
     * Note: it is often a good idea to compute convex hull before computing
     * Feret diameter.
     * 
     * @param points
     *            a collection of planar points
     * @return the maximum Feret diameter of the point set
     */
    private final static PointPair3D furthestPoints(ArrayList<? extends Point3D> points)
    {
        double distMax = Double.NEGATIVE_INFINITY;
        PointPair3D maxDiam = null;
        
        int n = points.size();
        for (int i1 = 0; i1 < n - 1; i1++)
        {
            Point3D p1 = points.get(i1);
            for (int i2 = i1 + 1; i2 < n; i2++)
            {
                Point3D p2 = points.get(i2);
        
                double dist = p1.distance(p2);
                if (dist > distMax)
                {
                    maxDiam = new PointPair3D(p1, p2);
                    distMax = dist;
                }
            }
        }
    
        return maxDiam;
    }
    

    @Override
    public Collection<Class<? extends Feature>>requiredFeatures()
    {
        return Arrays.asList(BoundaryFacesMidPoints.class);
    }
}
