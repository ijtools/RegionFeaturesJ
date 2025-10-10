/**
 * 
 */
package net.ijt.regfeat.morpho2d.core;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.process.ImageProcessor;
import inra.ijpb.algo.AlgoStub;
import inra.ijpb.geometry.Polygon2D;
import inra.ijpb.geometry.Polygons2D;
import net.ijt.regfeat.OverlayFeature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.RoiFeature;

/**
 * Computes the convex hull of each region in pixel coordinates.
 */
public class ConvexHull extends AlgoStub implements OverlayFeature, RoiFeature
{
    /**
     * Default empty constructor.
     */
    public ConvexHull()
    {
    }
    
    @Override
    public Polygon2D[] compute(RegionFeatures data)
    {
        // retrieve data
        ImageProcessor image = data.labelMap.getProcessor();
        
        // for each region, extract the points at the middle of the boundary edges
        ArrayList<Point2D>[] pointArrays = boundaryPixelsMiddleEdges(image, data.labelIndices);
        
        // compute convex hull of boundary points around each region
        Polygon2D[] hulls = Arrays.stream(pointArrays)
            .map(array -> !array.isEmpty() ? Polygons2D.convexHull(array) : null)
            .toArray(Polygon2D[]::new);
        
        return hulls;
    }
    
    /**
     * Extracts boundary points from the different regions.
     * 
     * This method considers middle points of pixel edges, assuming a "diamond
     * shape" for pixels. For a single pixel (x,y), ImageJ considers equivalent
     * area to be [x,x+1[ x [y,y+1[, and pixel center at (x+0.5, y+0.5).
     * 
     * The boundaries extracted by this methods have following coordinates:
     * <ul>
     * <li><i>(x+0.5, y)</i>: top boundary</li>
     * <li><i>(x , y+0.5)</i>: left boundary</li>
     * <li><i>(x+1 , y+0.5)</i>: right boundary</li>
     * <li><i>(x+0.5, y+1)</i>: bottom boundary</li>
     * </ul>
     * 
     * @see inra.ijpb.measure.region2d.RegionBoundaries#boundaryPixelsMiddleEdges(ImageProcessor, int[])
     * 
     * @param labelImage
     *            the image processor containing the region labels
     * @param labels
     *            the array of region labels
     * @return an array of arrays of boundary points, one array for each label.
     */
    private ArrayList<Point2D>[] boundaryPixelsMiddleEdges(ImageProcessor labelImage, HashMap<Integer, Integer> labelIndices)
    {
        // size of image
        int sizeX = labelImage.getWidth();
        int sizeY = labelImage.getHeight();
        
        int nLabels = labelIndices.size();
        
        // allocate data structure for storing results
        @SuppressWarnings("unchecked")
        ArrayList<Point2D>[] pointArrays = (ArrayList<Point2D>[]) new ArrayList<?>[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
            pointArrays[i] = new ArrayList<Point2D>();
        }
        
        // labels for current, up, and left pixels.
        int label = 0;
        int labelUp = 0;
        int labelLeft = 0;
        
        // iterate on image pixel configurations
        for (int y = 0; y < sizeY + 1; y++) 
        {
            this.fireProgressChanged(this, y, sizeY);
            
            for (int x = 0; x < sizeX + 1; x++) 
            {
                // update pixel values of configuration
                label = x < sizeX & y < sizeY ? (int) labelImage.getf(x, y): 0;
                labelUp = x < sizeX & y > 0 ? (int) labelImage.getf(x, y - 1): 0;

                // check boundary with upper pixel
                if (labelUp != label)
                {
                    Point2D p = new Point2D.Double(x + 0.5, y);
                    if (labelIndices.containsKey(label))
                    {

                        int index = labelIndices.get(label);
                        pointArrays[index].add(p);
                    }
                    if (labelIndices.containsKey(labelUp))
                    {
                        int index = labelIndices.get(labelUp);
                        pointArrays[index].add(p);
                    }
                }
                
                // check boundary with left pixel
                if (labelLeft != label)
                {
                    Point2D p = new Point2D.Double(x, y + 0.5);
                    if (labelIndices.containsKey(label))
                    {
                        int index = labelIndices.get(label);
                        pointArrays[index].add(p);
                    }
                    if (labelIndices.containsKey(labelLeft))
                    {
                        int index = labelIndices.get(labelLeft);
                        pointArrays[index].add(p);
                    }
                }

                // update values of left label for next iteration
                labelLeft = label;
            }
        }

        return pointArrays;
    }
    
    @Override
    public void overlayResult(ImagePlus image, RegionFeatures data, double strokeWidth)
    {
        // retrieve the result of computation
        Polygon2D[] polygons = (Polygon2D[]) data.results.get(this.getClass());
                
        // create overlay
        Overlay overlay = new Overlay();
        
        // add each box to the overlay
        for (int i = 0; i < polygons.length; i++) 
        {
            Roi roi = convertToRoi(polygons[i]);
            
            // add ROI to overlay
            Color color = data.labelColors[i];
            OverlayFeature.addRoiToOverlay(overlay, roi, color, strokeWidth);
        }
        
        image.setOverlay(overlay);
    }
    
    @Override
    public Roi[] computeRois(RegionFeatures data)
    {
        // retrieve array of ellipses
        Object obj = data.results.get(this.getClass());
        if (!(obj instanceof Polygon2D[]))
        {
            throw new RuntimeException("Requires object argument to be an array of Polygon2D");
        }
        
        // convert each polygon into a ROI
        return Stream.of((Polygon2D[]) obj)
                .map(poly -> convertToRoi(poly))
                .toArray(Roi[]::new);
    }

    private Roi convertToRoi(Polygon2D poly)
    {
        int nv = poly.vertexNumber();
        float[] xdata = new float[nv];
        float[] ydata = new float[nv];
        for (int i = 0; i < nv; i++)
        {
            Point2D p = poly.getVertex(i);
            xdata[i] = (float) p.getX();
            ydata[i] = (float) p.getY();
        }
        
        return new PolygonRoi(xdata, ydata, nv, Roi.POLYGON);
    }
}
