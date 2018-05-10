package com.daqifi.common.components;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.lp.io.SocketConnector;
import com.lp.io.UdpBroadcast;
import com.daqifi.common.devices.AD7195W;
import com.daqifi.common.devices.channels.ChannelInterface;
import com.daqifi.common.devices.DeviceCommandSchedule;
import com.daqifi.common.devices.DeviceInterface;
import com.daqifi.common.devices.MessageChannelRouter;

/**
 * Singleton connection manager class. Manages connections and their data
 * interpreters. A singleton instance is used so that connections persist
 * between views. This Singleton uses the singleton enum pattern.
 *
 * @author marc
 */
public class ConnectionManager implements PropertyChangeListener {
  /**
   * The singleton instance of the ConnectionManagerAndriod
   */
  public static ConnectionManager INSTANCE =  new ConnectionManager();
  private static Logger log = Logger.getLogger(MessageChannelRouter.class
          .getName());

  public ConnectionManager(){

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

  public final ArrayList<ChannelInterface> activeChannelsList = new ArrayList<ChannelInterface>();

  public void addChannel(ChannelInterface channel) {
    if (!activeChannelsList.contains(channel)) {
      activeChannelsList.add(channel);
      channel.setActive(true);
    }
  }

  public void removeChannel(ChannelInterface channel) {
    if (activeChannelsList.contains(channel)) {
      activeChannelsList.remove(channel);
      channel.setActive(false);
    } else {
      log.warning("Channel not found in activeChannelsList");
    }
  }

  public Collection<ChannelInterface> getNonActiveChannels() {
    ArrayList<ChannelInterface> channels = new ArrayList<ChannelInterface>();
    for (DeviceInterface device : knownDevices) {
      if (device.isConnected()) {
        Collection<ChannelInterface> dc = device.getChannels();
        for (ChannelInterface ch : dc) {
          if (!activeChannelsList.contains(ch)) {
            channels.add(ch);
          }
        }
      }
    }

    return channels;
  }

  private final HashMap<String, DeviceCommandSchedule> deviceSchedules = new HashMap<String, DeviceCommandSchedule>();

  public DeviceCommandSchedule getScheduleByDeviceName(String device) {
    return deviceSchedules.get(device);
  }

  /**
   * Socket connection factory method.
   *
   * @param host
   * @param port
   * @return
   */
  public SocketConnector createConnection(final String host, int port) {
    // Attempt connection.
    try {
      log.info(String.format("Creating connection to %s:%d", host, port));
      DeviceInterface lastDevice = new AD7195W();
      lastDevice.setNetworkAddress(InetSocketAddress.createUnresolved(
              host, port));
      lastDevice.connect();
      SocketConnector connection = lastDevice.getConnection();
      connection.addChangeListener(this);
      DeviceCommandSchedule schedule = new DeviceCommandSchedule(
              lastDevice);
      deviceSchedules.put(lastDevice.getDeviceName(), schedule);
      return connection;
    } catch (final Exception err) {
      log.warning("Unable to create connection. Exception:"
              + err.toString());
    }
    return null;
  }

  /**
   * Connects the device that is passed in and configures it.
   *
   * @param device
   * @return
   */
  public SocketConnector createConnection(DeviceInterface device) {
    device.connect();
    SocketConnector connection = device.getConnection();
    connection.addChangeListener(this);
    DeviceCommandSchedule schedule = new DeviceCommandSchedule(device);
    deviceSchedules.put(device.getDeviceName(), schedule);
    return connection;
  }

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
    activeChannelsList.clear();
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

  @Override
  public void propertyChange(PropertyChangeEvent event) {
    // notifyListeners("connection", null, connection);
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
