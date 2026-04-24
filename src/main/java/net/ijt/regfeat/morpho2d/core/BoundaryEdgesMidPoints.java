/**
 * 
 */
package net.ijt.regfeat.morpho2d.core;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;

import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.PointRoi;
import ij.gui.Roi;
import ij.process.ImageProcessor;
import inra.ijpb.algo.AlgoStub;
import net.ijt.regfeat.OverlayFeature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.RoiFeature;

/**
 * Returns the coordinates of the points located at the middle of boundary
 * edges. Points are stored in a nested Map data structure, in pixel
 * coordinates.
 */
public class BoundaryEdgesMidPoints extends AlgoStub implements OverlayFeature, RoiFeature
{
    /**
     * Empty default constructor.
     */
    public BoundaryEdgesMidPoints()
    {
    }
    
    @Override
    public void overlayResult(ImagePlus target, RegionFeatures data, double strokeWidth)
    {
        // convert region feature to ROI
        Roi[] rois = computeRois(data);
                
        // create overlay
        Overlay overlay = new Overlay();
        
        // add each ellipse to the overlay
        for (int i = 0; i < rois.length; i++) 
        {
            // convert region feature to ROI
            Roi roi = rois[i];
    
            // roi corresponding to ellipse
            Color color = data.labelColors[i];
            OverlayFeature.addRoiToOverlay(overlay, roi, color, strokeWidth);
        }
        
        target.setOverlay(overlay);
    }

    @Override
    public Roi[] computeRois(RegionFeatures data)
    {
        // retrieve array of maps
        Object obj = data.results.get(this.getClass());
        if (!(obj instanceof TreeMap[]))
        {
            throw new RuntimeException("Requires object argument to be an array of TreeMap");
        }
        
        @SuppressWarnings("unchecked")
        TreeMap<Double, TreeSet<Double>>[] coordsData = (TreeMap<Double, TreeSet<Double>>[]) obj;
        
        // convert each ellipse into a ROI
        return Stream.of(coordsData)
                .map(map -> createRoi(map))
                .toArray(Roi[]::new);
    }

    private final static Roi createRoi(TreeMap<Double, TreeSet<Double>> map)
    {
        ArrayList<Point2D.Float> points = new ArrayList<Point2D.Float>();
        for (double y : map.keySet())
        {
            for (double x : map.get(y))
            {
                points.add(new Point2D.Float((float) x, (float) y));
            }
        }
        
        int np = points.size();
        float[] xdata = new float[np];
        float[] ydata = new float[np];
        for (int i = 0; i < np; i++)
        {
            xdata[i] = points.get(i).x - 0.5f;
            ydata[i] = points.get(i).y - 0.5f;
        }
        return new PointRoi(xdata, ydata);
    }

    @Override
    public TreeMap<Double, TreeSet<Double>>[] compute(RegionFeatures data)
    {
        // retrieve label map data
        ImageProcessor labelMap = data.labelMap.getProcessor();
        int sizeX = labelMap.getWidth();
        int sizeY = labelMap.getHeight();
        
        // label data
        HashMap<Integer, Integer> labelIndices = data.labelIndices;
        int nLabels = labelIndices.size();
        
        // allocate data structure for storing results
        // for each region, organize the boundary points within a map, using the
        // y-coordinate of the points as map key, and listing all the
        // x-coordinates within the row within an TreeSet
        @SuppressWarnings("unchecked")
        TreeMap<Double, TreeSet<Double>>[] pointMaps = (TreeMap<Double, TreeSet<Double>>[]) new TreeMap<?,?>[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
            pointMaps[i] = new TreeMap<Double, TreeSet<Double>>();
        }
        
        // labels for current, up, and left pixels.
        int label = 0;
        int labelUp = 0;
        int labelLeft = 0;
        
        // iterate on image pixel configurations
        for (int y = 0; y < sizeY + 1; y++) 
        {
            this.fireProgressChanged(this, y, sizeY);
            
            labelLeft = 0;
            for (int x = 0; x < sizeX + 1; x++) 
            {
                // update pixel values of configuration
                label = x < sizeX & y < sizeY ? (int) labelMap.getf(x, y) : 0;
                labelUp = x < sizeX & y > 0 ? (int) labelMap.getf(x, y - 1) : 0;

                // check boundary with upper pixel
                if (labelUp != label)
                {
                    if (labelIndices.containsKey(label))
                    {
                        int index = labelIndices.get(label);
                        addPoint(pointMaps[index], x + 0.5, y);
                    }
                    if (labelIndices.containsKey(labelUp))
                    {
                        int index = labelIndices.get(labelUp);
                        addPoint(pointMaps[index], x + 0.5, y);
                    }
                }
                
                // check boundary with left pixel
                if (labelLeft != label)
                {
                    if (labelIndices.containsKey(label))
                    {
                        int index = labelIndices.get(label);
                        addPoint(pointMaps[index], x, y + 0.5);
                    }
                    if (labelIndices.containsKey(labelLeft))
                    {
                        int index = labelIndices.get(labelLeft);
                        addPoint(pointMaps[index], x, y + 0.5);
                    }
                }

                // update values of left label for next iteration
                labelLeft = label;
            }
        }

        return pointMaps;
    }
    
    private static final void addPoint(TreeMap<Double, TreeSet<Double>> map, double x, double y)
    {
        TreeSet<Double> set = map.get(y);
        if (set == null)
        {
            set = new TreeSet<Double>();
        }
        set.add(x);
        map.put(y, set);
    }
}
