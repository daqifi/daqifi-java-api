package com.daqifi.common.components.datascaling;

/**
 * Interface used for converting data from the integer read from the Device to a floating point value
 */
public interface DataScale {

    /**
     * Given sample, return converted value
     * @param sample
     * @param extAdcRange
     * @return
     */
    double convert(int sample, int extAdcRange);

    /**
     * Returns an array of coefficients for the DataScale
     * @return
     */
    float[] getCoefficients();

    /**
     * Returns the name of the scale
     * @return
     */
    String getName();

    /**
     * Returns the base scale. Useful when scales are chained together.
     * @return
     */
    DataScale getBase();
}
