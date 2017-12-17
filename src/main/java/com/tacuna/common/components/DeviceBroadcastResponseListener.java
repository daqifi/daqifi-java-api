package com.tacuna.common.components;

import java.util.Set;

import com.lp.io.MessageConsumer;
import com.lp.io.messages.DeviceBroadcastMessage;
import com.lp.io.messages.Message;
import com.tacuna.common.devices.DeviceFactory;
import com.tacuna.common.devices.DeviceInterface;

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
