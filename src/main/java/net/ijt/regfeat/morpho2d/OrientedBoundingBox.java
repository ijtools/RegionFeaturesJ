/**
 * 
 */
package net.ijt.regfeat.morpho2d;

import java.awt.geom.Point2D;
import java.util.Arrays;

import ij.measure.Calibration;
import ij.measure.ResultsTable;
import inra.ijpb.geometry.OrientedBox2D;
import inra.ijpb.geometry.Polygon2D;
import inra.ijpb.measure.region2d.OrientedBoundingBox2D;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.morpho2d.core.ConvexHull;

/**
 * 
 */
public class OrientedBoundingBox extends Feature
{
    public OrientedBoundingBox()
    {
        this.requiredFeatures.add(ConvexHull.class);
    }
    

    @Override
    public OrientedBox2D[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        ensureRequiredFeaturesAreComputed(data);
        Polygon2D[] hulls = (Polygon2D[]) data.results.get(ConvexHull.class);
        
        // retrieve spatial calibration of image
        Calibration calib = data.labelMap.getCalibration();

        // Compute the oriented box of each set of corner points
        return Arrays.stream(hulls)
                .map(hull -> OrientedBoundingBox2D.orientedBoundingBox(hull.vertices(), calib))
                .toArray(OrientedBox2D[]::new);
    }

    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof OrientedBox2D[])
        {
            OrientedBox2D[] array = (OrientedBox2D[]) obj;
            for (int r = 0; r < array.length; r++)
            {
                OrientedBox2D obox = array[r];
                Point2D center = obox.center();
                table.setValue("Box.Center.X",  r, center.getX());
                table.setValue("Box.Center.Y",  r, center.getY());
                table.setValue("Box.Length",    r, obox.length());
                table.setValue("Box.Width",     r, obox.width());
                table.setValue("Box.Orientation", r, obox.orientation());
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of double");
        }
    }

}
