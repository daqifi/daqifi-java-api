package com.daqifi.common.components.datascaling;

/**
 * Converts the integer value to a voltage.
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

    /**
     * Constructs the DtoV converter with the channel calibration values.
     * @param analogInPortRange
     * @param analogInScaleM
     * @param calM
     * @param calB
     */
    public DtoV(double analogInPortRange, double analogInScaleM, double calM, double calB) {
        this.analogInPortRange = analogInPortRange;
        this.analogInScaleM = analogInScaleM;
        this.calM = calM;
        this.calB = calB;
    }

    /**
     * Returns the value as a voltage
     * @param sampleValue
     * @param extAdcResolution
     * @return
     */
    @Override
    public double convert(int sampleValue, int extAdcResolution) {
        return (((double) sampleValue) / (double) extAdcResolution)* analogInPortRange*calM*analogInScaleM + calB;
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
