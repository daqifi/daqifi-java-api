package com.tacuna.common.components.datascaling;

/**
 * Created by marc on 3/29/16.
 */
public class LinearScale implements DataScale {

    final private double m;
    final private double b;
    final private DataScale base;

    public LinearScale(double m, double b, DataScale base) {
        this.m = m;
        this.b = b;
        this.base = base;
    }

    @Override
    public double convert(int sample, int extAdcRange) {
        return m * base.convert(sample,extAdcRange) + b;
    }

    @Override
    public float[] getCoefficients() {
        return new float[]{(float)m, (float)b};
    }

    @Override
    public String getName() {
        return "LINEAR";
    }

    @Override
    public DataScale getBase() {
        return base;
    }
}
