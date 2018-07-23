// Copyright 2013 Marc Bernardini.
package com.daqifi.common.devices;

import com.daqifi.io.messages.DeviceBroadcastMessage;
import com.daqifi.common.devices.channels.AnalogInputChannel;
import com.daqifi.common.devices.channels.Channel;
import com.daqifi.common.devices.channels.ChannelInterface;
import com.daqifi.common.devices.channels.DigitalInputChannel;
import com.daqifi.common.messages.ProtoMessageV2;
import com.google.protobuf.ByteString;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * The Device factory is used to create instances of DeviceInterfaces that match
 * the physical device.
 *
 * @author Marc
 */
public class DeviceFactory {

    private static Map<String, Class<? extends DeviceInterface>> deviceClasses = initializeDeviceClasses();
    HashSet<DeviceInterface> knownDevices = new HashSet<DeviceInterface>();

    private static Map<String, Class<? extends DeviceInterface>> initializeDeviceClasses() {
        Map<String, Class<? extends DeviceInterface>> map = new HashMap<String, Class<? extends DeviceInterface>>();
        map.put("Nyquist 1", Nyquist1.class);
        map.put("Nyquist 2", Nyquist2.class);
        map.put("Nyquist 3", Nyquist3.class);
        return map;
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
        DeviceInterface device = null;

        ProtoMessageV2.DaqifiOutMessage sysinfo = message.getMessage();
        if(sysinfo != null) {
            switch (sysinfo.getDevicePn()) {
                case "Nq1":
                    device = new Nyquist1();
                    break;
                case "Nq2":
                    device = new Nyquist2();
                    break;
                case "Nq3":
                    device = new Nyquist3();
                    break;
                default:
                    device = new Nyquist1();
                    break;
            }
        } else {
            device = new Nyquist1();
        }

        device.setDeviceName(message.getDeviceName());
        InetSocketAddress address = InetSocketAddress.createUnresolved(
                message.getHost(), message.getTcpPort());
        device.setNetworkAddress(address);
        device.setMacAddress(message.getMacAddress());

        return setDeviceStatus(sysinfo, device);
    }

    private static int getByteStringAsInt(ByteString in){
        ByteBuffer bb = ByteBuffer.allocateDirect(4);
        bb.order(ByteOrder.BIG_ENDIAN);
        for(int ii = 3; ii >= 0; ii--){
            if(ii >= in.size()){
                bb.put((byte) 0xFF);
            } else {
                bb.put(in.byteAt(ii));
            }
        }
        bb.flip();
        return bb.getInt();
    }

    public static DeviceInterface setDeviceStatus(ProtoMessageV2.DaqifiOutMessage sysinfo, DeviceInterface device){
        if(sysinfo == null) return device;
        if (sysinfo.hasPwrStatus()) {
            device.setPowerStatus(sysinfo.getPwrStatus() == 1 ? DeviceInterface.PowerStatus.USB : DeviceInterface.PowerStatus.BATTERY);
        }
        if (sysinfo.hasBattStatus()) {
            device.setBatteryCharge(sysinfo.getBattStatus());
        }
        if (sysinfo.hasAnalogInRes()) {
            device.setAdcResolution(sysinfo.getAnalogInRes());
        }
        if (sysinfo.hasAnalogInPortEnabled()) {
            int analogPortEnabled = getByteStringAsInt(sysinfo.getAnalogInPortEnabled());

            for(ChannelInterface ch:Channel.filter(device.getChannels(), ChannelInterface.Type.ANALOG_IN)) {
                AnalogInputChannel aiChannel = (AnalogInputChannel) ch;
                int channelMask = 1 << ch.getDeviceIndex();
                boolean isEnabled = (channelMask & analogPortEnabled) == channelMask;
                aiChannel.setActive(isEnabled);
            }
        }
        if (sysinfo.hasDigitalPortDir()) {
            int digitalPortDir = ~getByteStringAsInt(sysinfo.getDigitalPortDir());

            for(ChannelInterface ch:Channel.filter(device.getChannels(), ChannelInterface.Type.DIGITAL_IO)){
                DigitalInputChannel diChannel = (DigitalInputChannel) ch;
                boolean isOutput = (diChannel.getBitMask() & digitalPortDir) == diChannel.getBitMask();
                if(isOutput) {
                    diChannel.setDirection(DeviceInterface.Direction.Output);
                } else {
                    diChannel.setDirection(DeviceInterface.Direction.Input);
                }
            }

        }

        //
        // Calibrations:
        //
        Iterator<Float> calBIter = sysinfo.getAnalogInCalBList().iterator();
        Iterator<Float> calMIter = sysinfo.getAnalogInCalMList().iterator();
        Iterator<Float> analogInPortRangeIter = sysinfo.getAnalogInPortRangeList().iterator();
        Iterator<Float> analogInIntScaleMListIter = sysinfo.getAnalogInIntScaleMList().iterator();

        Iterator<ChannelInterface> channelIter = Channel.filter(device.getChannels(), ChannelInterface.Type.ANALOG_IN).iterator();
        while(channelIter.hasNext() && calMIter.hasNext() && calBIter.hasNext() && analogInPortRangeIter.hasNext() && analogInIntScaleMListIter.hasNext()){
            AnalogInputChannel ch = (AnalogInputChannel)channelIter.next();
            ch.setCalibrationValues(analogInPortRangeIter.next(), analogInIntScaleMListIter.next(),calMIter.next(), calBIter.next());
        }

        //
        // WiFi Networks:
        //
        Iterator<String> ssidIter = sysinfo.getAvSsidList().iterator();
        Iterator<Integer> ssidStrength = sysinfo.getAvSsidStrengthList().iterator();
        Iterator<Integer> ssidSecurityMode = sysinfo.getAvWifiSecurityModeList().iterator();

        ArrayList<DeviceInterface.AvailableWifiNetwork> availableWifiNetworks = new ArrayList<>(sysinfo.getAvSsidCount());
        while (ssidIter.hasNext() && ssidStrength.hasNext() && ssidSecurityMode.hasNext()){
            availableWifiNetworks.add(new DeviceInterface.AvailableWifiNetwork(ssidIter.next(), ssidStrength.next(), ssidSecurityMode.next()));
        }
        device.setAvailableWifiNetworks(availableWifiNetworks);

        return device;
    }

    public static DeviceInterface getDevice(String deviceType, String host, int port)
            throws InvalidDeviceType {
        DeviceInterface device = null;
        try {
            Class deviceClass = deviceClasses.get(deviceType);
            device = (DeviceInterface) deviceClass.newInstance();
        } catch (InstantiationException e) {
            throw new InvalidDeviceType(deviceType);
        } catch (IllegalAccessException e) {
            throw new InvalidDeviceType(deviceType);
        }


        device.setDeviceName(host);
        InetSocketAddress address = InetSocketAddress.createUnresolved(host, port);
        device.setNetworkAddress(address);
        device.setMacAddress("00:00:00:00:00:00");

        return device;
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

    public static class InvalidDeviceType extends Exception {
        public InvalidDeviceType(String type) {

        }
    }
}
