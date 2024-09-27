/**
 * 
 */
package net.ijt.regfeat;

import java.util.ArrayList;
import java.util.Collection;

import ij.ImagePlus;
import ij.measure.ResultsTable;

/**
 * The main computation class.
 */
public class RegionFeatures
{
    public static final RegionFeatures initialize(ImagePlus imagePlus, int[] labels)
    {
        return new RegionFeatures(imagePlus, labels);
    }
    
    /**
     * The list of features that will be used to populate the data table.
     */
    Collection<Class<? extends Feature>> features = new ArrayList<>();
    
    RegionAnalyisData data;

    private RegionFeatures(ImagePlus imagePlus, int[] labels)
    {
        this.data = new RegionAnalyisData(imagePlus, labels);
    }
    
    public RegionFeatures add(Class<? extends Feature> featureClass)
    {
        this.features.add(featureClass);
        return this;
    }
    
    public RegionFeatures computeAll()
    {
        this.features.stream().forEach(this.data::updateWith);
        return this;
    }
    
    public ResultsTable createTable()
    {
        // Initialize labels
        int nLabels = data.labels.length;
        ResultsTable table = new ResultsTable();
        for (int i = 0; i < nLabels; i++)
        {
            table.incrementCounter();
            table.setLabel("" + data.labels[i], i);
        }
        
        for (Class<? extends Feature> featureClass : this.features)
        {
            if (!data.isComputed(featureClass))
            {
                throw new RuntimeException("Feature has not been computed: " + featureClass);
            }
            
            for (int i = 0; i < data.labels.length; i++)
            {
                Feature feature = this.data.getFeature(featureClass);
                double[] dat = (double[]) this.data.results.get(featureClass);
                feature.populateTable(table, i, dat[i]);
            }
        }
        return table;

    }

}
