// Copyright 2013 Marc Bernardini.
package com.tacuna.common.devices;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import mockit.Injectable;
import mockit.NonStrictExpectations;

import org.junit.Test;

import main.java.com.daqifi.io.messages.DeviceBroadcastMessage;

public class DeviceFactoryTest {

    @Test
    public void testGetKnownDevices() {
	DeviceFactory factory = new DeviceFactory();
	assertNotNull(factory.getKnownDevices());
    }

    @Test
    public void testAddDevices() {
	DeviceInterface di = new FourChannelTestDevice();

	DeviceFactory factory = new DeviceFactory();
	factory.addDevice(di);
	Collection<DeviceInterface> devices = factory.getKnownDevices();
	assertNotNull(devices);
	assertEquals(1, devices.size());

	factory.addDevice(di);
	assertEquals(1, devices.size());
    }

    @Test
    public void getDeviceTest(final @Injectable DeviceBroadcastMessage message) {
	new NonStrictExpectations() {
	    {
		message.getDeviceName();
		result = "Test Device";

		message.getHost();
		result = "1.2.3.4";

		message.getTcpPort();
		result = 9090;

		message.getMacAddress();
		result = "01:23:45:67:89:ab";
	    }
	};

	DeviceInterface device = DeviceFactory.getDevice(message);

	assertEquals(device.getDeviceName(), "Test Device");
	assertEquals(device.getNetworkAddress().getHostName(), "1.2.3.4");
	assertEquals(device.getNetworkAddress().getPort(), 9090);
	assertEquals(device.getMacAddress(), "01:23:45:67:89:ab");

    }
}
