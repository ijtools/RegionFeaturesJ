/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import ij.measure.Calibration;
import ij.measure.ResultsTable;
import inra.ijpb.geometry.FeretDiameters;
import inra.ijpb.geometry.PointPair2D;
import inra.ijpb.geometry.Polygon2D;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.morpho2d.core.ConvexHull;

/**
 * 
 */
public class MaxFeretDiameter extends Feature
{

    public MaxFeretDiameter()
    {
        this.requiredFeatures.add(ConvexHull.class);
    }
    
    @Override
    public PointPair2D[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        ensureRequiredFeaturesAreComputed(data);
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
//            this.fireProgressChanged(this, i, nLabels);
            
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

}
