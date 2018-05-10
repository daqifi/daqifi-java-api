package com.daqifi.common.devices;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.lp.io.Server;
import com.lp.io.UdpResponderThread;

/**
 * Emulator class provides a main method to run the device emulator. The main
 * method takes 0 or more arguments. Each argument is interpretted as a port
 * number to listen on.
 *
 * @author Marc
 */
public class Emulator {
  private static Logger log = Logger.getLogger(Emulator.class.getName());

  public static int DEFAULT_PORT_NUMBER = 9760;

  public Emulator() {

  }

  public static void main(String[] args) {
    try {
      Set<Integer> portsToOpen = new HashSet<Integer>();
      for (int ii = 0; ii < args.length; ii++) {
        try {
          int port = Integer.parseInt(args[ii]);
          portsToOpen.add(port);
        } catch (Exception err) {
          log.warning("Unable to parse arguments. Reason: "
                  + err.toString());
        }
      }

      portsToOpen.add(DEFAULT_PORT_NUMBER);


      List<Server> servers = new ArrayList<Server>();
      for (Integer portNumber : portsToOpen) {
        servers.add(new Server(portNumber, null));
      }
      UdpResponderThread udpResponder = new UdpResponderThread(
              servers);
      Thread.sleep(1000000);
    } catch (Exception ie) {
      log.warning(ie.toString());
    }
  }

}
