/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import java.awt.Color;

import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import inra.ijpb.geometry.Box2D;
import inra.ijpb.measure.region2d.BoundingBox;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeature;
import net.ijt.regfeat.RegionFeatures;

/**
 * Computes the bounds of each region within a label map.
 */
public class Bounds implements RegionFeature
{
    @Override
    public Object compute(RegionFeatures data)
    {
        return BoundingBox.boundingBoxes(data.labelMap.getProcessor(), data.labels, data.labelMap.getCalibration());
    }

    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        Box2D[] boxes = (Box2D[]) data.results.get(this.getClass());
        
        for (int i = 0; i < boxes.length; i++)
        {
            // current box
            Box2D box = boxes[i];
            
            // coordinates of centroid
            table.setValue("Bounds2D.XMin", i, box.getXMin());
            table.setValue("Bounds2D.XMax", i, box.getXMax());
            table.setValue("Bounds2D.YMin", i, box.getYMin());
            table.setValue("Bounds2D.YMax", i, box.getYMax());
        }
    }
    
    @Override
    public void overlayResult(ImagePlus image, RegionFeatures data)
    {
        // retrieve the result of computation
        Box2D[] boxes = (Box2D[]) data.results.get(this.getClass());
                
        // get spatial calibration of target image
        Calibration calib = image.getCalibration();
        
        // create overlay
        Overlay overlay = new Overlay();
        Roi roi;
        
        // add each box to the overlay
        for (int i = 0; i < boxes.length; i++) 
        {
            // Create ROi corresponding to box, in pixel coordinates
            Box2D box = uncalibrate(boxes[i], calib);
            roi = createRoi(box);
            
            // add ROI to overlay
            Color color = data.labelColors[i];
            Feature.addRoiToOverlay(overlay, roi, color, 1.5);
        }
        
        image.setOverlay(overlay);
    }
    
    /**
     * Determines the box corresponding to the uncalibrated version of this
     * box, assuming it was defined in calibrated coordinates.
     * 
     * @param box
     *            the box in calibrated coordinates
     * @param calib
     *            the spatial calibration to consider
     * @return the circle in pixel coordinates
     */
    private final static Box2D uncalibrate(Box2D box, Calibration calib)
    {
        
        double xmin = (box.getXMin() - calib.xOrigin) / calib.pixelWidth;
        double xmax = (box.getXMax() - calib.xOrigin) / calib.pixelWidth;
        double ymin = (box.getYMin() - calib.yOrigin) / calib.pixelHeight;
        double ymax = (box.getYMax() - calib.yOrigin) / calib.pixelHeight;
        return new Box2D(xmin, xmax, ymin, ymax);
    }
    
    private final static Roi createRoi(Box2D box)
    {
        // Coordinates of box, in pixel coordinates
        double xmin = box.getXMin();
        double xmax = box.getXMax();
        double ymin = box.getYMin();
        double ymax = box.getYMax();
        
        return new Roi(xmin, ymin, xmax - xmin, ymax - ymin);
    }
}
