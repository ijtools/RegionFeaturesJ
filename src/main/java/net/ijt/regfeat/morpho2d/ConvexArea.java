/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collection;

import ij.measure.Calibration;
import inra.ijpb.geometry.Box2D;
import inra.ijpb.geometry.Polygon2D;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.SingleValueFeature;
import net.ijt.regfeat.morpho2d.core.ConvexHull;

/**
 * Computes the convex area, or area of the convex hull.
 */
public class ConvexArea extends SingleValueFeature
{
    /**
     * Default empty constructor.
     */
    public ConvexArea()
    {
        super("Convex_Area");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
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
            
            if (convexHull != null)
            {
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
        }
        
        return convexAreas;
    }

    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(ConvexHull.class);
    }
    
    @Override
    public String[] columnUnitNames(RegionFeatures data)
    {
        return new String[] {data.labelMap.getCalibration().getUnit() + "^2"};
    }
}
