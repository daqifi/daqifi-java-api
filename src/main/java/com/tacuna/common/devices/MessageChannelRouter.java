// Copyright 2013 Marc Bernardini.
package com.tacuna.common.devices;

import com.lp.io.MessageConsumer;
import com.lp.io.messages.MeasurementMessage;
import com.lp.io.messages.SimpleProtobufMessage;
import com.tacuna.common.components.DataBuffer;
import com.tacuna.common.devices.channels.AnalogInputChannel;
import com.tacuna.common.devices.channels.AnalogMathInputChannel;
import com.tacuna.common.devices.channels.ChannelInterface;
import com.tacuna.common.devices.channels.DigitalInputChannel;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Message consumer class that takes SimpleDeviceMessage and adds the value to
 * the channel associated with that message.
 *
 * @author Marc
 */
public class MessageChannelRouter implements
        MessageConsumer<SimpleProtobufMessage> {

    private Collection<ChannelInterface> aiChannels = null;
    private Collection<ChannelInterface> diChannels = null;
    private Collection<ChannelInterface> mathInputChannels = null;
    private DeviceInterface device;
    private int numberOfMessages = 0;
    private ArrayList<DataBuffer> buffers = new ArrayList<DataBuffer>();
    private Object bufferLock = new Object();

    /**
     * Creates the message channelIndex router using the given the channelIndex
     * list.
     *
     * @param aiChannels
     * @param diChannels
     */
    MessageChannelRouter(Collection<ChannelInterface> aiChannels,
                         Collection<ChannelInterface> diChannels,
                         Collection<ChannelInterface> mathChannels,
                         DeviceInterface device) {
        this.aiChannels = aiChannels;
        this.diChannels = diChannels;
        this.mathInputChannels = mathChannels;
        this.device = device;
    }

    @Override
    public void onMessage(final SimpleProtobufMessage msg) {
        final MeasurementMessage measurement = new MeasurementMessage(null);
        int activeIndex = 0;
        int index = 0;
        int adcRange = device.getAdcResolution();
        float[] data = new float[device.getNumberOfAnalogInChannels()];
        for (ChannelInterface c : aiChannels) {
            if (c.isActive()) {

                float value = (float) ((AnalogInputChannel) c).convert(msg.getAnalogInValue(activeIndex), adcRange);
                ((AnalogInputChannel) c).add(measurement.getTimestamp(), value);
                activeIndex++;

                data[index] = value;
            }

            index++;

        }
        measurement.analogData = data;
        measurement.timestamp = msg.getDeviceTimestamp();


        for (ChannelInterface c : diChannels) {
            DigitalInputChannel di = (DigitalInputChannel) c;
            byte dvalue = msg.getDigitalInValue(di.getByteIndex());
            di.add(msg.getDeviceTimestamp(), dvalue);
        }
        measurement.digitalData = msg.getDigitalData();
        measurement.deviceId = device.getDeviceId();

        for(ChannelInterface c: mathInputChannels){
            AnalogMathInputChannel mathCh = (AnalogMathInputChannel) c;
            mathCh.computeCurrent(msg.getTimestamp());

            measurement.analogData[mathCh.getDeviceIndex()] = mathCh.getCurrentValue();
        }

        synchronized (bufferLock) {
            for (DataBuffer buffer : buffers) {
                buffer.push(measurement);
            }
        }

        numberOfMessages++;
    }

    /**
     * @return the numberOfMessages
     */
    public int getNumberOfMessages() {
        return numberOfMessages;
    }

    /**
     * @return the numberOfMessages
     */
    public void setNumberOfMessages(int value) {
        numberOfMessages = value;
    }

    public void addOutputBuffer(DataBuffer buffer) {
        synchronized (bufferLock) {
            buffers.add(buffer);
        }
    }

    public boolean removeOutputBuffer(DataBuffer buffer) {
        synchronized (bufferLock) {
            return buffers.remove(buffer);
        }
    }
}
