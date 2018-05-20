package main.java.com.daqifi.io;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UdpResponderThread extends Thread {
  private static Logger log = Logger.getLogger(UdpResponderThread.class
          .getName());

  private final List<Server> devicePorts;

  public UdpResponderThread(List<Server> devicePorts) {
    this.devicePorts = devicePorts;
    start();
  }

  @Override
  public void run() {
    try {
      log.info("Starting UdpResponder on port 30303.");
      UdpResponder responder = new UdpResponder(30303, devicePorts);
      while (true) {
        responder.waitForBroadcast();
      }
    } catch (Exception err) {
      log.log(Level.SEVERE, "Exception caught", err);
    }
  }

}
