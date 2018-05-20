// Copyright 2013 Marc Bernardini.
package com.lp.io;

import static org.junit.Assert.assertEquals;

import java.net.DatagramPacket;
import java.net.InetAddress;

import org.junit.Before;
import org.junit.Test;

import main.java.com.daqifi.io.messages.DeviceBroadcastMessage;

public class DeviceBroadcastMessageTest {

    private DatagramPacket udpPacket;
    private DeviceBroadcastMessage message;
    private String data = "DCSGB1000\r\n00-04-A3-7A-A2-C1\r\n";
    @Before
    public void setup() throws Exception{
	udpPacket = new DatagramPacket(data.getBytes(), data.length(), InetAddress.getByName("192.168.1.1"), 30303);
	message = new DeviceBroadcastMessage(udpPacket);
    }
    
    @Test
    public void testGetData() {
	assertEquals(data, message.getData());
    }

    @Test
    public void testGetDeviceName() {
	assertEquals("DCSGB1000", message.getDeviceName());
    }
    
    @Test
    public void testGetHost() {
	assertEquals("192.168.1.1", message.getHost());
    }
    
    @Test
    public void testGetMacAddress() {
	assertEquals("00-04-A3-7A-A2-C1", message.getMacAddress());
    }
    
}
