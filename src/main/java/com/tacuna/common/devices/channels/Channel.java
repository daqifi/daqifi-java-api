package com.tacuna.common.devices.channels;

import com.tacuna.common.devices.DeviceInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Created by Marc on 1/10/15.
 */
public abstract class Channel implements ChannelInterface {

    public static Collection<ChannelInterface> filter(Collection<ChannelInterface> target, Type type) {
        Collection<ChannelInterface> result = new ArrayList<ChannelInterface>();
        for (ChannelInterface element : target) {
            if (element.getType() == type) {
                result.add(element);
            }
        }
        return result;
    }

    public static Collection<ChannelInterface> filterActive(Collection<ChannelInterface> target) {
        Collection<ChannelInterface> result = new ArrayList<ChannelInterface>();
        for (ChannelInterface element : target) {
            if (element.isActive()) {
                result.add(element);
            }
        }
        return result;
    }

    public static Collection<ChannelInterface> filterNames(Collection<ChannelInterface> target, Set<String> names) {
        Collection<ChannelInterface> result = new ArrayList<ChannelInterface>();
        for (ChannelInterface element : target) {
            if (names.contains(element.getName())) {
                result.add(element);
            }
        }
        return result;
    }

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

    /**
     * Channel decorator. The intent of this class is to be the base for any classes
     * that wish to decorate the channelIndex interface. See the Gamma, et al.
     * Design Patterns for more on the decorator pattern.
     *
     * @author Marc
     */
    public static class ChannelDecorator implements ChannelInterface {

        private final ChannelInterface channel;

        /**
         * @param channel
         */
        public ChannelDecorator(ChannelInterface channel) {
            super();
            this.channel = channel;
        }

        @Override
        public Type getType() {
            return channel.getType();
        }

        @Override
        public String getName() {
            return channel.getName();
        }

        @Override
        public void setName(String name) {

        }

        @Override
        public void setDevice(DeviceInterface device) {
            channel.setDevice(device);
        }

        @Override
        public DeviceInterface getDevice() {
            return channel.getDevice();
        }

        @Override
        public int getNumberOfSamples() {
            return channel.getNumberOfSamples();
        }

        @Override
        public String getUnit() {
            return channel.getUnit();
        }

        @Override
        public void setUnit(String name) {

        }

        @Override
        public int getDeviceIndex() {
            return channel.getDeviceIndex();
        }

        @Override
        public boolean isActive() {
            return this.channel.isActive();
        }

        @Override
        public void setActive(boolean value) {
            this.channel.setActive(value);
        }

        @Override
        public int getDisplayColor() {
            return channel.getDisplayColor();
        }

        @Override
        public void setDisplayColor(int color) {
            channel.setDisplayColor(color);
        }

        @Override
        public int getPlotNumber() {
            return channel.getPlotNumber();
        }

        @Override
        public void setPlotNumber(int color) {
            channel.setPlotNumber(color);
        }

    }
}
