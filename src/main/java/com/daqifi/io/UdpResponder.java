// Copyright 2013 Marc Bernardini.
package main.java.com.daqifi.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;
import java.util.logging.Logger;

import com.daqifi.common.messages.ProtoMessageV2;

/**
 * UDP Responder class is used to respond to the UdpBroadcast. This class is
 * only used to test UDP discovery.
 *
 * @author Marc
 */
public class UdpResponder {
  private static Logger log = Logger.getLogger(UdpResponder.class.getName());
  private final DatagramSocket socket;
  private final List<Server> devices;

  public UdpResponder(int port, List<Server> devices)
          throws SocketException {
    this.devices = devices;
    this.socket = new DatagramSocket(port);
  }

  public UdpResponder(int port) throws SocketException {
    this(port, null);
  }

  public static void main(String[] args) {
    try {
      log.info("Starting UdpResponder on port 30303.");
      UdpResponder responder = new UdpResponder(30303);
      while (true) {
        responder.waitForBroadcast();
      }
    } catch (SocketException err) {
      log.severe(err.getMessage());
    }
  }

  /**
   * Waits for a broadcast message to be received. This method blocks until
   * interrupted or a message is received on the socket.
   */
  public void waitForBroadcast() {
    try {
      byte[] buf = new byte[1024];
      DatagramPacket received = new DatagramPacket(buf, buf.length);
      socket.receive(received);
      log.info(String.format("Received UDP packet from [%s:%d]: %s",
              received.getAddress(), received.getPort(),
              received.getData()));

      for (Server port : devices) {
        sendResponseForPort(received, port);
      }
    } catch (IOException err) {
      log.warning("An error occured awaiting broadcast: "
              + err.getMessage());
    }
  }

  protected void sendResponseForPort(DatagramPacket received, Server server)
          throws IOException {
    ProtoMessageV2.DaqifiOutMessage msg = server.getOutMessage();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    //byte[] data = msg.toByteArray();
    msg.writeDelimitedTo(out);
    byte[] data = out.toByteArray();
    DatagramPacket response = new DatagramPacket(data, data.length,
            received.getAddress(), received.getPort());
    socket.send(response);
  }

  public void close() {
    socket.close();
  }

}
