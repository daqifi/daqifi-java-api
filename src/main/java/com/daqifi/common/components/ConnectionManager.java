package com.daqifi.common.components;

import com.daqifi.common.devices.DeviceFactory;
import com.daqifi.common.devices.DeviceInterface;
import com.daqifi.io.MessageConsumer;
import com.daqifi.io.UdpBroadcast;
import com.daqifi.io.messages.DeviceBroadcastMessage;
import com.daqifi.io.messages.Message;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Singleton connection manager class. Manages connections and their data
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
     * UDP response port. Note that for now this must be the same as the
     * listening port due to lastDevice limitations.
     */
    public static final int RESPONSE_PORT = 30303;

    private InetAddress broadcastAddress = null;

    private UdpBroadcast broadcaster;

    /**
     * A set that contains all of the known devices.
     */
    public final Set<DeviceInterface> knownDevices = Collections
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
                        Iterator<DeviceInterface> iter = knownDevices.iterator();
                        while (iter.hasNext()) {
                            DeviceInterface knownDevice = iter.next();
                            DeviceFactory.setDeviceStatus(dbm.getMessage(), knownDevice);
                        }
                    }
                }
            });
        }
        return broadcaster;
    }

    /**
     * Returns the broadcast address used on the current network. If this has
     * not been set, the default address of 255.255.255.255
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
    }

    /**
     * Closes the connections. Right now there is only one connection but that
     * may change.
     */
    public void closeAll() {
        for (DeviceInterface device : knownDevices) {
            if (device.isConnected()) {
                device.disconnect();
            }
        }
    }

    /**
     * In order to make the UDP broad cast listen in the background we have to
     * run it in its own thread. Originally I had set this up to just be an
     * AsynchTask but that doesn't work for blocking calls (like receive) on
     * Android 3.+ due to the API running all asynch tasks on a single thread.
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

    private final ArrayList<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();

    /**
     * Register a change listener for receiving state change updates.
     *
     * @param listener
     */
    public void addChangeListener(PropertyChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a previously registered listener.
     *
     * @param listener
     */
    public void removeChangeListener(PropertyChangeListener listener) {
        listeners.remove(listener);
    }

    protected void notifyListeners(String property, Object oldValue,
                                   Object newValue) {
        for (PropertyChangeListener listener : listeners) {
            listener.propertyChange(new PropertyChangeEvent(this, property,
                    oldValue, newValue));
        }
    }
}
