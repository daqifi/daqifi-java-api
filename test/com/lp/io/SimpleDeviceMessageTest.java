package com.lp.io;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import main.java.com.daqifi.io.messages.SimpleDeviceMessage;

public class SimpleDeviceMessageTest {

    @Test
    public void testChannelSet() {
	SimpleDeviceMessage msg = new SimpleDeviceMessage(
		"1,123456,0.298348957");
	assertEquals(1, msg.getChannelIndex());
    }

    @Test
    public void testValueSet() {
	SimpleDeviceMessage msg = new SimpleDeviceMessage(
		" 1, 123456,  0.298348957");
	assertEquals(0.298348757, msg.getValue(), 0.00001);
    }

    @Test(expected = NumberFormatException.class)
    public void testIncorrectFormat() {
	SimpleDeviceMessage msg = new SimpleDeviceMessage("");
	assertEquals(-1, msg.getChannel());
	assertEquals(0.0, msg.getValue(), 0.0001);
    }

    @Test(expected = NumberFormatException.class)
    public void testIncorrectFormatDos() {
	SimpleDeviceMessage msg = new SimpleDeviceMessage("ksursfg");
	assertEquals(-1, msg.getChannel());
	assertEquals(0.0, msg.getValue(), 0.0001);
    }

    @Test(expected = NumberFormatException.class)
    public void testIncorrectFormatTres() {
	SimpleDeviceMessage msg = new SimpleDeviceMessage(
		"1.0hj,b0.34sf56;29,f");
	assertEquals(0, msg.getChannel());
	assertEquals(0.0, msg.getValue(), 0.0001);
    }

    public void testScpiValueResponse() {
	SimpleDeviceMessage msg = new SimpleDeviceMessage("+0.23456f");
	assertEquals(0, msg.getChannel());
	assertEquals(0.23456, msg.getValue(), 0.0001);

	SimpleDeviceMessage msg2 = new SimpleDeviceMessage("-0.23456f");
	assertEquals(0, msg2.getChannel());
	assertEquals(-0.23456, msg2.getValue(), 0.0001);
    }
}
