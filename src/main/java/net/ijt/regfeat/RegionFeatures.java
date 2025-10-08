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
import inra.ijpb.algo.Algo;
import inra.ijpb.algo.AlgoEvent;
import inra.ijpb.algo.AlgoListener;
import inra.ijpb.algo.AlgoStub;
import inra.ijpb.color.ColorMaps;
import inra.ijpb.label.LabelImages;

/**
 * The main class of the plugin, that gathers all the information necessary to
 * analyze image as well as the results. The class contains:
 * <ul>
 * <li>a reference to the label map representing the regions to analyze</li>
 * <li>the list of features to analyze</li>
 * <li>for each feature, the result of computation</li>
 * <li>general options for computing features and presenting the results</li>
 * </ul>
 */
public class RegionFeatures extends AlgoStub
{
    // ==================================================
    // Enumerations
    
    /**
     * Specifies how to manage the display of unit names.
     */
    public enum UnitDisplay
    {
        /** Do not display unit names */
        NONE,
        /** Append unit names to column names */
        COLUMN_NAMES,
        /** Create new columns containing unit names */
        NEW_COLUMNS,
        /** Create a new table with column names as rows, ands unit names in a column */
        NEW_TABLE
    }
    
    
    // ==================================================
    // Static methods
    
    
    /**
     * Creates a new instance of RegionFeatures initialized with the specified
     * label maps, and using the labels that can be found within the label map.
     * 
     * @param imagePlus
     *            the ImagePlus containing the label map
     * @return a new instance of {@code RegionFeatures} initialized with the
     *         specified label map.
     */
    public static final RegionFeatures initialize(ImagePlus imagePlus)
    {
        return new RegionFeatures(imagePlus, LabelImages.findAllLabels(imagePlus));
    }
    
    /**
     * Creates a new instance of RegionFeatures initialized with the specified
     * label maps, and using the list of labels specified by second argument.
     * 
     * @param imagePlus
     *            the ImagePlus containing the label map
     * @param labels
     *            the list of labels of regions that will be analyzed
     * @return a new instance of {@code RegionFeatures} initialized with the
     *         specified label map and list of region labels.
     */
    public static final RegionFeatures initialize(ImagePlus imagePlus, int[] labels)
    {
        return new RegionFeatures(imagePlus, labels);
    }
    
    
    // ==================================================
    // Class members
    
    /**
     * The image containing the map of region label for each pixel / voxel.
     * Created at initialization.
     */
    public final ImagePlus labelMap;
    
    /**
     * The labels of the regions to be analyzed.
     * Created at initialization.
     */
    public final int[] labels;
    
    /**
     * Map a label to an index within the array of labels.
     * Created at initialization.
     */
    public final HashMap<Integer, Integer> labelIndices;
    
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
    
    /**
     * The color associated to each label, as an array with the same length as
     * the {@code labels} member.
     * 
     * Created at initialization.
     */
    public Color[] labelColors;
    
    /**
     * Specifies how to display calibration unit. Default is {@code NONE}.
     */
    public UnitDisplay unitDisplay = UnitDisplay.NONE;
    
    
    // ==================================================
    // Constructors
    
    /**
     * Creates a new instance of RegionFeatures initialized with the specified
     * label maps, and using the list of labels specified by second argument.
     * 
     * @param imagePlus
     *            the ImagePlus containing the label map
     * @param labels
     *            the list of labels of regions that will be analyzed
     */
    public RegionFeatures(ImagePlus imagePlus, int[] labels)
    {
        // store locally label map data
        this.labelMap = imagePlus;
        this.labels = labels;
        
        // initialize data structures
        this.features = new HashMap<Class<? extends Feature>, Feature>();
        this.imageData = new HashMap<String, ImagePlus>();
        this.results = new HashMap<Class<? extends Feature>, Object>();
        
        // create associative array to know index of each label
        this.labelIndices = mapLabelIndices(labels);
        
        // additional setup
        createLabelColors(this.labels.length);
    }
    
    /**
     * Creates an associative array to retrieve the index corresponding to each label.
     * 
     * The resulting map verifies the relation:
     * <pre>
     * {@code
     * int[] labels = ...
     * Map<Integer,Integer> labelIndices = RegionFeatures.mapLabelIndices(labels);
     * int index = ...
     * assert(index == labelIndices.get(labels[index]));
     * }
     * </pre>
     * 
     * @param labels
     *            an array of integer labels
     * @return a HashMap instance with each label as key, and the index of the
     *         label in array as value.
     */
    private static final HashMap<Integer, Integer> mapLabelIndices(int[] labels)
    {
        int nLabels = labels.length;
        HashMap<Integer, Integer> labelIndices = new HashMap<Integer, Integer>();
        for (int i = 0; i < nLabels; i++) 
        {
            labelIndices.put(labels[i], i);
        }

        return labelIndices;
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
        
        // propagate algorithm event of feature to the RegionFeature listeners
        if (feature instanceof Algo)
        {
            ((Algo) feature).addAlgoListener(new AlgoListener() {

                @Override
                public void algoProgressChanged(AlgoEvent evt)
                {
                    fireProgressChanged(evt);
                }

                @Override
                public void algoStatusChanged(AlgoEvent evt)
                {
                    fireStatusChanged(evt);
                }
            });
        }
        this.results.put(featureClass, feature.compute(this));
    }
    
