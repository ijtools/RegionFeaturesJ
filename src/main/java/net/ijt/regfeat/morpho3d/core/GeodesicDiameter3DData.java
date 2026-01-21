package net.ijt.regfeat.morpho3d.core;

import java.util.Arrays;
import java.util.Collection;

import ij.IJ;
import ij.ImageStack;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import inra.ijpb.algo.AlgoEvent;
import inra.ijpb.algo.AlgoListener;
import inra.ijpb.algo.AlgoStub;
import inra.ijpb.binary.distmap.ChamferMask3D;
import inra.ijpb.binary.geodesic.GeodesicDistanceTransform3D;
import inra.ijpb.binary.geodesic.GeodesicDistanceTransform3DFloat;
import inra.ijpb.data.Cursor3D;
import inra.ijpb.data.image.Images3D;
import inra.ijpb.label.LabelValues;
import inra.ijpb.label.LabelValues.Position3DValuePair;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.RegionTabularFeature;

/**
 * Computes the data necessary for computing the 3D geodesic diameter. Results
 * are stored in pixel coordinates.
 */
public class GeodesicDiameter3DData extends AlgoStub implements AlgoListener, RegionTabularFeature
{
    /**
     * The chamfer mask used to propagate distances to neighbor pixels.
     */
    ChamferMask3D chamferMask;
    
    /**
     * The string used for indicating the current step in algo events.
     */
    String currentStep = "";
    

    /**
     * Default empty constructor.
     */
    public GeodesicDiameter3DData()
    {
        chamferMask = ChamferMask3D.SVENSSON_3_4_5_7;
    }
    
