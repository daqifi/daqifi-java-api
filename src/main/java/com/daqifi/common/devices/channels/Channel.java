package com.daqifi.common.devices.channels;

import com.daqifi.common.devices.DeviceInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Abstract class that implement several common methods of a ChannelInterface
 */
public abstract class Channel implements ChannelInterface {

    /**
     * Filters a collection of Channels for a given channel Type
     * @param target
     * @param type
     * @return
     */
    public static Collection<ChannelInterface> filter(Collection<ChannelInterface> target, Type type) {
        Collection<ChannelInterface> result = new ArrayList<ChannelInterface>();
        for (ChannelInterface element : target) {
            if (element.getType() == type) {
                result.add(element);
            }
        }
        return result;
    }

    /**
     * Filters a collection of Channels for a active channels
     * @param target
     * @return
     */
    public static Collection<ChannelInterface> filterActive(Collection<ChannelInterface> target) {
        Collection<ChannelInterface> result = new ArrayList<ChannelInterface>();
        for (ChannelInterface element : target) {
            if (element.isActive()) {
                result.add(element);
            }
        }
        return result;
    }

    /**
     * Filters a collection of Channels for a channel with a matching name
     * @param target
     * @param names
     * @return
     */
    public static Collection<ChannelInterface> filterNames(Collection<ChannelInterface> target, Set<String> names) {
        Collection<ChannelInterface> result = new ArrayList<ChannelInterface>();
        for (ChannelInterface element : target) {
            if (names.contains(element.getName())) {
                result.add(element);
            }
        }
        return result;
    }

    /**
     * Filters a collection of Channels for a given channel index
     * @param target
     * @param index
     * @return
     */
    public static Collection<ChannelInterface> filterChannelIndex(Collection<ChannelInterface> target, int index) {
        Collection<ChannelInterface> result = new ArrayList<ChannelInterface>();
        for (ChannelInterface element : target) {
            if (element.getDeviceIndex() == index) {
                result.add(element);
            }
        }
        return result;
    }

    public static int COLOR_NOT_SET = Integer.MAX_VALUE;

    private String name;
    private int deviceIndex;
    private String unit;
    private int displayColor = COLOR_NOT_SET;
    private DeviceInterface device;
    private boolean active;
    private int plotNumber = 0;

    protected Channel(String name, int deviceIndex, String unit, DeviceInterface device) {
        this.name = name;
        this.deviceIndex = deviceIndex;
        this.unit = unit;
        this.device = device;
        this.active = false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getDeviceIndex() {
        return deviceIndex;
    }

    @Override
    public String getUnit() {
        return unit;
    }

    @Override
    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public int getDisplayColor() {
        return displayColor;
    }

    @Override
    public void setDisplayColor(int color) {
        displayColor = color;
    }

    @Override
    public int getPlotNumber() {
        return plotNumber;
    }

    @Override
    public void setPlotNumber(int number) {
        this.plotNumber = number;
    }

    @Override
    public void setDevice(DeviceInterface device) {
        this.device = device;
    }

    @Override
    public DeviceInterface getDevice() {
        return device;
    }

    @Override
    public int getNumberOfSamples() {
        return 0;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean value) {
        this.active = value;
    }
}