    /**
     * Checks whether the specified feature is computed.
     * 
     * @param featureClass
     *            the class of the feature to check.
     * @return true if the feature with the specified class has been computed.
     */
    public boolean isComputed(Class<? extends Feature> featureClass)
    {
        return results.containsKey(featureClass);
    }
    
    /**
     * Retrieves or creates the instance of {@code Feature} associated to this
     * analyzer, based on the class of the feature.
     * 
     * Uses lazy loading: the Feature instance is created only during the first
     * request for the feature.
     * 
     * @param featureClass
     *            the class of the feature to retrieve
     * @return an instance of the specified class stored within this analyzer.
     */
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
    
    /**
     * Ensures that the specified feature has been computed (by calling the
     * {@code process} method). If not, calls, the {@code process} method.
     * 
     * @param feature
     *            the feature to check
     */
    public void ensureRequiredFeaturesAreComputed(Feature feature)
    {
        feature.requiredFeatures().stream()
            .filter(fc -> !isComputed(fc))
            .forEach(fc -> process(fc));
    }

    /**
     * Adds the feature to the list of features to compute.
     * 
     * @param featureClass
     *            the class of the feature to compute.
     * @return a reference to this {@code RegionFeatures}, for chaining
     *         operations.
     */
    public RegionFeatures add(Class<? extends Feature> featureClass)
    {
        this.featureClasses.add(featureClass);
        return this;
    }
    
    /**
     * Checks whether this class will process the specified feature.
     * 
     * @param featureClass
     *            the feature to check
     * @return true if this analyzer will process / compute the specified
     *         feature.
     */
    public boolean contains(Class<? extends Feature> featureClass)
    {
        return this.featureClasses.contains(featureClass);
    }
    
    /**
     * Adds an image data identified with a name.
     * 
     * @param dataName
     *            the name of the data (e.g. "intensity", "colors",
     *            "distanceMap"...)
     * @param image
     *            the image containing the data
     * @return a reference to this {@code RegionFeatures}, for chaining
     *         operations.
     */
    public RegionFeatures addImageData(String dataName, ImagePlus image)
    {
        this.imageData.put(dataName, image);
        return this;
    }
    
    /**
     * Returns an image data identified with a name.
     * 
     * @param dataName
     *            the name of the data (e.g. "intensity", "colors",
     *            "distanceMap"...)
     * @return the image containing the data
     */
    public ImagePlus getImageData(String dataName)
    {
        return this.imageData.get(dataName);
    }
    
    /**
     * Computes all the features within this {@code RegionFeatures}.
     * 
     * @return a reference to this {@code RegionFeatures}, for chaining
     *         operations.
     */
    public RegionFeatures computeAll()
    {
        this.featureClasses.stream().forEach(this::process);
        return this;
    }
    
    /**
     * Creates a new results table from the different features contained within
     * the class.
     * 
     * @return a new ResultsTable containing a summary of the computed features.
     */
    public ResultsTable createTable()
    {
        return createTables()[0];
    }
    
    /**
     * Returns an array containing two ResultsTable: one with the feature
     * results, another one containing the unit associated to each column in the
     * first table.
     * 
     * @return an array of two ResultsTable.
     */
    public ResultsTable[] createTables()
    {
        // ensure everything is computed
        this.fireStatusChanged(this, "RegionFeatures: compute all features");
        computeAll();
        
        this.fireStatusChanged(this, "RegionFeatures: create result tables");
        ResultsTable fullTable = initializeRegionTable();
        ResultsTable columnUnitsTable = new ResultsTable();
        
        // update the global table with each feature
        for (Class<? extends Feature> featureClass : this.featureClasses)
        {
            if (!isComputed(featureClass))
            {
                throw new RuntimeException("Feature has not been computed: " + featureClass);
            }
            
            Feature feature = getFeature(featureClass);
            if (feature instanceof RegionTabularFeature)
            {
                // create table associated to feature
                RegionTabularFeature tabularFeature = (RegionTabularFeature) feature; 
                ResultsTable table = tabularFeature.createTable(this);
                
                // also retrieve information about columns 
                String[] colNames = columnHeadings(table);
                String[] unitNames = tabularFeature.columnUnitNames(this);
                
                // switch processing depending on the strategy for managing unit names
                switch(unitDisplay)
                {
                    case NONE:
                        // simply append columns to the full table
                        appendColumns(fullTable, table);
                        break;
                    case COLUMN_NAMES:
                        // update columns names before appending to the full tables
                        colNames = appendUnitNames(colNames, unitNames);
                        for (int c = 0; c < colNames.length; c++)
                        {
                            appendColumn(fullTable, colNames[c], table.getColumnAsDoubles(c));
                        }
                        break;
                    case NEW_COLUMNS:
                        // append columns and new columns containing unit names
                        if (unitNames != null && unitNames.length > 0)
                        {
                            addColumnsAndUnits(fullTable, table, unitNames);
                            continue;
                        }
                        else
                        {
                            appendColumns(fullTable, table);
                        }
                        break;
                    case NEW_TABLE:
                        // append full table, and update the columnUnits table
                        appendColumns(fullTable, table);
                        updateColumnUnitsTable(columnUnitsTable, colNames, unitNames);
                        break;

                    default:
                        throw new RuntimeException("Unknown strategy for managing units");
                }
            }
        }
        
        return new ResultsTable[] {fullTable, columnUnitsTable};
    }
    
