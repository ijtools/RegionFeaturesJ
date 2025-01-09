/**
 * 
 */
package net.ijt.regfeat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import inra.ijpb.algo.AlgoStub;
import inra.ijpb.color.ColorMaps;
import inra.ijpb.label.LabelImages;

/**
 * The class containing results (and data?) for the analysis of regions within an image.
 */
public class RegionFeatures extends AlgoStub
{
    // ==================================================
    // Static methods
    
    public static final RegionFeatures initialize(ImagePlus imagePlus)
    {
        return new RegionFeatures(imagePlus, LabelImages.findAllLabels(imagePlus));
    }
    
    public static final RegionFeatures initialize(ImagePlus imagePlus, int[] labels)
    {
        return new RegionFeatures(imagePlus, labels);
    }
    
    
    // ==================================================
    // Class members
    
    /**
     * The image containing the map of region label for each pixel / voxel.
     */
    public ImagePlus labelMap;
    
    /**
     * The labels of the regions to be analyzed.
     */
    public int[] labels;
    
    /**
     * The classes of the features that will be used to populate the data table.
     */
    Collection<Class<? extends Feature>> featureClasses = new ArrayList<>();
    
    /**
     * The map of features indexed by their class. When feature is created for
     * the first time, it is indexed within the results class to retrieve it in
     * case it is requested later.
     */
    public Map<Class<? extends Feature>, Feature> features;
    
    /**
     * A map for storing optional data that can be used to compute additional
     * features, for example region intensities.
     */
    public Map<String, ImagePlus> imageData;
    
    /**
     * The results computed for each feature. 
     */
    public Map<Class<? extends Feature>, Object> results;
    
    public Color[] labelColors;
    
    public boolean displayUnitsInTable = false;
    
    
    // ==================================================
    // Constructors
    
    public RegionFeatures(ImagePlus imagePlus, int[] labels)
    {
        // store locally label map data
        this.labelMap = imagePlus;
        this.labels = labels;
        
        // initialize data structures
        this.features = new HashMap<Class<? extends Feature>, Feature>();
        this.imageData = new HashMap<String, ImagePlus>();
        this.results = new HashMap<Class<? extends Feature>, Object>();
        
        // additional setup
        createLabelColors(this.labels.length);
    }
    
    private void createLabelColors(int nLabels)
    {
        byte[][] lut = ColorMaps.CommonLabelMaps.GLASBEY_BRIGHT.computeLut(nLabels, false);
        this.labelColors = new Color[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
            this.labelColors[i] = createColor(lut[i][0], lut[i][1], lut[i][2]);
        }
    }
    
    private static final Color createColor(byte r, byte g, byte b)
    {
        return new Color(r & 0x00FF, g & 0x00FF, b & 0x00FF);
    }
    
    
    // ==================================================
    // Processing methods
    
    /**
     * Updates the informations stored within this result class with the feature
     * identified by the specified class, if it is not already computed.
     * 
     * @param featureClass
     *            the class to compute
     */
    public void process(Class<? extends Feature> featureClass)
    {
        if (isComputed(featureClass)) return;
        
        Feature feature = getFeature(featureClass);
        ensureRequiredFeaturesAreComputed(feature);
        
        // compute feature, and index into results
        this.fireStatusChanged(this, "Compute feature: " + featureClass.getSimpleName());
        this.results.put(featureClass, feature.compute(this));
    }
    
    public boolean isComputed(Class<? extends Feature> featureClass)
    {
        return results.containsKey(featureClass);
    }
    
    public Feature getFeature(Class<? extends Feature> featureClass)
    {
        Feature feature = this.features.get(featureClass);
        if (feature == null)
        {
            feature = Feature.create(featureClass);
            this.features.put(featureClass, feature);
        }
        return feature;
    }
    
    public void ensureRequiredFeaturesAreComputed(Feature feature)
    {
        feature.requiredFeatures().stream()
            .filter(fc -> !isComputed(fc))
            .forEach(fc -> process(fc));
    }

    public RegionFeatures add(Class<? extends Feature> featureClass)
    {
        this.featureClasses.add(featureClass);
        return this;
    }
    
    public boolean contains(Class<? extends Feature> featureClass)
    {
        return this.featureClasses.contains(featureClass);
    }
    
    public RegionFeatures addImageData(String dataName, ImagePlus image)
    {
        this.imageData.put(dataName, image);
        return this;
    }
    
    public ImagePlus getImageData(String dataName)
    {
        return this.imageData.get(dataName);
    }
    
    public RegionFeatures computeAll()
    {
        this.featureClasses.stream().forEach(this::process);
        return this;
    }
    
    public ResultsTable createTable()
    {
        this.fireStatusChanged(this, "RegionFeatures: create result table");
        // ensure everything is computed
        computeAll();
        
        ResultsTable table = initializeTable(this.labels);
        
        for (Class<? extends Feature> featureClass : this.featureClasses)
        {
            if (!isComputed(featureClass))
            {
                throw new RuntimeException("Feature has not been computed: " + featureClass);
            }
            
            Feature feature = getFeature(featureClass);
            if (feature instanceof RegionFeature)
            {
                ((RegionFeature) feature).updateTable(table, this);
            }
        }
        return table;
    }
    
    public RegionFeatures displayUnitsInTable(boolean flag)
    {
        this.displayUnitsInTable = flag;
        return this;
    }
    
    private static final ResultsTable initializeTable(int[] labels)
    {
        // Initialize label column in table
        ResultsTable table = new ResultsTable();
        for (int i = 0; i < labels.length; i++)
        {
            table.incrementCounter();
            table.setLabel("" + labels[i], i);
        }
        return table;
    }

    public void printComputedFeatures()
    {
        results.keySet().stream().forEach(c -> System.out.println(c.getSimpleName()));
    }
}
