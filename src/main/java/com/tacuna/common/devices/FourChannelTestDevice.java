// Copyright 2013 Marc Bernardini.
package com.tacuna.common.devices;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import com.lp.io.DeviceMessageInterpreter;
import com.lp.io.MessageConsumer;
import com.lp.io.Server;
import com.lp.io.SocketConnector;
import com.lp.io.messages.Message;
import com.tacuna.common.components.DataBuffer;
import com.tacuna.common.devices.channels.AnalogInputChannel;
import com.tacuna.common.devices.channels.ChannelInterface;
import com.tacuna.common.devices.channels.InputInterface;
import com.tacuna.common.devices.scpi.Command;

/**
 * This class represents a simple four channelIndex test device. It is the
 * Andriod device for the test servers implementation.
 */
public class FourChannelTestDevice extends Device implements DeviceInterface {

  private static Logger log = Logger.getLogger(FourChannelTestDevice.class
          .getName());
  private final int NUMBER_OF_CHANNELS = 4;

  enum Channels {
    A1(1), A2(2), A3(3), A4(4);

    Channels(int index) {
      channel = new AnalogInputChannel(toString(), index, null);
    }

    protected AnalogInputChannel channel;

    public ChannelInterface getChannel() {
      return channel;
    }
  }

  private final ArrayList<ChannelInterface> channels = new ArrayList<ChannelInterface>(
          NUMBER_OF_CHANNELS);
  private final DeviceMessageInterpreter dataInterpreter = new DeviceMessageInterpreter();
  private String name = "Simple Four Channel Test Device";
  private final MessageChannelRouter router = new MessageChannelRouter(
          channels, null, null, null);
  private InetSocketAddress deviceAddress = null;
  private SocketConnector connection = null;

  public FourChannelTestDevice() {
    channels.add(Channels.A1.getChannel());
    channels.add(Channels.A2.getChannel());
    channels.add(Channels.A3.getChannel());
    channels.add(Channels.A4.getChannel());
    dataInterpreter.registerObserver(router);
  }

  @Override
  public String getDeviceType() {
    return "Four Channel Test Device";
  }

  @Override
  public String getDeviceName() {
    return name;
  }

  @Override
  public Collection<ChannelInterface> getChannels() {
    return channels;
  }

  @Override
  public int getNumberOfAnalogInChannels() {
    return NUMBER_OF_CHANNELS;
  }

  @Override
  public int getNumberOfDigitalInChannels() {
    return 0;
  }

  @Override
  public void setDeviceName(String name) {
    this.name = name;
  }

  @Override
  public InetSocketAddress getNetworkAddress() {
    return deviceAddress;
  }

  @Override
  public void setNetworkAddress(InetSocketAddress networkAddress) {
    deviceAddress = networkAddress;
  }

  @Override
  public void connect() {
    if (connection != null && connection.isConnected()) {
      connection.close();
    }
    connection = new SocketConnector(deviceAddress.getHostName(),
            deviceAddress.getPort(), dataInterpreter);
  }

  @Override
  public void disconnect(){
    if(this.connection != null) this.connection.close();
  }


  public static void main(String[] args) {
    try {

      DeviceInterface device = new FourChannelTestDevice();
      device.setNetworkAddress(new InetSocketAddress("192.168.1.100",
              9760));
      device.connect();
      Server svr = new Server(9760, null);

      svr.join();
    } catch (InterruptedException ie) {
      log.warning(ie.toString());
    }
  }

  @Override
  public int getNumberOfAnalogOutChannels() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void send(Command command) {
    // TODO Auto-generated method stub

  }

  // @Override
  // public ScpiMessageExchange getEx() {
  // return null;
  // }

  @Override
  public SocketConnector getConnection() {
    // TODO Auto-generated method stub
    return connection;
  }

  @Override
  public void startStreaming() {

  }

  @Override
  public void stopStreaming() {

  }

  @Override
  public String getMacAddress() {
    return "12:34:56:78:90:ab";
  }

  @Override
  public void setMacAddress(String macAddress) {
    // Do nothing. This class is meant for testing only.
  }

  @Override
  public void registerObserver(MessageConsumer o) {
    // TODO Auto-generated method stub

  }

  @Override
  public void removeObserver(MessageConsumer o) {
    // TODO Auto-generated method stub

  }

  @Override
  public void notifyObservers(Message message) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setSampleFrequency(int frequency) {
    // TODO Auto-generated method stub

  }

  @Override
  public int getSampleFrequency() {
    // TODO Auto-generated method stub
    return 0;
  }


  @Override
  public void addOutputBuffer(DataBuffer buffer) {
    router.addOutputBuffer(buffer);
  }

  @Override
  public void removeOutputBuffer(DataBuffer buffer) {
    router.removeOutputBuffer(buffer);
  }

  @Override
  public void updateNetworkSettings(String ssid, int securityType, String password){}
}
