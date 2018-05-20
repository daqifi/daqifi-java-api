// Copyright 2013 Marc Bernardini.
package com.lp.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import main.java.com.daqifi.io.DeviceMessageInterpreter;
import main.java.com.daqifi.io.MessageConsumer;
import mockit.Injectable;
import mockit.Verifications;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import main.java.com.daqifi.io.messages.Message;
import main.java.com.daqifi.io.messages.SimpleDeviceMessage;

public class DeviceMessageInterpreterTest {

    DeviceMessageInterpreter instance;

    @Before
    public void setUp() {
	instance = new DeviceMessageInterpreter();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testWellFormed(@Injectable final MessageConsumer listener)
	    throws Exception {
	// Record/setup:
	instance.registerObserver(listener);

	// Execute:
	byte[] buf = "1,12345,0.123552\r\n".getBytes();
	instance.parseData(new ByteArrayInputStream(buf));

	// Verify:
	new Verifications() {
	    {
		Message msg;
		listener.onMessage(msg = withCapture());
		times = 1;

		assertNotNull(msg);
		assertTrue(msg instanceof SimpleDeviceMessage);
		assertEquals(1, ((SimpleDeviceMessage) msg).getChannelIndex());
		assertEquals(12345,
			((SimpleDeviceMessage) msg).getDeviceTimestamp());
		assertEquals(0.123552, ((SimpleDeviceMessage) msg).getValue(),
			0.0001);
	    }
	};
    }

    @Test
    public void testNotWellFormed(@Injectable final MessageConsumer listener)
	    throws IOException {
	// Record/setup:
	instance.registerObserver(listener);

	// Execute:
	byte[] buf = "1,fs12345,0ads.123552\r\n".getBytes();
	instance.parseData(new ByteArrayInputStream(buf));

	// Verify:
	new Verifications() {
	    {
		listener.onMessage((Message) any);
		times = 0;
	    }
	};
    }

    @Test
    public void testScpiData(@Injectable final MessageConsumer listener)
	    throws IOException {
	// Record/setup:
	instance.registerObserver(listener);

	// Execute:
	byte[] buf = "+0.2345\r\n".getBytes();
	instance.parseData(new ByteArrayInputStream(buf));

	// Verify:
	new Verifications() {
	    {
		Message messageReceived;
		listener.onMessage(messageReceived = withCapture());
		times = 1;

		assertNotNull(messageReceived);
		assertEquals(0.2345,
			((SimpleDeviceMessage) messageReceived).getValue(),
			0.0001);

	    }
	};
    }

    @Test
    public void testScpiDataMultipleMessages(
	    @Injectable final MessageConsumer listener) throws IOException {
	// Record/setup:
	instance.registerObserver(listener);

	// Execute:
	byte[] buf = "+0.2345\r\n+1.2345\r\n+2.2345\r\n".getBytes();
	InputStream in = new ByteArrayInputStream(buf);
	instance.parseData(in);
	assertEquals(0, instance.parseData(in));
	assertEquals(0, instance.parseData(in));

	// Verify:
	new Verifications() {
	    {
		listener.onMessage((Message) any);
		times = 3;
	    }
	};
    }
}
