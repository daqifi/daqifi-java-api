package com.tacuna.common.components;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;

import mockit.Injectable;
import mockit.NonStrictExpectations;

import org.junit.Test;

import com.lp.io.messages.DeviceBroadcastMessage;
import com.tacuna.common.devices.DeviceInterface;

public class DeviceBroadcastResponseListenerTest {

    @Test
    public void testOnMessage(@Injectable final DeviceBroadcastMessage message) {
	new NonStrictExpectations() {
	    {
		message.getDeviceName();
		result = "UnitTestDevice";

		message.getHost();
		result = "somehost";

		message.getTcpPort();
		result = 1234;
	    }
	};
	HashSet<DeviceInterface> knownDevices = new HashSet<DeviceInterface>();
	DeviceBroadcastResponseListener listener = new DeviceBroadcastResponseListener(
		knownDevices);
	listener.onMessage(message);

	assertEquals(1, knownDevices.size());
	for (DeviceInterface device : knownDevices) {
	    assertEquals("UnitTestDevice", device.getDeviceName());
	    assertEquals("somehost", device.getNetworkAddress().getHostName());
	    assertEquals(1234, device.getNetworkAddress().getPort());
	}
    }

    @Test
    public void testOnMessageSameDeviceMultipleTimes(
	    @Injectable final DeviceBroadcastMessage message) {
	new NonStrictExpectations() {
	    {
		message.getDeviceName();
		result = "UnitTestDevice";

		message.getHost();
		result = "somehost";

		message.getTcpPort();
		result = 1234;
	    }
	};
	HashSet<DeviceInterface> knownDevices = new HashSet<DeviceInterface>();
	DeviceBroadcastResponseListener listener = new DeviceBroadcastResponseListener(
		knownDevices);
	listener.onMessage(message);
	listener.onMessage(message);

	assertEquals(1, knownDevices.size());
	for (DeviceInterface device : knownDevices) {
	    assertEquals("UnitTestDevice", device.getDeviceName());
	    assertEquals("somehost", device.getNetworkAddress().getHostName());
	    assertEquals(1234, device.getNetworkAddress().getPort());
	}
    }

}
