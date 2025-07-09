/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import java.awt.Color;
import java.util.HashMap;

import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import ij.process.ImageProcessor;
import inra.ijpb.algo.AlgoStub;
import inra.ijpb.geometry.Box2D;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionTabularFeature;
import net.ijt.regfeat.RegionFeatures;

/**
 * Computes the bounds of each region within a label map.
 */
public class Bounds extends AlgoStub implements RegionTabularFeature
{
    public static final String[] colNames = new String[] {"Bounds2D_XMin", "Bounds2D_XMax", "Bounds2D_YMin", "Bounds2D_YMax"};
    
    @Override
    public Box2D[] compute(RegionFeatures data)
    {
        // retrieve label map and list of labels
        ImageProcessor labelMap = data.labelMap.getProcessor();
        int[] labels = data.labels;
        Calibration calib = data.labelMap.getCalibration();
        
        // size of image
        int sizeX = labelMap.getWidth();
        int sizeY = labelMap.getHeight();

        // Extract spatial calibration
        double sx = 1, sy = 1;
        double ox = 0, oy = 0;
        if (calib != null)
        {
            sx = calib.pixelWidth;
            sy = calib.pixelHeight;
            ox = calib.xOrigin;
            oy = calib.yOrigin;
        }
        
        // create associative array to know index of each label
        HashMap<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(labels);

        // allocate memory for result
        int nLabels = labels.length;
        double[] xmin = new double[nLabels];
        double[] xmax = new double[nLabels];
        double[] ymin = new double[nLabels];
        double[] ymax = new double[nLabels];
        
        // initialize to extreme values
        for (int i = 0; i < nLabels; i++)
        {
            xmin[i] = Double.POSITIVE_INFINITY;
            xmax[i] = Double.NEGATIVE_INFINITY;
            ymin[i] = Double.POSITIVE_INFINITY;
            ymax[i] = Double.NEGATIVE_INFINITY;
        }

        // compute extreme coordinates of each region
        fireStatusChanged(this, "Compute bounds");
        for (int y = 0; y < sizeY; y++) 
        {
            for (int x = 0; x < sizeX; x++)
            {
                int label = (int) labelMap.getf(x, y);
                if (label == 0)
                    continue;

                // do not process labels that are not in the input list 
                if (!labelIndices.containsKey(label))
                    continue;
                
                int index = labelIndices.get(label);
                
                xmin[index] = Math.min(xmin[index], x);
                xmax[index] = Math.max(xmax[index], x);
                ymin[index] = Math.min(ymin[index], y);
                ymax[index] = Math.max(ymax[index], y);
            }
        }

        // create bounding box instances
        Box2D[] boxes = new Box2D[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
            boxes[i] = new Box2D(
                    xmin[i] * sx + ox, (xmax[i] + 1) * sx + ox,
                    ymin[i] * sy + oy, (ymax[i] + 1) * sy + oy);
        }
        return boxes;
    }
    
    /**
     * Computes bounding box of each region in input label image.
     * 
     * @param image
     *            the input image containing label of particles
     * @param labels
     *            the array of labels within the image
     * @param calib
     *            the calibration of the image
     * @return an array of Box2D representing the calibrated coordinates of
     *         the bounding box of each region
     */
    public Box2D[] analyzeRegions(ImageProcessor image, int[] labels, Calibration calib)
    {
        // size of image
        int sizeX = image.getWidth();
        int sizeY = image.getHeight();

        // Extract spatial calibration
        double sx = 1, sy = 1;
        double ox = 0, oy = 0;
        if (calib != null)
        {
            sx = calib.pixelWidth;
            sy = calib.pixelHeight;
            ox = calib.xOrigin;
            oy = calib.yOrigin;
        }
        
        // create associative array to know index of each label
        HashMap<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(labels);

        // allocate memory for result
        int nLabels = labels.length;
        double[] xmin = new double[nLabels];
        double[] xmax = new double[nLabels];
        double[] ymin = new double[nLabels];
        double[] ymax = new double[nLabels];
        
        // initialize to extreme values
        for (int i = 0; i < nLabels; i++)
        {
            xmin[i] = Double.POSITIVE_INFINITY;
            xmax[i] = Double.NEGATIVE_INFINITY;
            ymin[i] = Double.POSITIVE_INFINITY;
            ymax[i] = Double.NEGATIVE_INFINITY;
        }

        // compute extreme coordinates of each region
        fireStatusChanged(this, "Compute bounds");
        for (int y = 0; y < sizeY; y++) 
        {
            for (int x = 0; x < sizeX; x++)
            {
                int label = (int) image.getf(x, y);
                if (label == 0)
                    continue;

                // do not process labels that are not in the input list 
                if (!labelIndices.containsKey(label))
                    continue;
                
                int index = labelIndices.get(label);
                
                xmin[index] = Math.min(xmin[index], x);
                xmax[index] = Math.max(xmax[index], x);
                ymin[index] = Math.min(ymin[index], y);
                ymax[index] = Math.max(ymax[index], y);
            }
        }

        // create bounding box instances
        Box2D[] boxes = new Box2D[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
            boxes[i] = new Box2D(
                    xmin[i] * sx + ox, (xmax[i] + 1) * sx + ox,
                    ymin[i] * sy + oy, (ymax[i] + 1) * sy + oy);
        }
        return boxes;
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
            table.setValue(colNames[0], i, box.getXMin());
            table.setValue(colNames[1], i, box.getXMax());
            table.setValue(colNames[2], i, box.getYMin());
            table.setValue(colNames[3], i, box.getYMax());
        }
    }
    
    @Override
    public String[] columnUnitNames(RegionFeatures data)
    {
        String unit = data.labelMap.getCalibration().getUnit();
        return new String[] {unit, unit, unit, unit};
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