    private String[] appendUnitNames(String[] colNames, String[] unitNames)
    {
        // initialize with same column names
        String[] res = new String[colNames.length];
        System.arraycopy(colNames, 0, res, 0, colNames.length);

        if (unitNames != null && unitNames.length > 0)
        {
            for (int c = 0; c < colNames.length; c++)
            {
                String unitName = unitNames[c];
                if (!isBlank(unitName))
                {
                    res[c] = String.format("%s_(%s)", colNames[c], unitName);
                }
            }
        }
        
        return res;
    }
    
    private static final boolean isBlank(String string)
    {
        return string == null || string.trim().isEmpty();
    }
    
    private ResultsTable addColumnsAndUnits(ResultsTable res, ResultsTable table, String[] unitNames)
    {
        String[] colNames = columnHeadings(table);
        for (int c = 0; c < colNames.length; c++)
        {
            String colName = colNames[c];
            appendColumn(res, colName, table.getColumnAsDoubles(c));
            
            // add a new column containing unit name
            String unitColName = colName + "_unit";
            for (int r = 0; r < table.getCounter(); r++)
            {
                res.setValue(unitColName, r, unitNames[c]);
            }
        }

        return res;
    }
    
    private static final void appendColumns(ResultsTable table1, ResultsTable table2)
    {
        String[] colNames = columnHeadings(table2);
        for (int c = 0; c <= table2.getLastColumn(); c++)
        {
            String colName = colNames[c];
            for (int r = 0; r < table1.getCounter(); r++)
            {
                table1.setValue(colName, r, table2.getValueAsDouble(c, r));
            }
        }
    }
    
    private static final void appendColumn(ResultsTable table, String colName, double[] values)
    {
        if (values.length != table.getCounter())
        {
            throw new RuntimeException("value array must have as many elements as number of rows in ResultsTable");
        }
        for (int r = 0; r < table.getCounter(); r++)
        {
            table.setValue(colName, r, values[r]);
        }
    }

    private static final void updateColumnUnitsTable(ResultsTable columnUnitsTable, String[] colNames, String[] unitNames)
    {
        for (int c = 0; c < colNames.length; c++)
        {
            columnUnitsTable.incrementCounter();
            columnUnitsTable.addValue("Column", colNames[c]);
            columnUnitsTable.addValue("Unit", "");
            if (unitNames != null && unitNames.length > 0)
            {
                columnUnitsTable.addValue("Unit", unitNames[c]);
            }
        }
    }

    /**
     * Retrieve the headings of the columns in a String array, keeping only the
     * regular columns (not the row label column).
     * 
     * @param table
     *            the results table
     * @return a string array containing the heading of each column
     */
    private static final String[] columnHeadings(ResultsTable table)
    {
        int nc = table.getLastColumn() + 1;
        String[] colNames = new String[nc];
        for (int c = 0; c < nc; c++)
        {
            colNames[c] = table.getColumnHeading(c);
        }
        return colNames;
    }

    /**
     * Selects the strategy for representing calibration units.
     * 
     * @param unitDisplay
     *            the strategy for representing calibration units.
     * @return a reference to this {@code RegionFeatures}, for chaining
     *         operations.
     */
    public RegionFeatures unitDisplay(UnitDisplay unitDisplay)
    {
        this.unitDisplay = unitDisplay;
        return this;
    }
    
    /**
     * Chooses whether calibration units should be displayed in results table.
     * 
     * @param flag
     *            the boolean flag.
     * @return a reference to this {@code RegionFeatures}, for chaining
     *         operations.
     */
    public RegionFeatures displayUnitsInTable(boolean flag)
    {
        this.unitDisplay = flag ? UnitDisplay.COLUMN_NAMES : UnitDisplay.NONE;
        return this;
    }
    
    /**
     * Creates a new ResultsTable for populating analysis results.
     * 
     * @return a new instance of ResultsTable with row labels corresponding to
     *         region labels.
     */
    public ResultsTable initializeRegionTable()
    {
        // Initialize label column in table
        ResultsTable table = new ResultsTable();
        for (int i = 0; i < this.labels.length; i++)
        {
            table.incrementCounter();
            table.setLabel("" + this.labels[i], i);
        }
        return table;
    }

    /**
     * Utility methods that prints out the list of computed features.
     */
    public void printComputedFeatures()
    {
        results.keySet().stream().forEach(c -> System.out.println(c.getSimpleName()));
    }
}
