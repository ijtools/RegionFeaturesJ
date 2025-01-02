/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.PointRoi;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.measure.Calibration;
import inra.ijpb.measure.region2d.GeodesicDiameter.Result;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.SingleValueFeature;
import net.ijt.regfeat.morpho2d.core.GeodesicDiameterData;

/**
 * Computes the geodesic diameter of each region.
 * 
 * @see GeodesicElongation
 * @see Tortuosity
 */
public class GeodesicDiameter extends SingleValueFeature
{
    public GeodesicDiameter()
    {
        super("Geodesic_Diameter");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        Result[] results = (Result[]) data.results.get(GeodesicDiameterData.class);
        
        // iterate over labels
        double[] geodDiams = new double[results.length];
        for (int i = 0; i < results.length; i++)
        {
            geodDiams[i] = results[i].diameter;
        }
        
        return geodDiams;
    }

    public void overlayResult(RegionFeatures data, ImagePlus target)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        Result[] results = (Result[]) data.results.get(GeodesicDiameterData.class);
        
        // get spatial calibration of target image
        Calibration calib = target.getCalibration();
        
        // create overlay
        Overlay overlay = new Overlay();
        
        // add each ellipse to the overlay
        for (int i = 0; i < results.length; i++) 
        {
            // Create ROI corresponding to geodesic path, in pixel coordinates
            Roi roi = createPathRoi(results[i].path, calib);

            // add ROI to overlay
            Color color = data.labelColors[i];
            Feature.addRoiToOverlay(overlay, roi, color, 1.5);
        }
        
        target.setOverlay(overlay);
    }

    private static final Roi createPathRoi(List<Point2D> path, Calibration calib)
    {
        if (path == null)
        {
            return null;
        }
        
        if (path.size() > 1)
        {
            // Polyline path
            int n = path.size();
            float[] x = new float[n];
            float[] y = new float[n];
            int i = 0;
            for (Point2D pos : path)
            {
                pos = calibToPixel(pos, calib);
                x[i] = (float) (pos.getX() + 0.5f);
                y[i] = (float) (pos.getY() + 0.5f);
                i++;
            }
            return new PolygonRoi(x, y, n, Roi.POLYLINE);
        }
        else if (path.size() == 1)
        {
            // case of single point particle
            Point2D pos = calibToPixel(path.get(0), calib);
            return new PointRoi(pos.getX() + 0.5, pos.getY() + 0.5);
        }
        else
        {
            throw new RuntimeException("Can not manage empty paths");
        }
    }
    
    private static final Point2D calibToPixel(Point2D point, Calibration calib)
    {
        double x = (point.getX() - calib.xOrigin) / calib.pixelWidth;
        double y = (point.getY() - calib.yOrigin) / calib.pixelHeight;
        return new Point2D.Double(x, y);
    }
    
    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(GeodesicDiameterData.class);
    }
}
