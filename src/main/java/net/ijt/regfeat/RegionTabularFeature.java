/**
 * 
 */
package net.ijt.regfeat;

import ij.measure.ResultsTable;

/**
 * Abstract class for a feature that can generate a data table with as many rows
 * as the number of regions to analyze.
 */
public interface RegionTabularFeature extends Feature
{
    /**
     * Updates the specified result table with the result of this feature.
     * Depending on features, this method may populate one or several columns.
     * In the case of utility or intermediary feature, the implementation of
     * this method may be empty.
     * 
     * @param table
     *            the results table to populate
     * @param data
     *            the class containing all the computed features.
     */
    public abstract void updateTable(ResultsTable table, RegionFeatures data);

}
