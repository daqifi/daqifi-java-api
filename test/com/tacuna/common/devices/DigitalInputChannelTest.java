package com.tacuna.common.devices;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.tacuna.common.devices.channels.DigitalInputChannel;

public class DigitalInputChannelTest {

    @Test
    public void testBitMaskCreation() {
	DigitalInputChannel ch = new DigitalInputChannel("DI0", 0);
	DigitalInputChannel ch1 = new DigitalInputChannel("DI1", 1);
	DigitalInputChannel ch8 = new DigitalInputChannel("DI8", 7);

	assertTrue((ch.getBitMask() & 1) == 1);
	assertTrue((ch1.getBitMask() & 2) == 2);
	assertTrue((ch8.getBitMask() & 128) == 128);
    }

}
