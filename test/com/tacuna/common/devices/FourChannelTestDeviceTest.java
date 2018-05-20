// Copyright 2013 Marc Bernardini.
package com.tacuna.common.devices;

import java.net.InetSocketAddress;

import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.junit.Test;

import main.java.com.daqifi.io.DataInterpreter;
import main.java.com.daqifi.io.SocketConnector;

public class FourChannelTestDeviceTest {

    @Test
    public void testConnect() {
	InetSocketAddress addr = new InetSocketAddress("abcde",1234);
	new NonStrictExpectations(){
	    @Mocked
	  SocketConnector mock;
	  {
	      new SocketConnector("abcde", 1234, (DataInterpreter)withNotNull()); 
	      times =1;
	  }
	};
	FourChannelTestDevice dev = new FourChannelTestDevice();
	dev.setNetworkAddress(addr);
	dev.connect();
    }

    
    @Test
    public void testConnectWhenAlreadyConnected() {
	InetSocketAddress addr = new InetSocketAddress("abcde",1234);
	new NonStrictExpectations(){
	    @Mocked
	  SocketConnector mock;
	  {
	      new SocketConnector("abcde", 1234, withInstanceOf(DataInterpreter.class));
	      times=2;
	      mock.isConnected(); 
	      result = true;
	      times=1;
	      mock.close();
	      times=1;
	  }
	};
	FourChannelTestDevice dev = new FourChannelTestDevice();
	dev.setNetworkAddress(addr);
	dev.connect();
	
	dev.connect();
    }
}
