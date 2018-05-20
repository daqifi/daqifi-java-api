package com.daqifi.common.components;

import java.util.Set;

import com.daqifi.io.MessageConsumer;
import com.daqifi.io.messages.DeviceBroadcastMessage;
import com.daqifi.common.devices.DeviceFactory;
import com.daqifi.common.devices.DeviceInterface;

/**
 * The device broadcast listener listens for the DeviceBroadCast Messages and
 * adds devices to the set of known devices.
 *
 * @author Marc
 */
public class DeviceBroadcastResponseListener implements MessageConsumer<DeviceBroadcastMessage> {

  private final Set<DeviceInterface> knownDevices;

  DeviceBroadcastResponseListener(Set<DeviceInterface> knownDevices) {
    this.knownDevices = knownDevices;
  }

  @Override
  public void onMessage(DeviceBroadcastMessage broadCastMessage) {
    DeviceInterface device = DeviceFactory.getDevice(broadCastMessage);
    knownDevices.add(device);
  }

}
