package com.tacuna.common.components.datascaling;

/**
 * Created by marc on 3/29/16.
 */
public class DtoV implements DataScale {
    public static double convertToVoltage(int sampleValue, int extAdcRange) {
        double extAdcResolution = 131072.0;
        return ((sampleValue * ((extAdcRange * 10.0) + 10.0)) / (extAdcResolution));
    }

    @Override
    public double convert(int sampleValue, int extAdcRange) {
        return convertToVoltage(sampleValue, extAdcRange);
    }

    @Override
    public float[] getCoefficients() {
        return new float[0];
    }

    @Override
    public String getName() {
        return "D_TO_V";
    }
}
