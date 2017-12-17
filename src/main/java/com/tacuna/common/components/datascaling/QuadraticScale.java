package com.tacuna.common.components.datascaling;

/**
 * Created by marc on 3/29/16.
 */
public class QuadraticScale implements DataScale {
    private double a;
    private double b;
    private double c;

    public QuadraticScale(double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public double convert(int sample, int extAdcRange) {
        double x = DtoV.convertToVoltage(sample,extAdcRange);
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
}
