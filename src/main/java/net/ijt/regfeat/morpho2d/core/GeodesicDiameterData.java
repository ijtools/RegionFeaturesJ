/**
 * 
 */
package net.ijt.regfeat.morpho2d.core;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collection;

import ij.IJ;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import inra.ijpb.algo.AlgoStub;
import inra.ijpb.binary.distmap.ChamferMask2D;
import inra.ijpb.binary.geodesic.GeodesicDistanceTransformFloat;
import inra.ijpb.label.LabelValues;
import inra.ijpb.label.LabelValues.PositionValuePair;
import net.ijt.regfeat.Feature;
import net.ijt.regfeat.RegionFeature;
import net.ijt.regfeat.RegionFeatures;
import net.ijt.regfeat.morpho2d.LargestInscribedDisk;

/**
 * Data to compute geodesic diameter. Stores the results in pixel coordinates.
 */
public class GeodesicDiameterData extends AlgoStub implements RegionFeature
{
    @Override
    public Result[] compute(RegionFeatures data)
    {
        // retrieve label map and list of labels
        ImageProcessor labelMap = data.labelMap.getProcessor();
        int[] labels = data.labels;
        Calibration calib = data.labelMap.getCalibration();
        
        // Initial check-up
        if (calib.pixelWidth != calib.pixelHeight)
        {
            throw new RuntimeException("Requires image with square pixels");
        }

        // number of labels to process
        int nLabels = labels.length;

        // retrieve required features
        data.ensureRequiredFeaturesAreComputed(this);
        Point[] maximaPositions = (Point[]) data.results.get(DistanceMapMaximaPosition.class);

        // Create new marker image
        this.fireStatusChanged(this, "Initializing marker image");
        int sizeX = labelMap.getWidth();
        int sizeY = labelMap.getHeight();
        ImageProcessor marker = new ByteProcessor(sizeX, sizeY);
        
        // initialize marker image with position of maxima
        marker.setValue(0);
        marker.fill();
        for (int i = 0; i < nLabels; i++) 
        {
            Point center = maximaPositions[i];
            if (center.x == -1)
            {
                IJ.showMessage("Particle Not Found", 
                        "Could not find maximum for region with label " + labels[i]);
                continue;
            }
            marker.set(center.x, center.y, 255);
        }
    
        this.fireStatusChanged(this, "Computing first geodesic extremities...");
    
        // First geodesic distance propagation from region centers
        GeodesicDistanceTransformFloat geodesicDistanceTransform = new GeodesicDistanceTransformFloat(ChamferMask2D.CHESSKNIGHT, true);
        ImageProcessor distanceMap = geodesicDistanceTransform.geodesicDistanceMap(marker, labelMap);
        
        // find position of maximal value for each label
        // this is expected to correspond to a geodesic extremity 
        Point[] firstGeodesicExtremities = LabelValues.findPositionOfMaxValues(distanceMap, labelMap, labels);
        
        // Create new marker image with position of maxima
        marker.setValue(0);
        marker.fill();
        for (int i = 0; i < nLabels; i++)
        {
            if (firstGeodesicExtremities[i].x == -1) 
            {
                // error message was already displayed, no need to display twice...
                continue;
            }
            marker.set(firstGeodesicExtremities[i].x, firstGeodesicExtremities[i].y, 255);
        }
        
        this.fireStatusChanged(this, "Computing second geodesic extremities...");
    
        // second geodesic distance propagation from first extremity
        distanceMap = geodesicDistanceTransform.geodesicDistanceMap(marker, labelMap);
        
        // also computes position of maxima
        PositionValuePair[] secondGeodesicExtremities = LabelValues.findMaxValues(distanceMap, labelMap, labels);
        
        // Create array of results and populate with computed values
        Result[] result = new Result[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
            Result res = new Result();
                    
            // Get the maximum distance within each label, 
            // and add 1.0 to take into account pixel (side) thickness
            res.diameter = secondGeodesicExtremities[i].getValue() + 1.0;

            // also keep references to characteristic points
            res.initialPoint = new Point(maximaPositions[i].x, maximaPositions[i].y); 
            res.firstExtremity = firstGeodesicExtremities[i];
            res.secondExtremity = secondGeodesicExtremities[i].getPosition();
            
            // store the result
            result[i] = res;
        }
        
//        // calibrate the results
//        if (calib.scaled())
//        {
//            this.fireStatusChanged(this, "Re-calibrating results");
//            for (int i = 0; i < nLabels; i++)
//            {
//                result[i] = result[i].recalibrate(calib);
//            }
//        }
//        
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
                table.setValue("GeodesicDiameter", r, res.diameter);

                // coordinates of initial point
                table.setValue("InitPoint_X", r, res.initialPoint.getX());
                table.setValue("InitPoint_Y", r, res.initialPoint.getY());

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
        return Arrays.asList(LargestInscribedDisk.class);
    }
    
    // ==================================================
    // Inner class used for representing computation results
    
    /**
     * Inner class used for representing results of geodesic diameters
     * computations. Each instance corresponds to a single region / particle.
     * 
     * @author dlegland
     *
     */
    public class Result
    {
        /** The geodesic diameter of the region */
        public double diameter;

        /**
         * The initial point used for propagating distances, corresponding the
         * center of one of the minimum inscribed circles.
         */
        public Point2D initialPoint;

        /**
         * The radius of the largest inner circle. Value may depends on the chamfer weihgts.
         */
        public double innerRadius;

        /**
         * The first geodesic extremity found by the algorithm.
         */
        public Point2D firstExtremity;

        /**
         * The second geodesic extremity found by the algorithm.
         */
        public Point2D secondExtremity;

        /**
         * Computes the result corresponding to the spatial calibration. The
         * current result instance is not modified.
         * 
         * @param calib
         *            the spatial calibration of an image
         * @return the result after applying the spatial calibration
         */
        public Result recalibrate(Calibration calib)
        {
            double size = calib.pixelWidth;
            Result res = new Result();
            
            // calibrate the diameter
            res.diameter = this.diameter * size;

            // calibrate inscribed disk
            res.initialPoint = calibrate(this.initialPoint, calib); 
            res.innerRadius = this.innerRadius * size;

            // calibrate geodesic extremities
            res.firstExtremity = calibrate(this.firstExtremity, calib); 
            res.secondExtremity = calibrate(this.secondExtremity, calib);
            
            // return the calibrated result
            return res;
        }
        
        private Point2D calibrate(Point2D point, Calibration calib)
        {
            return new Point2D.Double(
                    point.getX() * calib.pixelWidth + calib.xOrigin, 
                    point.getY() * calib.pixelHeight + calib.yOrigin);
        }
    }
}
