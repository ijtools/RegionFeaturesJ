/**
 * 
 */
package net.ijt.regfeat;

import ij.measure.ResultsTable;

/**
 * An utility class used to simplify the implementation of features that compute
 * a single scalar value for each region.
 * 
 * As the result is returned as a single value for each region, the result of
 * the <code>compute()</code> method can be returned as an array of double.
 * Furthermore, the <code>updateTable()</code> method can be implemented, by
 * populating a column identified with the name of the feature. The name of the
 * feature is provided within the constructor.
 */
public abstract class SingleValueFeature implements RegionFeature
{
    /**
     * The name of the feature, used to populate the result table.
     */
    protected String name;
    
    /**
     * Specifies the name of the feature.
     * 
     * @param name
     *            the name of the feature.
     */
    public SingleValueFeature(String name)
    {
        this.name = name;
    }
    
    protected void setName(String newName)
    {
        this.name = newName;
    }
    
    /**
     * Overrides the default definition to enforce the result to be an array of
     * double.
     * 
     * @param data
     *            the data computed for each region
     * @return a summary feature for each region
     */
    @Override
    public abstract double[] compute(RegionFeatures data);

    @Override
    public void updateTable(ResultsTable table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof double[])
        {
            double[] array = (double[]) obj;
            for (int r = 0; r < array.length; r++)
            {
                table.setValue(this.name, r, array[r]);
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of double");
        }
    }

}
