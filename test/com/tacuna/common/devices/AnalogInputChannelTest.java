// Copyright 2013 Marc Bernardini.
package com.tacuna.common.devices;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.tacuna.common.devices.channels.AnalogInputChannel;
import com.tacuna.common.devices.channels.ChannelInterface;

public class AnalogInputChannelTest {

    @Test
    public void testGetLastValue() {
	AnalogInputChannel test = new AnalogInputChannel("TestChannel", 1);
	test.add(0, 0.12345f);
	assertEquals(0.12345f, test.getCurrentValue(), 0.0001);
    }

    @Test
    public void testGetType() {
	AnalogInputChannel test = new AnalogInputChannel("TestChannel", 1);
	assertEquals(ChannelInterface.Type.ANALOG_IN, test.getType());
    }

    @Test(expected = com.tacuna.common.devices.channels.ChannelInterface.NotSupportedException.class)
    public void testSetValue() throws Exception {
	AnalogInputChannel test = new AnalogInputChannel("TestChannel", 1);
	test.add(1, 1.23f);
    }

    @Test
    public void testAdd() {
	AnalogInputChannel test = new AnalogInputChannel("TestChannel", 1);

	test.add(1, 2.34f);

	assertEquals(2.34, test.getCurrentValue(), 0.001);
	assertEquals(2.34, test.getMaximum(), 0.001);
	assertEquals(2.34, test.getMinimum(), 0.001);
    }

    @Test
    public void testAddAndGetMultipleValues() {
	AnalogInputChannel test = new AnalogInputChannel("TestChannel", 1);

	test.add(1, 2.34f);
	test.add(2, 3.45f);
	test.add(3, 4.56f);

	assertEquals(test.getCurrentValue(), 4.56, 0.001);
	assertEquals(test.getMaximum(), 4.56, 0.001);
	assertEquals(test.getMinimum(), 2.34, 0.001);

	assertEquals(test.getIndex(0).value, 2.34, 0.001);
	assertEquals(test.getIndex(1).value, 3.45, 0.001);
	assertEquals(test.getIndex(2).value, 4.56, 0.001);
    }
}
