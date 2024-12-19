/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collection;

import ij.measure.Calibration;
import ij.measure.ResultsTable;
import inra.ijpb.geometry.Polygon2D;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.morpho2d.core.ConvexHull;

/**
 * Computes the convex perimeter, or perimeter of the convex hull.
 * 
 * The perimeter is obtained as the length of boundary of the convex hull, and
 * may be different from the perimeter of the image of the convex image.
 */
public class ConvexPerimeter extends RegionFeature
{
    @Override
    public Object compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        Polygon2D[] hulls = (Polygon2D[]) data.results.get(ConvexHull.class);
        
        // retrieve calibration
        Calibration calib = data.labelMap.getCalibration();
        
        // iterate over labels
        double[] convexPerimeters = new double[hulls.length];
        for (int i = 0; i < hulls.length; i++)
        {
            // compute calibrated convex perimeter
            convexPerimeters[i] = perimeter(hulls[i]) * calib.pixelWidth;
        }
        
        return convexPerimeters;
    }
    
    private double perimeter(Polygon2D poly)
    {
        int nv = poly.vertexNumber();
        Point2D prev = poly.getVertex(nv - 1);
        
        double perim = 0;
        for (Point2D vertex : poly.vertices())
        {
            perim += vertex.distance(prev);
            prev = vertex;
        }
        return perim;
    }

    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof double[])
        {
            double[] array = (double[]) obj;
            for (int r = 0; r < array.length; r++)
            {
                table.setValue("Convex_Area", r, array[r]);
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of double");
        }
    }

    @Override
    public Collection<Class<? extends Feature>>requiredFeatures()
    {
        return Arrays.asList(ConvexHull.class);
    }
}
