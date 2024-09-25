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
        this.requiredFeatures.add(Area.class);
        this.requiredFeatures.add(Perimeter.class);
    }
    
    @Override
    public Object[] compute(int[] labels, RegionAnalyisData results)
    {
        Double[] res = new Double[labels.length];
        for (int i = 0; i < labels.length; i++)
        {
            RegionData region = results.regionData.get(labels[i]);
            double a = (double) region.getFeature(Area.class);
            double p = (double) region.getFeature(Perimeter.class);
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
