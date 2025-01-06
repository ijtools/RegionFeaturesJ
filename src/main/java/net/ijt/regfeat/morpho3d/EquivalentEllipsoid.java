/**
 * 
 */
package net.ijt.regfeat.morpho3d;

import static inra.ijpb.measure.region3d.EquivalentEllipsoid.equivalentEllipsoids;

import ij.ImageStack;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import inra.ijpb.geometry.Ellipsoid;
import inra.ijpb.geometry.Point3D;
import net.ijt.regfeat.RegionFeature;
import net.ijt.regfeat.RegionFeatures;

/**
 * Compute equivalent ellipsoid of 3D regions stored within label map.
 * 
 * The equivalent ellipsoid of a region is computed such that is has same second
 * order moments as the region. The code is mostly a wrapper for the class
 * <code>EquivalentEllipsoid</code> within MorphoLibJ.
 * 
 * @see inra.ijpb.measure.region2d.EquivalentEllipse
 */
public class EquivalentEllipsoid implements RegionFeature
{
    @Override
    public Ellipsoid[] compute(RegionFeatures data)
    {
        // retrieve label map and list of labels
        ImageStack labelMap = data.labelMap.getStack();
        int[] labels = data.labels;
        Calibration calib = data.labelMap.getCalibration();
        
        return equivalentEllipsoids(labelMap, labels, calib);
    }

    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof Ellipsoid[])
        {
            Ellipsoid[] array = (Ellipsoid[]) obj;
            for (int r = 0; r < array.length; r++)
            {
                // current ellipsoid
                Ellipsoid ellipsoid = array[r];
                
                // coordinates of centroid
                Point3D center = ellipsoid.center();
                table.setValue("Ellipsoid_Center_X", r, center.getX());
                table.setValue("Ellipsoid_Center_Y", r, center.getY());
                table.setValue("Ellipsoid_Center_Z", r, center.getZ());
                
                // ellipse size
                table.setValue("Ellipsoid_Radius_1", r, ellipsoid.radius1());
                table.setValue("Ellipsoid_Radius_2", r, ellipsoid.radius2());
                table.setValue("Ellipsoid_Radius_3", r, ellipsoid.radius3());

                // ellipse orientation (in degrees)
                table.setValue("Ellipsoid_Phi", r, ellipsoid.phi());
                table.setValue("Ellipsoid_Theta", r, ellipsoid.theta());
                table.setValue("Ellipsoid_Psi", r, ellipsoid.psi());
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of Ellipsoid");
        }
    }
}
