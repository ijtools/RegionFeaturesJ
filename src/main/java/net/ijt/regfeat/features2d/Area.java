/**
 * 
 */
package net.ijt.regfeat.features2d;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import inra.ijpb.measure.region2d.IntrinsicVolumes2D;

/**
 * A feature that computes the area of 2D regions.
 */
public class Area extends Feature
{
    public static final Area instance()
    {
        if (instance == null)
        {
            instance = new Area();
            FeatureManager.getInstance().addFeature(instance);
        }
        return instance;
    }
    private static Area instance = null;
    
    public Area()
    {
    }

    @Override
    public void populateTable(ResultsTable table, int row, Object value)
    {
        table.setValue("Area", row, (double) value);
    }

    @Override
    public Object[] compute(int[] labels, RegionAnalyisData results)
    {
        ImagePlus labelMap = results.labelMap;
            
        IntrinsicVolumes2D algo = new IntrinsicVolumes2D();
        IntrinsicVolumes2D.Result[] res = algo.analyzeRegions(labelMap.getProcessor(), labels, labelMap.getCalibration());
        
        Double[] areas = new Double[labels.length];
        for (int i = 0; i < labels.length; i++)
        {
            areas[i] = res[i].area;
        }
        return areas;
    }
}
