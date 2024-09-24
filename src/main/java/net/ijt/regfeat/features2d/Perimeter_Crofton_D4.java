/**
 * 
 */
package net.ijt.regfeat.features2d;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import inra.ijpb.measure.region2d.IntrinsicVolumes2D;

/**
 * Computation of perimeter using discretization of Crofton formula with four
 * directions.
 */
public class Perimeter_Crofton_D4 extends Feature
{
    public static final Perimeter_Crofton_D4 instance()
    {
        if (instance == null)
        {
            instance = new Perimeter_Crofton_D4();
            FeatureManager.getInstance().addFeature(instance);
        }
        return instance;
    }
    
    private static Perimeter_Crofton_D4 instance = null;
    
    
    public Perimeter_Crofton_D4()
    {
        super("perimeter_crofton_d4");
    }
    
//    @Override
//    public void updateData(RegionAnalyisData results)
//    {
//        System.out.println("start computing crofton perimeter");
//        if (results.isComputed(getId())) return;
//        System.out.println("  compute it");
//        
//        int[] labels = results.labels;
//            
//        IntrinsicVolumesAnalyzer2D algo = new IntrinsicVolumesAnalyzer2D();
//        IntrinsicVolumesAnalyzer2D.Result[] res = algo.analyzeRegions(results.labelMap.getProcessor(), labels, results.labelMap.getCalibration());
//        
//        for (int i = 0; i < labels.length; i++)
//        {
//            results.regionData.get(labels[i]).data.put(getId(), res[i].perimeter);
//        }
//        
//        results.setAsComputed(getId());
//    }
    
    @Override
    public Object[] compute(int[] labels, RegionAnalyisData results)
    {
        ImagePlus labelMap = results.labelMap;
            
        IntrinsicVolumes2D algo = new IntrinsicVolumes2D();
        IntrinsicVolumes2D.Result[] res = algo.analyzeRegions(labelMap.getProcessor(), labels, labelMap.getCalibration());
        
        Double[] perims = new Double[labels.length];
        for (int i = 0; i < labels.length; i++)
        {
            perims[i] = res[i].perimeter;
        }
        return perims;
    }
    
    @Override
    public void populateTable(ResultsTable table, int row, Object value)
    {
        table.setValue("Perimeter_Crofton_D4", row, (double) value);
    }
}
