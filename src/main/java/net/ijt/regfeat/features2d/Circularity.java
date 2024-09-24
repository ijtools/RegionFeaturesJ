/**
 * 
 */
package net.ijt.regfeat.features2d;

import ij.measure.ResultsTable;

/**
 * The circularity of a region, defined from normalized ratio of area and
 * squared perimeter.
 */
public class Circularity extends Feature
{
    public static final Circularity instance()
    {
        if (instance == null)
        {
            instance = new Circularity();
            FeatureManager.getInstance().addFeature(instance);
        }
        return instance;
    }
    private static Circularity instance = null;

    public Circularity()
    {
        super("circularity");
        this.requiredFeatures.add(Area.instance());
        this.requiredFeatures.add(Perimeter.instance());
    }
    
//    @Override
//    public void updateData(RegionAnalyisData results)
//    {
//        System.out.println("start computing circularity");
//        if (results.isComputed(getId())) return;
//        System.out.println("  compute it");
//
//        Area area = Area.instance();
//        Perimeter perim = Perimeter.instance();
//        area.updateData(results);
//        perim.updateData(results);
//        
//        int[] labels = results.labels;
//        for (int i = 0; i < labels.length; i++)
//        {
//            RegionData features = results.regionData.get(labels[i]);
//            
//            double a = (double) features.getFeature(area.getId());
//            double p = (double) features.getFeature(perim.getId());
//            
//            double circ = 4 * Math.PI * a / (p * p);
//            features.setFeature(getId(), circ);
//        }
//        
//        results.setAsComputed(getId());
//    }
    
    @Override
    public Object[] compute(int[] labels, RegionAnalyisData results)
    {
        Double[] res = new Double[labels.length];
        for (int i = 0; i < labels.length; i++)
        {
            RegionData region = results.regionData.get(labels[i]);
            double a = (double) region.getFeature(Area.instance().getId());
            double p = (double) region.getFeature(Perimeter.instance().getId());
            double circ = 4 * Math.PI * a / (p * p);
            res[i] = circ;
        }
        return res;
    }
    
    @Override
    public void populateTable(ResultsTable table, int row, Object value)
    {
        table.setValue("Circ.", row, (Double) value);
    }
}
