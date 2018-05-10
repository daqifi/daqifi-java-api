package com.daqifi.common.components.datascaling;

/**
 * Created by marc on 3/29/16.
 */
public class DtoV implements DataScale {
    double analogInPortRange;
    double analogInScaleM;
    double calM;
    double calB;

    public DtoV(){
        analogInPortRange = 5;
        analogInScaleM = 1;
        calM = 1;
        calB = 0;
    }

    public DtoV(double analogInPortRange, double analogInScaleM, double calM, double calB) {
        this.analogInPortRange = analogInPortRange;
        this.analogInScaleM = analogInScaleM;
        this.calM = calM;
        this.calB = calB;
    }

    @Override
    public double convert(int sampleValue, int extAdcResolution) {
        return (((double) sampleValue) / (double) extAdcResolution)* analogInPortRange*calM*analogInScaleM + calB;
        //convertToVoltage(sampleValue, extAdcResolution);
    }

    @Override
    public float[] getCoefficients() {
        return new float[0];
    }

    @Override
    public String getName() {
        return "D_TO_V";
    }

    @Override
    public DataScale getBase() {
        return this;
    }

    public void setCalibrationValues(double analogInPortRange, double analogInScaleM, double calM, double calB){
        this.analogInPortRange = analogInPortRange;
        this.analogInScaleM = analogInScaleM;
        this.calM = calM;
        this.calB = calB;
    }
}
