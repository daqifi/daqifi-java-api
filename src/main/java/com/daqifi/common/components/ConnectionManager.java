package com.daqifi.common.components;

import com.daqifi.common.devices.DeviceFactory;
import com.daqifi.common.devices.DeviceInterface;
import com.daqifi.io.MessageConsumer;
import com.daqifi.io.UdpBroadcast;
import com.daqifi.io.messages.DeviceBroadcastMessage;
import com.daqifi.io.messages.Message;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Connection manager class. Manages connections and their data
 * interpreters. A singleton instance is used so that connections persist
 * between views. This Singleton uses the singleton enum pattern.
 *
 * @author marc
 */
public class ConnectionManager {

    private static Logger log = Logger.getLogger(ConnectionManager.class
            .getName());

    public ConnectionManager() {

    }

    /**
     * This is the devices UDP listening port.
     */
    public static final int DEVICE_LISTENING_PORT = 30303;
    /**
     * UDP response port.
     */
    public static final int RESPONSE_PORT = 30303;

    private InetAddress broadcastAddress = null;

    private UdpBroadcast broadcaster;

    /**
     * Returns the set of known devices.
     */
    public Set<DeviceInterface> getKnownDevices() {
        return knownDevices;
    }


    private final Set<DeviceInterface> knownDevices = Collections
            .synchronizedSet(new HashSet<DeviceInterface>());

    /**
     * Returns the UdpBroadcaster. This method will create the broadcaster the
     * first timestamp it is called.
     *
     * @return UdpBroadcast
     * @throws IOException
     */
    public UdpBroadcast getBroadcaster() throws IOException {
        if (broadcaster == null) {
            broadcaster = new UdpBroadcast(RESPONSE_PORT, getBroadcastAddress());
            new UdpListenThread(broadcaster);
            broadcaster.registerObserver(new MessageConsumer() {
                @Override
                public void onMessage(Message message) {
                    DeviceBroadcastMessage dbm = (DeviceBroadcastMessage) message;
                    DeviceInterface device = DeviceFactory
                            .getDevice(dbm);
                    if (!knownDevices.add(device)) {
                        DeviceInterface knownDevice = getDeviceByAddress(device.getNetworkAddress());
                        DeviceFactory.setDeviceStatus(dbm.getMessage(), knownDevice);
                    }
                }
            });
        }
        return broadcaster;
    }

    /**
     * Returns the broadcast address used on the current network. If this has
     * not been set, the default address of 255.255.255.255. The default broadcast
     * address will typically work for simple networks and on home WiFi networks. However,
     * some networks require broadcasting using the networks local broadcast address (e.g. 192.168.1.255)
     *
     * @return InetAdress for broadcasts
     * @throws IOException
     */
    public InetAddress getBroadcastAddress() throws IOException {
        if (broadcastAddress == null) {
            broadcastAddress = InetAddress.getByName("255.255.255.255");
        }
        return broadcastAddress;
    }

    /**
     * Sets the broadcast address to be used by the application.
     *
     * @param address
     */
    public void setBroadcastAddress(InetAddress address) {
        broadcastAddress = address;
        if(broadcaster != null){
            broadcaster.setBroadcastAddr(address);
        }
    }

    /**
     * Closes the connections that were made using this instance of the ConnectionManager.
     */
    public void closeAll() {
        for (DeviceInterface device : knownDevices) {
            if (device.isConnected()) {
                device.disconnect();
            }
        }
    }

    /**
     * Returns the device with the given InetSocketAddress
     * @param address Address to search for
     * @return device If found. Null if no matching device found.
     */
    public DeviceInterface getDeviceByAddress(InetSocketAddress address){
        Iterator<DeviceInterface> iter = knownDevices.iterator();
        while (iter.hasNext()) {
            DeviceInterface knownDevice = iter.next();
            if(knownDevice.getNetworkAddress().equals(address)) {
                return knownDevice;
            }
        }
        return null;
    }

    /**
     * Thread used for for listening for UDP Broadcast responses.
     */
    protected class UdpListenThread extends Thread {
        /**
         * Constructor. Takes in the UdpBroadcast instance and runs it in its
         * own thread.
         *
         * @param broadcaster
         */
        UdpListenThread(UdpBroadcast broadcaster) {
            super(broadcaster);
            start();
        }
    }
}
