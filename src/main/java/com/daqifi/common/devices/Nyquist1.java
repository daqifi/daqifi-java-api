package com.daqifi.common.devices;

/**
 * Nyquist 1 Device class.
 */
public class Nyquist1 extends AD7195W {
    public static final int ANALOG_IN_CHANNELS = 16;
    public static final int DIGITAL_IO_CHANNELS = 16;
    public static final int ANALOG_RES = 4096;

    public Nyquist1(){
        super(ANALOG_IN_CHANNELS, 0, DIGITAL_IO_CHANNELS);
    }

    @Override
    public String getDeviceType() {
        return "Nyquist 1";
    }
}
