package com.daqifi.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;

import com.daqifi.io.messages.DeviceBroadcastMessage;
import com.daqifi.io.messages.Message;

/**
 * Bare bones command line client for the WIFI DAQ devices. You can run this
 * utility and it will send out broadcast messages to discover devices on the
 * local network and attempt to connect to the first one.
 *
 * @author Marc
 */
public class CommandLineClient {

  protected HashSet<String> deviceAddresses = new HashSet<String>();

  /**
   * Prints the devices on the network that have responded.
   */
  public void printDevices() {
    int count = 0;
    for (String device : deviceAddresses) {
      System.out.println("Device " + Integer.toString(count++) + " @"
              + device);
    }
  }

  public class BroadcastPrintingClass implements MessageConsumer {

    @Override
    public void onMessage(Message message) {
      DeviceBroadcastMessage broadCastMessage = (DeviceBroadcastMessage) message;
      deviceAddresses.add(broadCastMessage.getHost() + ":"
              + Integer.toString(broadCastMessage.getTcpPort()));
    }
  }

  /**
   * Main.
   *
   * @param args
   */
  public static void main(String[] args) {
    try {
      CommandLineClient client = new CommandLineClient();
      BroadcastPrintingClass printer = client.new BroadcastPrintingClass();

      // The IP address can be 255.255.255.255 but most documents
      // seem to indicate that its better to use the local broadcast
      // address which might change from network to network. A local
      // address on a typical home network would look like 192.168.1.255.
      // We need to figure out a better to do this find the local address.
      UdpBroadcast broadcaster = new UdpBroadcast(12345,
              InetAddress.getByName("192.168.1.255"));
      broadcaster.registerObserver(printer);
      Thread listenThread = new Thread(broadcaster);
      listenThread.start();
      while (true) {
        Thread.sleep(1000);
        System.out.println("Sending message...");
        broadcaster.send("Discovery: Who is out there?\r\n", 30303);

        Thread.sleep(1000);
        client.printDevices();

        // Connect to the device here:
        // SocketConnector connector = new SocketConnector();
      }
    } catch (SocketException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (UnknownHostException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
