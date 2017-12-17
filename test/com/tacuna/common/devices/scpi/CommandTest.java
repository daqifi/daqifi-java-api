// Copyright 2013 Marc Bernardini.
package com.tacuna.common.devices.scpi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import mockit.Injectable;
import mockit.NonStrictExpectations;

import org.junit.Test;

import com.tacuna.common.devices.channels.ChannelInterface;

public class CommandTest {

    @Test
    public void testToStringWithChannel(
	    @Injectable final ChannelInterface channel) {
	new NonStrictExpectations() {
	    {
		channel.getDeviceIndex();
		result = 2;
	    }
	};
	Command cmd = new Command("MEASure:EXT:ADC?", channel);
	assertEquals("MEASure:EXT:ADC? 2\r\n", cmd.toString());
    }

    @Test
    public void testToStringWithNoChannel() {
	Command cmd = new Command("MEASure:EXT:ADC?");
	assertEquals("MEASure:EXT:ADC?\r\n", cmd.toString());
    }

    @Test
    public void testEquals(@Injectable ChannelInterface channel) {
	Command cmd1 = new Command("MEASure:EXT:ADC?");
	Command cmd2 = new Command("MEASure:EXT:ADC?");
	Command cmd3 = new Command("MEASure:EXT:ADC?", channel);
	Command cmd4 = new Command("MEASure:EXT:VOLT?", channel);

	assertEquals(cmd1, cmd1);
	assertEquals(cmd1, cmd2);
	assertFalse(cmd1.equals(null));
	assertFalse(cmd1.equals(cmd3));
	assertFalse(cmd3.equals(cmd4));
    }

    @Test
    public void testHash(@Injectable final ChannelInterface channel) {
	new NonStrictExpectations() {
	    {
		channel.getDeviceIndex();
		result = 2;
	    }
	};
	Command cmd1 = new Command("MEASure:EXT:ADC?");
	Command cmd2 = new Command("MEASure:EXT:ADC?");
	Command cmd3 = new Command("MEASure:EXT:ADC?", channel);
	Command cmd4 = new Command("MEASure:EXT:VOLT?", channel);

	assertEquals(cmd1.hashCode(), cmd2.hashCode());
	assertFalse(cmd1.hashCode() == cmd3.hashCode());
	assertFalse(cmd3.hashCode() == cmd4.hashCode());
    }
}
