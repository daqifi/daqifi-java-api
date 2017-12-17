package com.tacuna.common.devices.channels;

import com.tacuna.common.devices.DeviceInterface;
import com.tacuna.common.devices.scpi.Command;

/**
 * Created by marc on 10/25/16.
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
