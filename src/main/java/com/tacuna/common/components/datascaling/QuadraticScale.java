package com.tacuna.common.components.datascaling;

/**
 * Created by marc on 3/29/16.
 */
public class QuadraticScale implements DataScale {
    private double a;
    private double b;
    private double c;
    private DataScale base;

    public QuadraticScale(double a, double b, double c, DataScale base) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.base = base;
    }

    @Override
    public double convert(int sample, int extAdcRange) {
        double x = base.convert(sample,extAdcRange);
        return a*x*x + b*x + c;
    }

    @Override
    public float[] getCoefficients() {
        return new float[]{(float)a, (float)b, (float)c};
    }

    @Override
    public String getName() {
        return "QUADRATIC";
    }

    @Override
    public DataScale getBase() {
        return base;
    }
}
