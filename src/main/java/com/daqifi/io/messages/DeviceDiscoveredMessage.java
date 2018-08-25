package com.daqifi.io.messages;

import com.daqifi.common.devices.DeviceInterface;

/**
 * Created by marc on 8/8/18.
 */

public class DeviceDiscoveredMessage extends Message {
    public DeviceInterface getDevice() {
        return device;
    }

    private final DeviceInterface device;

    public DeviceDiscoveredMessage(DeviceInterface device){
        super(device);
        this.device = device;
    }
}
