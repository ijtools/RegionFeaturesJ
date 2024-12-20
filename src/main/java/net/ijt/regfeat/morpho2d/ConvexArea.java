/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collection;

import ij.measure.Calibration;
import ij.measure.ResultsTable;
import inra.ijpb.geometry.Box2D;
import inra.ijpb.geometry.Polygon2D;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.morpho2d.core.ConvexHull;

/**
 * Computes the convex area, or area of the convex hull.
 */
public class ConvexArea implements RegionFeature
{
    @Override
    public Object compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        Polygon2D[] hulls = (Polygon2D[]) data.results.get(ConvexHull.class);
        
        // retrieve label map data
        int[] labels = data.labels;
        Calibration calib = data.labelMap.getCalibration();
        double pixelArea = 1.0;
        if (calib != null)
        {
            pixelArea = calib.pixelWidth * calib.pixelHeight;
        }
        
        // iterate over labels
        double[] convexAreas = new double[labels.length];
        for (int i = 0; i < labels.length; i++)
        {
            Polygon2D convexHull = hulls[i];
            
            // determine bounds
            Box2D box = convexHull.boundingBox();
            int xmin = (int) box.getXMin();
            int xmax = (int) box.getXMax();
            int ymin = (int) box.getYMin();
            int ymax = (int) box.getYMax();
            
            // counts the number of pixels with integer coordinates within the convex hull
            double convexArea = 0;
            for (int y = ymin; y < ymax; y++)
            {
                for (int x = xmin; x < xmax; x++)
                {
                    if (convexHull.contains(new Point2D.Double(x + 0.5, y + 0.5)))
                    {
                        convexArea++;
                    }
                }
            }
            
            // compute calibrated convex area
            convexAreas[i] = convexArea * pixelArea;
        }
        
        return convexAreas;
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
