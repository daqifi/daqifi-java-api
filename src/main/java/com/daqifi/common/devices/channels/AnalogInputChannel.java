// Copyright 2013 Marc Bernardini.
package com.daqifi.common.devices.channels;

import com.daqifi.common.components.CircularArrayList;
import com.daqifi.common.components.datascaling.DataScale;
import com.daqifi.common.components.datascaling.DtoV;
import com.daqifi.common.devices.DeviceInterface;

import java.util.List;

/**
 * AnalogInput channelIndex is used as a model for an input channelIndex that
 * measures an analog voltage.
 *
 * @author Marc
 */
public class AnalogInputChannel extends Channel implements InputInterface<Float> {

    public static final int BUFFER_SIZE = 1000;

    @Override
    public Float getCurrentValue() {
        return lastValue;
    }

    private float lastValue = Float.NaN;
    private boolean active;
    private DataScale scale = new DtoV();
    /**
     * Minimum series value. Default set to positive infinity to ensure that
     * when data is added the correct data min is taken. (i.e. everything is
     * less than infinity, so a new value will be set as soon as data is added)
     */
    private float minValue = Float.POSITIVE_INFINITY;
    /**
     * Maximum series value. Default set to positive infinity to ensure that
     * when data is added the correct max is taken. (i.e. everything is less
     * than infinity)
     */
    private float maxValue = Float.NEGATIVE_INFINITY;

    public class TimeValue {
        public long time;
        public float value;

        /**
         * @param time
         * @param value
         */
        public TimeValue(long time, float value) {
            super();
            this.time = time;
            this.value = value;
        }

        public TimeValue() {
            this.time = 0;
            this.value = 0;
        }
    }

    /**
     * Buffer for holding measured values.
     */
    private final List<TimeValue> buffer = new CircularArrayList<TimeValue>(BUFFER_SIZE);

    public long copyBuffer(CircularArrayList<Float> copy, long from) {
        synchronized (buffer){
            int size = buffer.size();
            for(int ii = 0; ii < size; ii++){
                TimeValue val = buffer.get(ii);
                if(val.time <= from) continue;

                copy.safeAdd(val.value);
            }
            return size > 0 ? buffer.get(size - 1).time: 0;
        }
    }

    public AnalogInputChannel(String name, int index, DeviceInterface device) {
        super(name, index, "V", device);
    }

    public AnalogInputChannel(String name, int index) {
        super(name, index, "V", null);
    }

    @Override
    public Type getType() {
        return Type.ANALOG_IN;
    }

    @Override
    public void add(long time, Float value) {
        lastValue = value;

        // Set the min if the value is less than the current
        // min
        if (minValue > value) {
            minValue = value;
        }
        // set the max is the value is greater than the current
        // max
        if (maxValue < value) {
            maxValue = value;
        }

        synchronized (buffer) {
            if (buffer.size() == BUFFER_SIZE) {
                TimeValue old = buffer.remove(0);
                old.time = time;
                old.value = value;
                buffer.add(old);
            } else {
                buffer.add(new TimeValue(time, value));
            }
        }
    }

    @Override
    public Float getMaximum() {
        return maxValue;
    }

    @Override
    public Float getMinimum() {
        return minValue;
    }

    @Override
    public int getNumberOfSamples() {
        synchronized (buffer) {
            return buffer.size();
        }
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean value) {
        active = value;
    }

    public void setScale(DataScale scale) {
        this.scale = scale;
    }

    public DataScale getScale() {
        return scale;
    }

    public void setCalibrationValues(double analogInPortRange, double analogInScaleM, double calM, double calB){
        DataScale base = getScale().getBase();
        if(base instanceof DtoV){
            ((DtoV)base).setCalibrationValues(analogInPortRange, analogInScaleM, calM, calB);
        }
    }

    public double convert(int sample, int adcDataRange) {
        return scale.convert(sample, adcDataRange);
    }

    public float getSamplePeriod(){
        synchronized (buffer) {
            return (buffer.get(buffer.size() - 1).time - buffer.get(0).time) / buffer.size();
        }
    }
}
