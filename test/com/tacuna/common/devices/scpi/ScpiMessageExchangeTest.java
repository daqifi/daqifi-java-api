// Copyright 2013 Marc Bernardini.
package com.tacuna.common.devices.scpi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import main.java.com.daqifi.io.messages.Message;
import mockit.Injectable;
import mockit.NonStrictExpectations;
import mockit.Verifications;

import org.junit.Test;

import main.java.com.daqifi.io.DeviceMessageInterpreter;
import main.java.com.daqifi.io.MessageConsumer;
import main.java.com.daqifi.io.SocketConnector;
import main.java.com.daqifi.io.messages.SimpleDeviceMessage;
import com.tacuna.common.devices.channels.ChannelInterface;

public class ScpiMessageExchangeTest {

    @Injectable
    DeviceMessageInterpreter interpreter;
    @Injectable
    SocketConnector connection;
    @Injectable
    MessageConsumer consumer;

    @Test
    public void testSendMessage() throws Exception {
	// Record:
	new NonStrictExpectations() {
	    {
		connection.send((byte[]) any);
	    }
	};

	// Test:
	ScpiMessageExchange sme = new ScpiMessageExchange(connection,
		interpreter);
	sme.send(new Command("SYSTem:BAT:STAT?"));

	// Verify:
	new Verifications() {
	    {
		connection.send("SYSTem:BAT:STAT?\r\n".getBytes());
		times = 1;
	    }
	};
    }

    @Test
    public void testSendMessageWithError() throws Exception {
	// Record:
	new NonStrictExpectations() {
	    {
		connection.send((byte[]) any);
		result = new IOException();
	    }
	};

	// Test:
	ScpiMessageExchange sme = new ScpiMessageExchange(connection,
		interpreter);
	try {
	    sme.send(new Command("SYSTem:BAT:STAT?"));
	} catch (Exception err) {
	    fail("Exception caught from method that is not supposed to throw.");
	}

	// Verify:
    }

    @Test
    public void testOnMessage(@Injectable final ChannelInterface channel)
	    throws Exception {
	// Record:
	new NonStrictExpectations() {
	    {
		connection.send((byte[]) any);
		result = new IOException();

		channel.getDeviceIndex();
		result = 2;
	    }
	};

	// Test:
	ScpiMessageExchange sme = new ScpiMessageExchange(connection,
		interpreter);
	sme.registerObserver(consumer);
	sme.send(new Command("MEASure:EXT:ADC?", channel));
	sme.onMessage(new SimpleDeviceMessage("+0.123456"));

	// Verify:
	new Verifications() {
	    {
		Message message;
		consumer.onMessage(message = withCapture());
		assertEquals(channel,
			((SimpleDeviceMessage) message).getChannel());
		assertEquals(0.123456,
			((SimpleDeviceMessage) message).getValue(), 0.0001);
	    }
	};
    }
}
