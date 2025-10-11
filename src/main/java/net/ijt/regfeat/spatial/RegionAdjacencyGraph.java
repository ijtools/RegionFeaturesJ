/**
 * 
 */
package net.ijt.regfeat.spatial;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import ij.ImagePlus;
import ij.gui.Line;
import ij.gui.Overlay;
import ij.gui.Roi;
import inra.ijpb.label.RegionAdjacencyGraph.LabelPair;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.OverlayFeature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.morpho2d.Centroid;

/**
 * Computes the region adjacency graph of a set of regions, that contains an
 * edge for edge adjacent region.
 * 
 * This class is mostly a wrapper for the methods of the RegionAdjacencyGraph
 * class in the MorphoLibJ library.
 */
public class RegionAdjacencyGraph implements Feature, OverlayFeature
{
    /**
     * Default empty constructor.
     */
    public RegionAdjacencyGraph()
    {
    }
    
    @Override
    public Set<LabelPair> compute(RegionFeatures data)
    {
        ImagePlus labelMap = data.labelMap;
        return inra.ijpb.label.RegionAdjacencyGraph.computeAdjacencies(labelMap);
    }

    @Override
    public void overlayResult(ImagePlus targetImage, RegionFeatures data, double strokeWidth)
    {
        // first retrieve centroids
        Object obj = data.results.get(Centroid.class);
        if (!(obj instanceof Point2D[]))
        {
            throw new RuntimeException("Requires centroid output to be an array of Point2D");
        }
        Point2D[] centroids = (Point2D[]) obj;
        
        Object obj2 = data.results.get(RegionAdjacencyGraph.class);
        if (!(obj2 instanceof Set))
        {
            throw new RuntimeException("Requires RAG output to be an array of Set");
        }
        @SuppressWarnings("unchecked")
        Set<LabelPair> adjList = (Set<LabelPair>) obj2;
        
        Map<Integer, Integer> labelMap = data.labelIndices;
        
        // create an overlay for drawing edges
        Overlay overlay = new Overlay();
        
        // iterate over adjacencies to add edges to overlay
        for (LabelPair pair : adjList)
        {
            // first retrieve index in centroid array
            int ind1 = labelMap.get(pair.label1);
            int ind2 = labelMap.get(pair.label2);
            
            // coordinates of edge extremities
            int x1 = (int) centroids[ind1].getX();
            int y1 = (int) centroids[ind1].getY();
            int x2 = (int) centroids[ind2].getX();
            int y2 = (int) centroids[ind2].getY();
            
            // draw current edge
            Roi roi = new Line(x1, y1, x2, y2);
            
            roi.setStrokeColor(Color.GREEN);
            roi.setStrokeWidth(strokeWidth);
            overlay.add(roi);
        }
        
        targetImage.setOverlay(overlay);
    }
    
    @Override
    public Collection<Class<? extends Feature>>requiredFeatures()
    {
        // TODO: should be able to manage 2D or 3D centroids in the same way
        return Arrays.asList(Centroid.class);
    }
}