    @Override
    public Object compute(RegionFeatures data)
    {
        // retrieve label map and list of labels
        ImageStack labelMap = data.labelMap.getStack();
        Calibration calib = data.labelMap.getCalibration();
        
        // Initial check-up
        if (calib.pixelWidth != calib.pixelHeight || calib.pixelWidth != calib.pixelDepth)
        {
            throw new RuntimeException("Requires image with cubic voxels");
        }

        // number of labels to process
        int[] labels = data.labels;
        int nLabels = labels.length;

        // retrieve image size
        int sizeX = labelMap.getWidth();
        int sizeY = labelMap.getHeight();
        int sizeZ = labelMap.size();
        
        
        // retrieve required features
        this.fireStatusChanged(this, "Retrieving pseudo geodesic centers...");
        data.ensureRequiredFeaturesAreComputed(this);
        Cursor3D[] maximaPositions = (Cursor3D[]) data.results.get(DistanceMap3DMaximaPosition.class);

        // Create calculator for computing geodesic distances within label map
        GeodesicDistanceTransform3D gdt;
        gdt = new GeodesicDistanceTransform3DFloat(this.chamferMask, false);
        gdt.addAlgoListener(this);

        // initialize marker image with position of maxima
        this.fireStatusChanged(this, "Initialize first marker image...");
        ImageStack marker = ImageStack.create(sizeX, sizeY, sizeZ, 8);
        Images3D.fill(marker, 0);
        for (int i = 0; i < nLabels; i++) 
        {
            Cursor3D center = maximaPositions[i];
            if (center.getX() == -1)
            {
                IJ.showMessage("Region Not Found", 
                        "Could not find maximum for region with label " + labels[i]);
                continue;
            }
            marker.setVoxel(center.getX(), center.getY(), center.getZ(), 255);
        }


        // Distance propagation from initial markers
        this.fireStatusChanged(this, "Computing first geodesic extremities...");
        ImageStack distanceMap = gdt.geodesicDistanceMap(marker, labelMap);

        // find position of maximum value for each label
        // this is expected to correspond to a geodesic extremity 
        Cursor3D[] firstGeodesicExtremities = LabelValues.findPositionOfMaxValues(distanceMap, labelMap, labels);

        // Create new marker image with position of maxima
        this.fireStatusChanged(this, "Initialize second marker image...");
        Images3D.fill(marker, 0);
        for (int i = 0; i < nLabels; i++)
        {
            Cursor3D pos = firstGeodesicExtremities[i];
            if (pos.getX() == -1) 
            {
                continue;
            }
            marker.setVoxel(pos.getX(), pos.getY(), pos.getZ(), 255);
        }

        this.fireStatusChanged(this, "Computing second geodesic extremities...");

        // third distance propagation from second maximum
        distanceMap = gdt.geodesicDistanceMap(marker, labelMap);

        // also computes position of maxima
        Position3DValuePair[] secondGeodesicExtremities = LabelValues.findMaxValues(distanceMap, labelMap, labels);

        // Create array of results and populate with computed values
        Result[] result = new Result[nLabels];
        double w0 = chamferMask.getNormalizationWeight();
        for (int i = 0; i < nLabels; i++)
        {
            // Get the maximum distance within each label
            // normalized by first weight of chamfer mask, 
            // and adding 1.0 to take into account voxel thickness
            double diameter = secondGeodesicExtremities[i].getValue() / w0 + 1.0;

            // store the result
            result[i] = new Result(
                    diameter, 
                    maximaPositions[i],
                    firstGeodesicExtremities[i],
                    secondGeodesicExtremities[i].getPosition()
                    );
        }

        //      // calibrate the results
        //      if (calib.scaled())
        //      {
        //          this.fireStatusChanged(this, "Re-calibrating results");
        //          for (int i = 0; i < nLabels; i++)
        //          {
        //              result[i] = result[i].recalibrate(calib);
        //          }
        //      }

        // returns the results
        return result;
    }


    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof Result[])
        {
            Result[] array = (Result[]) obj;
            for (int r = 0; r < array.length; r++)
            {
                // current diameter
                Result res = array[r];

                // add an entry to the resulting data table
                table.setValue("GeodesicDiameter3D", r, res.diameter);

                // coordinates of initial point
                table.setValue("InitPoint_X", r, res.initialPoint.getX());
                table.setValue("InitPoint_Y", r, res.initialPoint.getY());
                table.setValue("InitPoint_Z", r, res.initialPoint.getZ());

                // coordinate of first and second geodesic extremities 
                table.setValue("Extremity1_X", r, res.firstExtremity.getX());
                table.setValue("Extremity1_Y", r, res.firstExtremity.getY());
                table.setValue("Extremity2_X", r, res.secondExtremity.getX());
                table.setValue("Extremity2_Y", r, res.secondExtremity.getY());
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of GeodesicDiameter.Result");
        }
    }

    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(DistanceMap3DMaximaPosition.class);
    }
    
    
    // ==================================================
    // Implementation of AlgoListener interface 

    @Override
    public void algoProgressChanged(AlgoEvent evt) 
    {
        fireProgressChanged(new Event(this, evt));
    }

    @Override
    public void algoStatusChanged(AlgoEvent evt) 
    {
        evt = new Event(this, evt);
        fireStatusChanged(evt);
    }
    
    /**
     * Encapsulation class to add a semantic layer on the interpretation of the event.
     */
    class Event extends AlgoEvent
    {
        public Event(GeodesicDiameter3DData source, AlgoEvent evt)
        {
            super(source, "(GeodDiam3d) " + evt.getStatus(), evt.getCurrentProgress(), evt.getTotalProgress());
            if (!currentStep.isEmpty())
            {
                this.status = "(GeodDiam3d-" + currentStep + ") " + evt.getStatus();
            }
        }
    }
    

    // ==================================================
    // Inner class used for representing computation results
    
    /**
     * Inner class used for representing results of computation of 3D geodesic
     * diameters. Each instance corresponds to a single region.
     */
    public class Result
    {
        /**
         * Initialization constructor.
         * 
         * @param diameter
         *            the geodesic diameter of the region
         * @param initialPoint
         *            the coordinates of the voxel used to initialize
         *            propagation
         * @param firstExtremity
         *            the coordinates of the voxel identified as first geodesic
         *            extremity
         * @param secondExtremity
         *            the coordinates of the voxel identified as second geodesic
         *            extremity
         */
        public Result(double diameter, Cursor3D initialPoint, Cursor3D firstExtremity, Cursor3D secondExtremity)
        {
            this.diameter = diameter;
            this.initialPoint = initialPoint;
            this.firstExtremity = firstExtremity;
            this.secondExtremity = secondExtremity;
        }
        
        /** The geodesic diameter of the region */
        public double diameter;

        /**
         * The initial point used for propagating distances, corresponding the
         * center of one of the minimum inscribed circles.
         */
        public Cursor3D initialPoint;

        /**
         * The first geodesic extremity found by the algorithm.
         */
        public Cursor3D firstExtremity;

        /**
         * The second geodesic extremity found by the algorithm.
         */
        public Cursor3D secondExtremity;
    }
}
