package com.daqifi.common.devices;

/**
 * Created by marc on 2/20/17.
 */
public class Nyquist2 extends AD7195W {

    public static final int ANALOG_IN_CHANNELS = 16;
    public static final int ANALOG_OUT_CHANNELS = 8;
    public static final int DIGITAL_IO_CHANNELS = 1;

    public Nyquist2(){
        super(ANALOG_IN_CHANNELS, ANALOG_OUT_CHANNELS, DIGITAL_IO_CHANNELS);
    }

    @Override
    public String getDeviceType() {
        return "Nyquist 2";
    }
}
