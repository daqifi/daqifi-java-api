package com.tacuna.common.components.datascaling;

/**
 * Created by marc on 3/29/16.
 */
public class LinearScale implements DataScale {

    public LinearScale(double m, double b) {
        this.m = m;
        this.b = b;
    }

    private double m;
    private double b;

    @Override
    public double convert(int sample, int extAdcRange) {
        return m * DtoV.convertToVoltage(sample,extAdcRange) + b;
    }

    @Override
    public float[] getCoefficients() {
        return new float[]{(float)m, (float)b};
    }

    @Override
    public String getName() {
        return "LINEAR";
    }
}
