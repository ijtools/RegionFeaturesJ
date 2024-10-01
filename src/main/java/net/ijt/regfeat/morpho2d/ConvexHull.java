/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import ij.measure.ResultsTable;
import ij.process.ImageProcessor;
import inra.ijpb.geometry.Polygon2D;
import inra.ijpb.geometry.Polygons2D;
import inra.ijpb.measure.region2d.RegionBoundaries;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;

/**
 * Computes the convex hull of each region in pixel coordinates.
 */
public class ConvexHull extends Feature
{

    @Override
    public Polygon2D[] compute(RegionFeatures data)
    {
        ImageProcessor image = data.labelMap.getProcessor();
        int[] labels = data.labels;
        
        // for each region, extract the points at the middle of the boundary edges
        ArrayList<Point2D>[] pointArrays = RegionBoundaries.boundaryPixelsMiddleEdges(image, labels);

        // compute convex hull of boundary points around each region
        Polygon2D[] hulls = new Polygon2D[labels.length];
        for (int i = 0; i < labels.length; i++)
        {
            // compute convex hull of boundary points around the binary particle
            hulls[i] = Polygons2D.convexHull(pointArrays[i]);
        }
        
        return hulls;
    }

    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        // do nothing
    }

}
