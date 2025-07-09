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
import net.ijt.regfeat.RegionTabularFeature;
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
public class EquivalentEllipsoid implements RegionTabularFeature
{
    /**
     * The names of the columns of the resulting table.
     */
    public static final String[] colNames = new String[] {
            "Ellipsoid_Center_X", "Ellipsoid_Center_Y", "Ellipsoid_Center_Z", 
            "Ellipsoid_Radius_1", "Ellipsoid_Radius_2", "Ellipsoid_Radius_3",
            "Ellipsoid_Phi", "Ellipsoid_Theta", "Ellipsoid_Psi"};
    
    /**
     * Default empty constructor.
     */
    public EquivalentEllipsoid()
    {
    }
    
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
                table.setValue(colNames[0], r, center.getX());
                table.setValue(colNames[1], r, center.getY());
                table.setValue(colNames[2], r, center.getZ());
                
                // ellipse size
                table.setValue(colNames[3], r, ellipsoid.radius1());
                table.setValue(colNames[4], r, ellipsoid.radius2());
                table.setValue(colNames[5], r, ellipsoid.radius3());

                // ellipse orientation (in degrees)
                table.setValue(colNames[6], r, ellipsoid.phi());
                table.setValue(colNames[7], r, ellipsoid.theta());
                table.setValue(colNames[8], r, ellipsoid.psi());
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of Ellipsoid instances");
        }
    }
    
    @Override
    public String[] columnUnitNames(RegionFeatures data)
    {
        String[] unitNames = new String[colNames.length];
        
        // setup table info
        Calibration calib = data.labelMap.getCalibration();
        String unitName = calib.getUnit();
        for (int c = 0; c < 6; c++)
        {
            unitNames[c] = unitName;
        }
        for (int c = 6; c < 9; c++)
        {
            unitNames[c] = "degree";
        }
        
        return unitNames;
    }
}
