// Copyright 2013 Marc Bernardini.
package com.daqifi.io.messages;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.logging.Logger;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.daqifi.common.messages.ProtoMessageV2;

/**
 * Message type for the device broadcast messages. Wraps the UDP packet and
 * parses out the important data from packet.
 *
 * @author marc
 */
public class DeviceBroadcastMessage extends Message {
  /**
   * The TCP of the device is set at this port
   */
  public static final int DEVICE_TCP_PORT = 9760;
  private static Logger log = Logger.getLogger(DeviceBroadcastMessage.class
          .getName());

  @Override
  public String toString() {
    return "DeviceBroadcastMessage [host=" + getHost() + ", name=" + name
            + ", mac=" + mac + ", data=" + new String(packet.getData())
            + "]";
  }

  private final DatagramPacket packet;

  public  ProtoMessageV2.DaqifiOutMessage getMessage() {
    return message;
  }

  private  ProtoMessageV2.DaqifiOutMessage message;
  private String name;
  private String mac = "";

  /**
   * Constructor.
   *
   * @param msg
   */
  public DeviceBroadcastMessage(final DatagramPacket msg) throws InvalidProtocolBufferException {
    super(null);
    this.packet = msg;
    try {
      this.message = ProtoMessageV2.DaqifiOutMessage.parseDelimitedFrom(new ByteArrayInputStream(msg.getData(), 0, msg.getLength()));

      StringBuilder sb = new StringBuilder();
      ByteString mac = this.message.getMacAddr();
      for (int i = 0; i < mac.size(); i++) {
        sb.append(String.format("%02X%s", mac.byteAt(i), (i < mac.size() - 1) ? "-" : ""));
      }
      this.mac = sb.toString();
      this.name = this.message.getHostName();
      log.info(this.message.toString());
    } catch (InvalidProtocolBufferException err) {
      log.warning(err.toString());
      log.info(print(msg.getData()));
      throw err;
    } catch (IOException err){
      log.warning(err.toString());
      this.name = "Unknown";
      this.mac = "Unknown";
    }
    // parseAndSetData(msg.getData());
  }

  /**
   * parses the data and sets this objects properties
   *
   * @param data
   */
  private void parseAndSetData(byte[] data) {
    String str = new String(data);
    String[] lines = str.split("\r\n");
    if (lines.length > 0) {
      name = lines[0];
    }
    if (lines.length > 1) {
      mac = lines[1];
    }
  }

  /**
   * Returns the host name or IP address of the datagram packet. This defers
   * from the getDeviceName in that this will return a network addressable
   * name.
   *
   * @return IP/host that sent this message
   */
  public String getHost() {
    String host = packet.getAddress().toString();
    if (host.startsWith("/")) {
      host = host.substring(1);
    }
    return host;
  }

  /**
   * Returns the device name parsed from the packet data.
   *
   * @return The device name
   */
  public String getDeviceName() {
    return name;
  }

  /**
   * Returns the devices MAC address as a string
   *
   * @return device MAC address
   */
  public String getMacAddress() {
    return mac;
  }

  /**
   * Returns the TCP port used to connect to the device.
   *
   * @return TCP port number
   */
  public int getTcpPort() {
    return (message != null) ? message.getDevicePort() : DEVICE_TCP_PORT;
  }

  @Override
  /**
   * Overriden getData method returns the byte data sent
   *  from the UDP broadcast.
   *  @return Raw data as a string.
   */
  public String getData() {
    return new String(packet.getData());
  }
}
