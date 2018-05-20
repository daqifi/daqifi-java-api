package com.daqifi.io.messages;

/**
 * Message generated when a measurement is made.
 */
public class MeasurementMessage extends Message {

    public float[] analogData = null;
    public byte[] digitalData;
    public int deviceId;
    public long timestamp;

    public MeasurementMessage(Object data) {
        super(data);
    }

}
