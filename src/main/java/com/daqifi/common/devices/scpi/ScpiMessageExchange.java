// Copyright 2013 Marc Bernardini.
package com.daqifi.common.devices.scpi;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import com.daqifi.io.DataInterpreter;
import com.daqifi.io.DeviceMessageInterpreter;
import com.daqifi.io.MessageConsumer;
import com.daqifi.io.SocketConnector;
import com.daqifi.io.messages.Message;
import com.daqifi.io.messages.SimpleDeviceMessage;

/**
 * Since the SCPI commands aren't truly messaged based, the SCPI message
 * exchange attempts to reconcile the response to the original command. This
 * needs to be done so that way stuff like channelIndex information isn't lost.
 *
 * @author Marc
 */
public class ScpiMessageExchange extends DataInterpreter implements
        MessageConsumer {

  private static Logger log = Logger.getLogger(ScpiMessageExchange.class
          .getName());

  private final Queue<Command> commandQueue;
  private SocketConnector connection;
  private final DeviceMessageInterpreter interpreter;

  public ScpiMessageExchange(SocketConnector connection,
                             DeviceMessageInterpreter interpreter) {
    super();
    this.connection = connection;
    this.interpreter = interpreter;
    this.interpreter.registerObserver(this);
    commandQueue = new ConcurrentLinkedQueue<Command>();
  }

  /**
   * Sends the command to the underlying connection and adds the command to
   * the sent queue.
   * This call is synchronized because storing the command in the command
   * queue and send the command to the socket must be atomic.
   *
   * @param command
   */
  public synchronized void send(final Command command) {
    try {
      if (connection != null) {
        commandQueue.add(command);
        log.warning("Command: " + command.toString());
        connection.send(command.getBytes());
      }
    } catch (final IOException e) {
      log.severe("Caught exception trying to send SCPI command: "
              + e.toString());
    }
  }

  @Override
  public void onMessage(Message message) {
    SimpleDeviceMessage msg = (SimpleDeviceMessage) message;
    log.warning("Response: " + msg.toString());
    Command command = commandQueue.poll();
    if (command != null && command.isChannelSet()) {
      msg.setChannel(command.getChannel());
      log.info("Message: " + msg.toString() + "; Command: "
              + command.toString());
    }

    notifyObservers(msg);
  }

  /**
   * @param connection the connection to set
   */
  public void setConnection(SocketConnector connection) {
    commandQueue.clear();
    this.connection = connection;
  }
}
