// Copyright 2013 Marc Bernardini.
package com.tacuna.common.devices;

import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.lp.io.messages.DeviceBroadcastMessage;

/**
 * The Device factory is used to create instances of DeviceInterfaces that match
 * the physical device.
 *
 * @author Marc
 */
public class DeviceFactory {

  public static class InvalidDeviceType extends Exception{
    public InvalidDeviceType(String type){

    }
  }
  HashSet<DeviceInterface> knownDevices = new HashSet<DeviceInterface>();

  private static Map<String, Class<? extends DeviceInterface>> deviceClasses = initializeDeviceClasses();
  private static  Map<String, Class<? extends DeviceInterface>> initializeDeviceClasses(){
    Map<String, Class<? extends DeviceInterface>> map = new HashMap<String, Class<? extends DeviceInterface>>();
    map.put("Nyquist 1", Nyquist1.class);
    map.put("Nyquist 2", Nyquist2.class);
    map.put("Nyquist 3", Nyquist3.class);
    return map;
  }

  /**
   * Not Implemented.
   *
   * @return Empty list
   */
  public Collection<DeviceInterface> getKnownDevices() {
    return knownDevices;
  }

  /**
   * Not Implemented.
   *
   * @param newDevice
   */
  public void addDevice(DeviceInterface newDevice) {
    knownDevices.add(newDevice);
  }

  /**
   * Converts the DeviceBroadCast Message to a DeviceInterface. Currently
   * there is only one device implemented but in the feature expect this
   * method to determine the device type and return an instance of the correct
   * device.
   *
   * @param message
   * @return A DeviceInterface representing the device that sent the Broadcast
   * Message.
   */
  public static DeviceInterface getDevice(DeviceBroadcastMessage message) {
    DeviceInterface device = new AD7195W();
    device.setDeviceName(message.getDeviceName());
    InetSocketAddress address = InetSocketAddress.createUnresolved(
            message.getHost(), message.getTcpPort());
    device.setNetworkAddress(address);
    device.setMacAddress(message.getMacAddress());

    return device;
  }

  public static DeviceInterface getDevice(String deviceType, String host, int port)
          throws InvalidDeviceType{
    DeviceInterface device = null;
    try{
      Class deviceClass = deviceClasses.get(deviceType);
      device = (DeviceInterface) deviceClass.newInstance();
    } catch (InstantiationException e){
      throw new InvalidDeviceType(deviceType);
    } catch (IllegalAccessException e){
      throw new InvalidDeviceType(deviceType);
    }


    device.setDeviceName(host);
    InetSocketAddress address = InetSocketAddress.createUnresolved(host, port);
    device.setNetworkAddress(address);
    device.setMacAddress("00:00:00:00:00:00");

    return device;
  }
}
