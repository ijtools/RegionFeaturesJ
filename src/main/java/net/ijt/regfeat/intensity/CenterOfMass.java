/**
 * 
 */
package net.ijt.regfeat.intensity;

import java.util.Map;

import ij.ImagePlus;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import ij.process.ImageProcessor;
import inra.ijpb.label.LabelImages;
import net.ijt.regfeat.RegionTabularFeature;
import net.ijt.regfeat.RegionFeatures;

/**
 * 
 */
public class CenterOfMass implements RegionTabularFeature
{
    @Override
    public double[][] compute(RegionFeatures data)
    {
        // retrieve necessary data
        ImagePlus intensityImage = data.getImageData("intensity");
        if (intensityImage == null)
        {
            throw new RuntimeException("Requires to populate the 'RegionFeatures' class with an 'intensity' image data");
        }
        Calibration calib = data.labelMap.getCalibration();
        
        // size of images
        final int width = data.labelMap.getWidth();
        final int height = data.labelMap.getHeight();
        final int nSlices = data.labelMap.getImageStackSize();
        int nLabels = data.labels.length;

        // create associative hash table to know the index of each label
        Map<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(data.labels);
        
        // initialize values
        double[] sumWX = new double[nLabels]; 
        double[] sumWY = new double[nLabels]; 
        double[] sumWZ = new double[nLabels]; 
        double[] sumWeights = new double[nLabels]; 

        // iterate over image elements to compute coordinates of center of mass
        for (int z = 1; z <= nSlices; z++)
        {
            ImageProcessor intensitySlice = intensityImage.getImageStack().getProcessor(z);
            ImageProcessor labelsMapSlice = data.labelMap.getImageStack().getProcessor(z);

            for (int x = 0; x < width; x++)
            {
                for (int y = 0; y < height; y++)
                {
                    int label = (int) labelsMapSlice.getf(x, y);
                    if (label == 0) continue;

                    int index = labelIndices.get(label);
                    double weight = intensitySlice.getf(x, y);

                    sumWX[index] += x * weight;
                    sumWY[index] += y * weight;
                    sumWZ[index] += (z - 1) * weight;
                    sumWeights[index] += weight;
                }
            }
        }
        
        // create result array
        int nd = nSlices > 1 ? 3 : 2;
        double[][] res = new double[nLabels][nd];
        for (int i = 0; i < nLabels; i++)
        {
            res[i][0] = (sumWX[i] / sumWeights[i]) * calib.pixelWidth + calib.xOrigin;
            res[i][1] = (sumWY[i] / sumWeights[i]) * calib.pixelHeight + calib.yOrigin;
            if (nd > 2)
            {
                res[i][2] = (sumWZ[i] / sumWeights[i]) * calib.pixelDepth + calib.zOrigin;
            }
        }
        return res;
    }
    
    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        int nd = data.labelMap.getImageStackSize() > 2 ? 3 : 2;
        String[] colNames = nd == 2 ? new String[] { "Center_Of_Mass_X", "Center_Of_Mass_Y" } : new String[] { "Center_Of_Mass_X", "Center_Of_Mass_Y" , "Center_Of_Mass_Z" };
        if (data.displayUnitsInTable)
        {
            // update the name to take into account the unit
            Calibration calib = data.labelMap.getCalibration();
            String suffix = String.format("_[%s]", calib.getUnit());
            for (int i = 0; i < nd; i++)
            {
                colNames[i] = colNames[i] + suffix;
            }
        }
        
        Object obj = data.results.get(this.getClass());
        if (obj instanceof double[][])
        {
            double[][] array = (double[][]) obj;
            for (int r = 0; r < array.length; r++)
            {
                double[] coords = array[r];
                table.setValue(colNames[0], r, coords[0]);
                table.setValue(colNames[1], r, coords[1]);
                if (nd > 2)
                {
                    table.setValue(colNames[2], r, coords[2]);
                }
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be a 2D array of double");
        }
    }
}
