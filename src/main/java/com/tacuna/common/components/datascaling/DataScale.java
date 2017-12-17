package com.tacuna.common.components.datascaling;

/**
 * Created by marc on 3/29/16.
 */
public interface DataScale {
    public double convert(int sample, int extAdcRange);
    public float[] getCoefficients();
    public String getName();
}
