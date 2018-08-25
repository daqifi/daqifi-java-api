package com.daqifi.common.devices;

import com.daqifi.common.devices.channels.AnalogInputChannel;
import com.daqifi.common.devices.channels.AnalogMathInputChannel;
import com.daqifi.common.devices.channels.AnalogOutputChannel;
import com.daqifi.common.devices.channels.Channel;
import com.daqifi.common.devices.channels.ChannelInterface;
import com.daqifi.common.devices.channels.DigitalInputChannel;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Abstract Device class. Contains common method implementations used on all
 * devices.
 */
public abstract class Device implements DeviceInterface {

    /**
     * Filter streaming devices from a collection of DeviceInterface's
     * @param target
     * @return streaming devices
     */
    public static Collection<DeviceInterface> filterStreaming(Collection<DeviceInterface> target) {
        Collection<DeviceInterface> result = new ArrayList<DeviceInterface>();
        for (DeviceInterface element : target) {
            if (element.isStreaming()) {
                result.add(element);
            }
        }
        return result;
    }

    /**
     * Constant for the default streaming rate.
     */
    public static final int DEFAULT_SAMPLES_PER_SECOND = 10;

    public static final int DEFAULT_DEVICE_TIMESTAMP_FREQUENCY = 50000000;

    private String deviceName;
    private String macAddress;
    private InetSocketAddress address;
    private int battery = 0;
    private PowerStatus powerStatus = PowerStatus.UNKNOWN;

    private int adcRes = 0;
    private int deviceTimestampFreq = DEFAULT_DEVICE_TIMESTAMP_FREQUENCY;
    private Collection<AvailableWifiNetwork> availableWifiNetworks = Collections.emptyList();

    @Override
    public boolean isDiStreaming() {
        return diStreaming;
    }

    @Override
    public void setDiStreaming(boolean diStreaming) {
        this.diStreaming = diStreaming;
    }

    private boolean diStreaming = true;

    @Override
    public String getDeviceName() {
        return deviceName;
    }

    @Override
    public int getDeviceId() {
        return getMacAddress().hashCode();
    }

    @Override
    public void setDeviceName(String name) {
        this.deviceName = name;
    }

    @Override
    public String getMacAddress() {
        return macAddress;
    }

    @Override
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    @Override
    public InetSocketAddress getNetworkAddress() {
        return address;
    }

    @Override
    public void setNetworkAddress(InetSocketAddress networkAddress) {
        this.address = networkAddress;
    }

    @Override
    public int getTimestampFrequency() {
        return deviceTimestampFreq;
    }

    @Override
    public void setTimestampFrequency(int frequency) {
        deviceTimestampFreq = frequency;
    }

    @Override
    public void setAdcResolution(int resolution) {
        this.adcRes = resolution;
    }

    @Override
    public int getAdcResolution() {
        return adcRes;
    }

    @Override
    public int getBatteryCharge() {
        return battery;
    }

    @Override
    public void setBatteryCharge(int charge) {
        this.battery = charge;
    }

    @Override
    public PowerStatus getPowerStatus() {
        return powerStatus;
    }

    @Override
    public void setPowerStatus(PowerStatus status) {
        this.powerStatus = status;
    }

    public void setIsStreaming(boolean isStreaming) {
        this.isStreaming = isStreaming;
    }

    private boolean isStreaming = false;

    @Override
    public boolean isStreaming() {
        return isStreaming;
    }

    protected List<ChannelInterface> analogMathChannels = new ArrayList<ChannelInterface>();

    public void addMathChannel(ChannelInterface ch) {
        if(ch instanceof AnalogMathInputChannel) {
            analogMathChannels.add(ch);
        } else {
            throw new IllegalArgumentException("Channel must be an AnalogMathInputChannel");
        }
    }

    public int getNumberOfMathChannels() {
        return analogMathChannels.size();
    }

    @Override
    public ChannelInterface getChannelByName(String name){
        Iterator<ChannelInterface> chIter = this.getChannels().iterator();
        while(chIter.hasNext()) {
            ChannelInterface ci = chIter.next();
            if(name.equals(ci.getName())){
                return ci;
            }
        }
        return null;
    }

    @Override
    public AnalogInputChannel getAnalogInChannelByIndex(int index){
        Iterator<ChannelInterface> chIter = Channel.filter(this.getChannels(), ChannelInterface.Type.ANALOG_IN).iterator();
        while(chIter.hasNext()) {
            ChannelInterface ci = chIter.next();
            if(ci.getDeviceIndex() == index){
                return (AnalogInputChannel) ci;
            }
        }
        return null;
    }

    @Override
    public AnalogOutputChannel getAnalogOutChannelByIndex(int index){
        Iterator<ChannelInterface> chIter = Channel.filter(this.getChannels(), ChannelInterface.Type.ANALOG_OUT).iterator();
        while(chIter.hasNext()) {
            ChannelInterface ci = chIter.next();
            if(ci.getDeviceIndex() == index){
                return (AnalogOutputChannel) ci;
            }
        }
        return null;
    }

    @Override
    public DigitalInputChannel getDigitalIOChannelByIndex(int index){
        Iterator<ChannelInterface> chIter = Channel.filter(this.getChannels(), ChannelInterface.Type.DIGITAL_IO).iterator();
        while(chIter.hasNext()) {
            ChannelInterface ci = chIter.next();
            if(ci.getDeviceIndex() == index){
                return (DigitalInputChannel) ci;
            }
        }
        return null;
    }


    @Override
    public int getNumberOfChannels() {
        return getChannels().size();
    }

    @Override
    public boolean isConnected() {
        return (getConnection() == null) ? false
                : getConnection().isConnected();
    }

    @Override
    public Collection<AvailableWifiNetwork> getAvailableWifiNetworks() {
        return availableWifiNetworks;
    }

    @Override
    public void setAvailableWifiNetworks(Collection<AvailableWifiNetwork> networks) {
        availableWifiNetworks = networks;
    }

    protected int getActiveChannelMask() {
        int activeChannelBitMask = 0;
        Collection<ChannelInterface> channels = getChannels();
        for (ChannelInterface ch : channels) {
            if (ch.isActive()) {
                int channelMask = 1 << ch.getDeviceIndex();
                activeChannelBitMask = activeChannelBitMask | channelMask;
            }
        }
        return activeChannelBitMask;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((address == null) ? 0 : address.hashCode());
        result = prime * result
                + ((deviceName == null) ? 0 : deviceName.hashCode());
        result = prime * result
                + ((macAddress == null) ? 0 : macAddress.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Device other = (Device) obj;
        if (address == null) {
            if (other.address != null)
                return false;
        } else if (!address.equals(other.address))
            return false;
        if (deviceName == null) {
            if (other.deviceName != null)
                return false;
        } else if (!deviceName.equals(other.deviceName))
            return false;
        if (macAddress == null) {
            if (other.macAddress != null)
                return false;
        } else if (!macAddress.equals(other.macAddress))
            return false;
        return true;
    }

}
