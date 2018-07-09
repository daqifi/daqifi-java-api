package com.daqifi.io.messages;

/**
 * Message generated when a measurement is made.
 */
public class MeasurementMessage extends Message {

    /**
     * Analog data measured from the device.
     */
    public float[] analogData = null;
    /**
     * Digital data measure from the device.
     */
    public byte[] digitalData;

    public int deviceId;
    /**
     * Device timestamp. Should be interpreted as an unsigned value.
     */
    public long timestamp;

    public MeasurementMessage(Object data) {
        super(data);
    }

}
