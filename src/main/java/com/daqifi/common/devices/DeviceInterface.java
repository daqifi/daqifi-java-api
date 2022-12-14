// Copyright 2013 Marc Bernardini.
package com.daqifi.common.devices;

import com.daqifi.common.devices.channels.AnalogInputChannel;
import com.daqifi.common.devices.channels.AnalogOutputChannel;
import com.daqifi.common.devices.channels.DigitalInputChannel;
import com.daqifi.io.MessageProducer;
import com.daqifi.io.SocketConnector;
import com.daqifi.common.components.DataBuffer;
import com.daqifi.common.devices.channels.ChannelInterface;
import com.daqifi.common.devices.scpi.Command;

import java.net.InetSocketAddress;
import java.util.Collection;

/**
 * The Device interface is used as the interface for interacting with the actual
 * DAQ.
 *
 * @author marc
 */
public interface DeviceInterface extends MessageProducer {

    enum Direction {
        Input,
        Output
    }

    /**
     * POJO for holding Available Wifi networks
     */
    class AvailableWifiNetwork {
        public final String ssid;
        public final int strength;
        public final int securityMode;

        public AvailableWifiNetwork(String ssid, int strength, int securityMode) {
            this.ssid = ssid;
            this.strength = strength;
            this.securityMode = securityMode;
        }
    }

    /**
     * Returns the type of the device. TBD on what this will actually be but it
     * should unique identify the type of device such that the application can
     * use this to determine device characteristics.
     *
     * @return
     */
    String getDeviceType();

    /**
     * The name of the device.
     *
     * @return
     */
    String getDeviceName();

    int getDeviceId();

    /**
     * Sets the device name.
     *
     * @param name
     */
    void setDeviceName(final String name);

    /**
     * The MAC address of the device.
     *
     * @return
     */
    String getMacAddress();

    /**
     * Sets the MAC address of the device.
     *
     * @param macAddress
     */
    void setMacAddress(final String macAddress);

    /**
     * Returns the device channels.
     * <p></p>
     *
     * @return
     */
    Collection<ChannelInterface> getChannels();

    /**
     * Helper method that returns the first channel with a name that matches the input name.
     * If no channel matches the name, null is returned.
     * @param name
     * @return channel with matching name or null
     */
    ChannelInterface getChannelByName(String name);

    /**
     * Helper method that returns the AnalogInputChannel that matches the passed in device index.
     *  The device index is 0 based.
     * @param index
     * @return channel or null
     */
    AnalogInputChannel getAnalogInChannelByIndex(int index);

    /**
     * Helper method that returns the AnalogOutputChannel that matches the passed in device index.
     *  The device index is 0 based. For devices that do not have analog output channels, this method always returns null.
     * @param index
     * @return channel or null
     */
    AnalogOutputChannel getAnalogOutChannelByIndex(int index);

    /**
     * Helper method that returns the DigitalInputChannel that matches the passed in device index.
     *  The device index is 0 based. For devices that do not have dio channels, this method always returns null.
     * @param index
     * @return channel or null
     */
    DigitalInputChannel getDigitalIOChannelByIndex(int index);

    /**
     * Returns the total number of channels including synthetic channels
     *
     * @return
     */
    int getNumberOfChannels();

    /**
     * Returns the number of analog input channels.
     *
     * @return
     */
    int getNumberOfAnalogInChannels();

    /**
     * Returns the number of analog out channels.
     *
     * @return
     */
    int getNumberOfAnalogOutChannels();

    /**
     * Returns the number of digital in channels.
     *
     * @return
     */
    int getNumberOfDigitalInChannels();

    /**
     * Returns the network address of the device.
     *
     * @return
     */
    InetSocketAddress getNetworkAddress();

    /**
     * Sets the network address of this Device object. To convert from host name
     * and port to InetSocketAddress using the static method of
     * InetSocketAddress class. Example: <code>
     * InetSocketAddress address = InetSocketAddress.createUnresolved(hostname,port);
     * </code>
     *
     * @param networkAddress
     */
    void setNetworkAddress(final InetSocketAddress networkAddress);

    /**
     * Sets the device sample frequency. The sample frequency is used at the
     * start of streaming. Changing this value once streaming has started has no
     * effect.
     *
     * @param frequency
     */
    void setSampleFrequency(int frequency);

    int getSampleFrequency();

    void setTimestampFrequency(int frequency);
    int getTimestampFrequency();

    /**
     * Sets the ADC resolution (device voltage range). This is a selector and
     * can be 1 (-10 to +10) or 0 (-5 to +5).
     *
     * @param range
     */
    void setAdcResolution(int range);

    int getAdcResolution();

    /**
     * Sets the DI channel streaming.
     *
     * @param diStreaming
     */
    void setDiStreaming(boolean diStreaming);
    boolean isDiStreaming();

    /**
     * Connects to the physical device using the network address
     */
    void connect();

    /**
     * Disconnects the device
     */
    void disconnect();

    /**
     * Sends a command to the device, if the device is connected.
     *
     * @param command
     */
    void send(Command command);

    /**
     * Returns the devices connection object or null if the device has not been
     * connected.
     *
     * @return SocketConnector for the device
     */
    SocketConnector getConnection();

    /**
     * Returns the battery charge percentage
     * @return
     */
    int getBatteryCharge();
    void setBatteryCharge(int charge);

    enum PowerStatus {
        UNKNOWN,
        BATTERY,
        USB
    }

    /**
     * Returns the devices power status.
     * @return
     */
    PowerStatus getPowerStatus();
    void setPowerStatus(PowerStatus status);

    void startStreaming();

    void stopStreaming();

    boolean isStreaming();

    boolean isConnected();

    void updateNetworkSettings(String ssid, int securityType, String password);
    Collection<AvailableWifiNetwork> getAvailableWifiNetworks();
    void setAvailableWifiNetworks(final Collection<AvailableWifiNetwork> networks);

    /**
     * Adds a buffer that parsed messages will be added to.
     *
     * @param buffer
     */
    void addOutputBuffer(DataBuffer buffer);

    void removeOutputBuffer(DataBuffer buffer);

    /**
     * Returns a unique hashCode for the device. This method must be implemented
     * such that the same physical device produces the same hashCode across all
     * DeviceInterface instances.
     */
    @Override
    int hashCode();
}
