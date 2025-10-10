/**
 * 
 */
package net.ijt.regfeat;

import ij.gui.Roi;

/**
 * Specialization of the Feature interface for feature that can be converted to
 * ROI.
 */
public interface RoiFeature
{
    /**
     * Converts the data computed for each region into a Region Of Interest that
     * can be managed by the RoiManager.
     * 
     * @param data
     *            the data structure containing results of features computed on
     *            regions
     * @return an array of Roi with as many elements as the number of regions
     */
    public Roi[] computeRois(RegionFeatures data);
}
