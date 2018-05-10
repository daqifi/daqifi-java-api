package com.daqifi.common.components.datascaling;

/**
 * Created by marc on 3/29/16.
 */
public interface DataScale {
    double convert(int sample, int extAdcRange);
    float[] getCoefficients();
    String getName();
    DataScale getBase();
}
