// Copyright 2013 Marc Bernardini.
package main.java.com.daqifi.io;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.protobuf.InvalidProtocolBufferException;
import main.java.com.daqifi.io.messages.DeviceBroadcastMessage;

/**
 * The UDP Broadcast class handles setting up and sending UDP broadcasts. This
 * class does not automatically know the broadcast address since that might
 * cause portability issues.
 * <p/>
 * This class extends the data interpreter so that observers may register to be
 * notified of a broadcast message.
 *
 * @author marc
 */
public class UdpBroadcast extends DataInterpreter implements Runnable {
  private static Logger log = Logger.getLogger(UdpBroadcast.class.getName());
  /**
   * The broadcast datagram socket for send UDP broadcasts
   */
  private final DatagramSocket socket;
  /**
   * Broadcast address.
   */
  private final InetAddress broadcastAddr;
  private final int port;
  boolean running = true;

  /**
   * The lastMessage is the last message sent out. Its primary purpose is to
   * ensure that this broadcaster ignores messages coming from itself.
   */
  private String lastMessage;

  /**
   * Constructor. Constructs a UDP broadcast socket on the specified broadcast
   * address that listens for responses on the specified port.
   *
   * @param port
   * @param broadcastAddr
   * @throws SocketException
   */
  public UdpBroadcast(int port, InetAddress broadcastAddr)
          throws SocketException {
    this.port = port;
    this.socket = new DatagramSocket(port);
    this.socket.setBroadcast(true);
    this.broadcastAddr = broadcastAddr;
  }

  /**
   * Sends the data string in a datagram packet to the same port used to
   * construct this class. In other words this method may be used if the
   * destination and receiving port are the same.
   *
   * @param data
   * @throws IOException
   */
  public void send(final String data) throws IOException {
    send(data, port);
  }

  /**
   * Sends the data string in a datagram packet to the specified destination
   * port.
   *
   * @param data
   * @param destinationPort
   * @throws IOException
   */
  public void send(final String data, final int destinationPort)
          throws IOException {
    lastMessage = data;
    DatagramPacket packet = new DatagramPacket(data.getBytes(),
            data.length(), broadcastAddr, destinationPort);
    socket.send(packet);
  }

  @Override
  public void run() {
    log.info("Awaiting response.");
    running = true;
    while (running) {
      receiveData();
    }
  }

  /**
   * ReceiveData is used to receive the data off of the broadcast socket. If
   * data is previously sent out on the socket, this method checks to see if
   * the last message matches the received message and discards it if it does.
   * This prevents the applications own messages from being processed.
   */
  protected void receiveData() {
    try {
      byte[] buf = new byte[1024];
      DatagramPacket received = new DatagramPacket(buf, buf.length);
      socket.receive(received);
      // Ignore anything coming from ourselves.
      if (lastMessage != null
              && (new String(received.getData())).contains(lastMessage)) {
        return;
      }
      notifyObservers(new DeviceBroadcastMessage(received));
      log.info(String.format("Received response from [%s:%d]", received
              .getAddress().toString(), received.getPort()));
    } catch (InvalidProtocolBufferException err) {
      log.log(Level.WARNING, "Invalid DatagramPacket", err);
    } catch (IOException err) {
      log.warning(err.getMessage());
    }
  }

  /**
   * Stops the receiving thread and closes the Socket connection.
   */
  public void stop() {
    running = false;
    // Close if not null.
    if (socket != null) {
      socket.close();
    }
  }
}
