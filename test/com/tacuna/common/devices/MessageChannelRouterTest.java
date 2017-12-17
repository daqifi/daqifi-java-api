// Copyright 2013 Marc Bernardini.
package com.tacuna.common.devices;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import com.tacuna.common.devices.channels.AnalogInputChannel;
import com.tacuna.common.devices.channels.ChannelInterface;

public class MessageChannelRouterTest {
    @Test
    public void testOnMessage() {

	AnalogInputChannel channel0 = new AnalogInputChannel("A0", 0);
	AnalogInputChannel channel1 = new AnalogInputChannel("A1", 1);
	ArrayList<ChannelInterface> channels = new ArrayList<ChannelInterface>(
		2);
	channels.add(channel0);
	channels.add(channel1);

	MessageChannelRouter router = new MessageChannelRouter(channels, null);
	// router.onMessage(new SimpleDeviceMessage(123456, channel0,
	// 0.298348957));
	// router.onMessage(new SimpleDeviceMessage(123456, channel1,
	// 1.983489574));

	assertEquals(0.298348957, channel0.getCurrentValue(), 0.0001);
	assertEquals(1.983489574, channel1.getCurrentValue(), 0.0001);
    }

    @Test
    public void testOnMessageInvalidChannel() {

	AnalogInputChannel channel1 = new AnalogInputChannel("A1", 1);
	ArrayList<ChannelInterface> channels = new ArrayList<ChannelInterface>(
		2);
	channels.add(channel1);

	MessageChannelRouter router = new MessageChannelRouter(channels, null);
	// router.onMessage(new SimpleDeviceMessage("2,123456,0.298348957"));

	assertEquals(channel1.getCurrentValue(), Float.NaN, 0.0001);
    }
}
