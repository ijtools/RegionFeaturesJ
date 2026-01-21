/**
 * 
 */
package net.ijt.regfeat.plugins;

import java.util.stream.Stream;

import net.ijt.regfeat.RegionFeatures.UnitDisplay;

/**
 * An enumeration for the different unit display strategies available within the
 * {@code RegionFeatures} class.
 * 
 * @see UnitDisplay
 * @see RegionMorphologyPlugin
 */
public enum UnitDisplayOption
{
    /** Do not display units. */
    NONE("None", UnitDisplay.NONE),
    /** Appends unit names to column names (when relevant). */
    COLUMN_NAMES("Column Names", UnitDisplay.COLUMN_NAMES),
    /** Creates new columns containing unit name of the preceding column. */
    NEW_COLUMNS("New Columns", UnitDisplay.NEW_COLUMNS),
    /**
     * Creates a new table mapping each feature within the result table to the
     * unit name for the column.
     */
    NEW_TABLE("New Table", UnitDisplay.NEW_TABLE);

    /** The name of the option, for GUI display */
    private final String label;

    /** the class of the feature to compute */
    private final UnitDisplay unitDisplay;

    private UnitDisplayOption(String label, UnitDisplay choice)
    {
        this.label = label;
        this.unitDisplay = choice;
    }
    
    /**
     * Returns the label associated to this enumeration item.
     * 
     * @return the label associated to this enumeration item
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * Returns the UnitDisplay instance corresponding to this enumeration item.
     * 
     * @return the UnitDisplay instance corresponding to this enumeration item
     */
    public UnitDisplay getUnitDisplay() 
    {
        return unitDisplay;
    }
    
    /**
     * @return a string representation of this enumeration item
     */
    public String toString() 
    {
        return label;
    }
    
    /**
     * Returns the array of labels for the items within this enumeration.
     * 
     * @return the array of labels for the items within this enumeration
     */
    public static String[] getAllLabels()
    {
        return Stream.of(UnitDisplayOption.values())
                .map(item -> item.label)
                .toArray(String[]::new);
    }
    
    /**
     * Determines the FeatureOption from its label.
     * 
     * @param label
     *            the name of the FeatureOption
     * @return the FeatureOption enumeration corresponding to the name
     * @throws IllegalArgumentException
     *             if FeatureOption name is not recognized.
     */
    public static UnitDisplayOption fromLabel(String label) 
    {
        if (label != null)
            label = label.toLowerCase();
        for (UnitDisplayOption value : UnitDisplayOption.values()) 
        {
            String cmp = value.label.toLowerCase();
            if (cmp.equals(label))
                return value;
        }
        throw new IllegalArgumentException("Unable to parse UnitDisplayOption with label: " + label);
    }

}
