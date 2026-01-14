/**
 * 
 */
package net.ijt.regfeat.morpho3d;

import java.util.stream.Stream;

import ij.ImageStack;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import inra.ijpb.algo.AlgoStub;
import inra.ijpb.data.Cursor3D;
import inra.ijpb.geometry.Point3D;
import inra.ijpb.geometry.Sphere;
import inra.ijpb.label.LabelImages;
import inra.ijpb.label.LabelValues;
import net.ijt.regfeat.RegionTabularFeature;
import net.ijt.regfeat.RegionFeatures;

/**
 * Computes the largest inscribed ball within 3D regions of a label map.
 */
public class LargestInscribedBall extends AlgoStub implements RegionTabularFeature
{
    /**
     * The names of the columns of the resulting table.
     */
    public static final String[] colNames = new String[] {
            "Inscribed_Ball_Center_X",
            "Inscribed_Ball_Center_Y",
            "Inscribed_Ball_Center_Z",
            "Inscribed_Ball_Radius"};
    
    /**
     * Default empty constructor.
     */
    public LargestInscribedBall()
    {
    }
    
    @Override
    public Sphere[] compute(RegionFeatures data)
    {
        ImageStack labelMap = data.labelMap.getStack();
        Calibration calib = data.labelMap.getCalibration();
        
        // first distance propagation to find an arbitrary center
        fireStatusChanged(this, "Compute distance map");
        ImageStack distanceMap = LabelImages.distanceMap(labelMap);

        // Extract position of maxima
        fireStatusChanged(this, "Find inscribed balls center");
        Cursor3D[] posCenter;
        posCenter = LabelValues.findPositionOfMaxValues(distanceMap, labelMap, data.labels);
        double[] radii = getValues(distanceMap, posCenter);

        // Create result data table
        fireStatusChanged(this, "Create ball data");
        int nLabels = data.labels.length;
        Sphere[] balls = new Sphere[nLabels];
        for (int i = 0; i < nLabels; i++) 
        {
            double xc = posCenter[i].getX() * calib.pixelWidth + calib.xOrigin;
            double yc = posCenter[i].getY() * calib.pixelHeight + calib.yOrigin;
            double zc = posCenter[i].getZ() * calib.pixelDepth + calib.zOrigin;
            Point3D center = new Point3D(xc, yc, zc);
            balls[i] = new Sphere(center, radii[i] * calib.pixelWidth);
        }

        return balls;
    }
    
    /**
     * Get values in input image for each specified position.
     */
    private final static double[] getValues(ImageStack image, Cursor3D[] positions)
    {
        return Stream.of(positions)
                .mapToDouble(pos -> image.getVoxel(pos.getX(), pos.getY(), pos.getZ()))
                .toArray();
    }

    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof Sphere[])
        {
            Sphere[] array = (Sphere[]) obj;
            for (int r = 0; r < array.length; r++)
            {
                Sphere ball = array[r];
                // coordinates of ball center
                table.setValue(colNames[0], r, ball.center().getX());
                table.setValue(colNames[1], r, ball.center().getY());
                table.setValue(colNames[2], r, ball.center().getZ());
                
                // ball radius
                table.setValue(colNames[3], r, ball.radius());
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of Sphere");
        }
    }
    
    @Override
    public String[] columnUnitNames(RegionFeatures data)
    {
        String[] unitNames = new String[colNames.length];
        String unitName = data.labelMap.getCalibration().getUnit();
        for (int c = 0; c < colNames.length; c++)
        {
            unitNames[c] = unitName;
        }
        return unitNames;
    }
    
}
