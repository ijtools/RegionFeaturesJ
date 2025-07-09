/**
 * 
 */
package net.ijt.regfeat.morpho2d.core;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

import ij.process.ImageProcessor;
import inra.ijpb.algo.AlgoStub;
import inra.ijpb.geometry.Polygon2D;
import inra.ijpb.geometry.Polygons2D;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;

/**
 * Computes the convex hull of each region in pixel coordinates.
 */
public class ConvexHull extends AlgoStub implements Feature
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
        ImageProcessor image = data.labelMap.getProcessor();
        int[] labels = data.labels;
        
        // for each region, extract the points at the middle of the boundary edges
        ArrayList<Point2D>[] pointArrays = boundaryPixelsMiddleEdges(image, labels);

        // compute convex hull of boundary points around each region
        Polygon2D[] hulls = new Polygon2D[labels.length];
        for (int i = 0; i < labels.length; i++)
        {
            // compute convex hull of boundary points around the binary particle
            hulls[i] = Polygons2D.convexHull(pointArrays[i]);
        }
        
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
    private ArrayList<Point2D>[] boundaryPixelsMiddleEdges(ImageProcessor labelImage, int[] labels)
    {
        // size of image
        int sizeX = labelImage.getWidth();
        int sizeY = labelImage.getHeight();
        
        int nLabels = labels.length;
        HashMap<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(labels);
        
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
                    if (label != 0)
                    {
                        int index = labelIndices.get(label);
                        pointArrays[index].add(p);
                    }
                    if (labelUp != 0)
                    {
                        int index = labelIndices.get(labelUp);
                        pointArrays[index].add(p);
                    }
                }
                
                // check boundary with left pixel
                if (labelLeft != label)
                {
                    Point2D p = new Point2D.Double(x, y + 0.5);
                    if (label != 0)
                    {
                        int index = labelIndices.get(label);
                        pointArrays[index].add(p);
                    }
                    if (labelLeft != 0)
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
}
