/**
 * 
 */
package net.ijt.regfeat.spatial;

import java.util.Set;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import inra.ijpb.label.RegionAdjacencyGraph.LabelPair;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.morpho2d.Centroid;

/**
 * Computes the region adjacency graph of a set of regions, that contains an
 * edge for edge adjacent region.
 * 
 * This class is mostly a wrapper for the methods of the RegionAdjacencyGraph
 * class in the MorphoLibJ library.
 */
public class RegionAdjacencyGraph extends Feature
{
    public RegionAdjacencyGraph()
    {
        // TODO: should be able to manage 2D or 3D centroids in the same way
        this.requiredFeatures.add(Centroid.class);
    }

    @Override
    public Set<LabelPair> compute(RegionFeatures data)
    {
        ImagePlus labelMap = data.labelMap;
        return inra.ijpb.label.RegionAdjacencyGraph.computeAdjacencies(labelMap);
    }

    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        // nothing to do...
    }

}