package com.daqifi.common.devices.channels;

import com.daqifi.common.devices.DeviceInterface;
import com.daqifi.common.devices.scpi.Command;

/**
 *
 */
public class AnalogOutputChannel extends Channel {

    public float getVoltage() {
        return voltage;
    }

    public void setVoltage(float voltage) {
        this.voltage = voltage;
        //TODO: Verify that this is the correct SCPI command
        if(isActive()) {
            getDevice().send(new Command(String.format("SOURce:VOLTage:LEVel %d %f", getDeviceIndex(), voltage)));
        }
    }

    private float voltage;

    public AnalogOutputChannel(String name, int index, DeviceInterface device) {
        super(name, index, "V", device);
        voltage = 0.0f;
    }

    @Override
    public Type getType() {
        return Type.ANALOG_OUT;
    }
}
