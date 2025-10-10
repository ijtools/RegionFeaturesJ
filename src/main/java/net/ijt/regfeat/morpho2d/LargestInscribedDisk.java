/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collection;

import ij.ImagePlus;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import ij.process.ImageProcessor;
import inra.ijpb.algo.AlgoStub;
import inra.ijpb.geometry.Circle2D;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.OverlayFeature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.RegionTabularFeature;
import net.ijt.regfeat.morpho2d.core.DistanceMapMaximaPosition;
import net.ijt.regfeat.morpho2d.core.DistanceMap_Chamfer_ChessKnight_Float;

/**
 * Computes the largest inscribed disk within regions of a label map.
 */
public class LargestInscribedDisk extends AlgoStub implements RegionTabularFeature,OverlayFeature
{
    /**
     * The names of the columns of the resulting table.
     */
    public static final String[] colNames = new String[] {"Inscribed_Disk_Center_X", "Inscribed_Disk_Center_Y", "Inscribed_Disk_Radius"};
    
    /**
     * Default empty constructor.
     */
    public LargestInscribedDisk()
    {
    }
    
    @Override
    public Circle2D[] compute(RegionFeatures data)
    {
        // retrieve meta data
        int nLabels = data.labels.length;
        Calibration calib = data.labelMap.getCalibration();
        
        // retrieve required features
        data.ensureRequiredFeaturesAreComputed(this);
        ImageProcessor distanceMap = ((ImagePlus) data.results.get(DistanceMap_Chamfer_ChessKnight_Float.class)).getProcessor();
        Point[] maximaPositions = (Point[]) data.results.get(DistanceMapMaximaPosition.class);
        
        // Create array of calibrated circles
        Circle2D[] circles = new Circle2D[nLabels];
        for (int i = 0; i < nLabels; i++) 
        {
            if (maximaPositions[i] != null && maximaPositions[i].x > -1)
            {
                Point center = maximaPositions[i];
                double xc = center.x * calib.pixelWidth + calib.xOrigin;
                double yc = center.y * calib.pixelHeight + calib.yOrigin;
                double radius = distanceMap.getf(center.x, center.y) * calib.pixelWidth;
                circles[i] = new Circle2D(new Point2D.Double(xc, yc), radius);
            }
        }

        return circles;
    }
    
    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof Circle2D[])
        {
            Circle2D[] array = (Circle2D[]) obj;
            for (int r = 0; r < array.length; r++)
            {
                Circle2D circle = array[r];
                if (circle != null)
                {
                    // coordinates of circle center
                    table.setValue(colNames[0], r, circle.getCenter().getX());
                    table.setValue(colNames[1], r, circle.getCenter().getY());

                    // circle radius
                    table.setValue(colNames[2], r, circle.getRadius());
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
            throw new RuntimeException("Requires object argument to be an array of Circle2D");
        }
    }
    
    @Override
    public String[] columnUnitNames(RegionFeatures data)
    {
        String[] unitNames = new String[colNames.length];
        String unitName = data.labelMap.getCalibration().getUnit();
        for (int c = 0; c < colNames.length; c++)
        {
            unitNames[c] = unitName;
        }
        return unitNames;
    }
    
    @Override
    public void overlayResult(ImagePlus image, RegionFeatures data)
    {
        // retrieve the result of computation
        Circle2D[] circles = (Circle2D[]) data.results.get(this.getClass());
                
        // get spatial calibration of target image
        Calibration calib = image.getCalibration();
        
        // create overlay
        Overlay overlay = new Overlay();
        Roi roi;
        
        // add each box to the overlay
        for (int i = 0; i < circles.length; i++) 
        {
            // Coordinates of inscribed circle, in pixel coordinates
            Circle2D circle = uncalibrate(circles[i], calib);
            Point2D center = circle.getCenter();
            double xi = center.getX();
            double yi = center.getY();
            double ri = circle.getRadius();
            
            // create ROI corresponding to circle
            int width = (int) Math.round(2 * ri);
            roi = new OvalRoi((int) (xi - ri), (int) (yi - ri), width, width);
            
            // add ROI to overlay
            Color color = data.labelColors[i];
            OverlayFeature.addRoiToOverlay(overlay, roi, color, 1.5);
        }
        
        image.setOverlay(overlay);
    }
    
    /**
     * Determines the circle corresponding to the uncalibrated version of this
     * circle, assuming it was defined in calibrated coordinates.
     * 
     * @param circle
     *            the circle in calibrated coordinates
     * @param calib
     *            the spatial calibration to consider
     * @return the circle in pixel coordinates
     */
    private final static Circle2D uncalibrate(Circle2D circle, Calibration calib)
    {
        Point2D center = circle.getCenter();
        double xc = (center.getX() - calib.xOrigin) / calib.pixelWidth;
        double yc = (center.getY() - calib.yOrigin) / calib.pixelHeight;
        double radius = circle.getRadius() / calib.pixelWidth;
        return new Circle2D(new Point2D.Double(xc, yc), radius);
    }
    
    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(DistanceMap_Chamfer_ChessKnight_Float.class, DistanceMapMaximaPosition.class);
    }
}
