/**
 * 
 */
package net.ijt.regfeat.features2d;

import ij.measure.ResultsTable;

/**
 * Computes the perimeter of a 2D region. In practice, this feature is an alias for the Perimeter_Crofton_D4 feature.
 */
public class Perimeter extends Feature
{
    public static final Perimeter instance()
    {
        if (instance == null)
        {
            instance = new Perimeter();
            FeatureManager.getInstance().addFeature(instance);
        }
        return instance;
    }
    
    private static Perimeter instance = null;
    
    
    public Perimeter()
    {
        super("perimeter");
        this.requiredFeatures.add(Perimeter_Crofton_D4.instance());
    }
    
//    @Override
//    public void updateData(RegionAnalyisData results)
//    {
//        System.out.println("start computing perimeter");
//        if (results.isComputed(getId())) return;
//        System.out.println("  compute it");
//        
//        Perimeter_Crofton_D4.instance().updateData(results);
//        
//        ImagePlus labelMap = results.labelMap;
//        int[] labels = results.labels;
//            
//        IntrinsicVolumesAnalyzer2D algo = new IntrinsicVolumesAnalyzer2D();
//        IntrinsicVolumesAnalyzer2D.Result[] res = algo.analyzeRegions(labelMap.getProcessor(), labels, labelMap.getCalibration());
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
        // check required features have been computed
        String id = Perimeter_Crofton_D4.instance().getId();
        if (!results.isComputed(id))
        {
            throw new RuntimeException("Requires Perimeter_Crofton_D4 to have been computed");
        }
        
        // convert into an array of double
        Double[] res = new Double[labels.length];
        for (int i = 0; i < labels.length; i++)
        {
            res[i] = (double) results.regionData.get(labels[i]).getFeature(id);
        }
        return res;
    }
    
    @Override
    public void populateTable(ResultsTable table, int row, Object obj)
    {
        table.setValue("Perimeter", row, (double) obj);
    }
}
