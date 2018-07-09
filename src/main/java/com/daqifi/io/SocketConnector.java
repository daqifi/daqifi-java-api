// Copyright 2013 Marc Bernardini.
package com.daqifi.io;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

/**
 * The Socket connector connects and reads data off of a Socket. Data read off
 * of the socket is passed to the DataInterpreter which breaks the data stream
 * into discrete messages.
 *
 * @author marc
 */
public class SocketConnector extends Thread {
  private static Logger log = Logger.getLogger(SocketConnector.class
          .getName());

  /**
   * Time out timestamp on the socket connections.
   */
  public final int TIME_OUT_MS = 10000; // 10 Seconds

  /**
   * Socket States. Here is the ASCII version of the state machine diagram for
   * this class.
   * <p></p>
   * <pre>
   *                   -[Failed to connect]-&gt; Failed
   *  0-&gt; Connecting-&gt;|
   *                  -[Connected]-&gt; Connected -&gt; [Connection ends] -&gt; Closed
   * </pre>
   *
   * @author marc
   */
  public enum State {
    Connecting, Connected, Failed, Closed, ClosedByPeer
  }

  private String host;

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  /**
   * Field port
   */
  private int port;
  private Socket cSocket;
  private State state;
  private int bytesSent = 0;

  public int getBytesReceived() {
    return bytesReceived;
  }

  public int getBytesSent() {
    return bytesSent;
  }

  private final int bytesReceived = 0;
  private Date firstConnected;
  private Date lastConnected;

  /**
   * Returns the number of milliseconds that this connection has been
   * connected.
   *
   * @return
   */
  public long getConnectionTime() {
    if (firstConnected == null) {
      return 0;
    }
    if (lastConnected == null) {
      return (new Date()).getTime() - firstConnected.getTime();
    }
    return lastConnected.getTime() - firstConnected.getTime();
  }

  /**
   * Protected set state updates the current state of the SocketConnector
   * object and notifies observers when the connection state changes.
   *
   * @param state
   */
  protected void setState(State state) {
    State oldState = this.state;
    this.state = state;
    if (this.state != oldState) {
      if (state == State.Connected) {
        firstConnected = new Date();
      }
      if (oldState == State.Connected) {
        lastConnected = new Date();
      }
      notifyListeners("state", oldState, state);
    }
  }

  private final DataInterpreter dataInterpreter;
  private int bufferSize = 65536;

  /**
   * Returns the internal buffer size used to pull data off of the input
   * stream. This should be sized to be just large enough to fit the incoming
   * messages.
   *
   * @return bufferSize
   */
  public int getBufferSize() {
    return bufferSize;
  }

  public void setBufferSize(int bufferSize) {
    this.bufferSize = bufferSize;
  }

  public SocketConnector(String host, int port,
                         DataInterpreter dataInterpreter) {
    this.host = host;
    this.port = port;
    this.dataInterpreter = dataInterpreter;
    state = SocketConnector.State.Connecting;
    start();
  }

  public SocketConnector(Socket socket, DataInterpreter di) {
    dataInterpreter = di;
    if (socket.isConnected()) {
      host = socket.getInetAddress().getHostName();
      port = socket.getPort();
      cSocket = socket;
      start();
    }

  }

  @Override
  public void run() {
    try {
      InetAddress serverAddr = InetAddress.getByName(host);
      cSocket = new Socket();
      InetSocketAddress endpoint = new InetSocketAddress(serverAddr, port);
      cSocket.connect(endpoint, TIME_OUT_MS);

      cSocket.setTcpNoDelay(true);

      setState(SocketConnector.State.Connected);
      InputStream in = new BufferedInputStream(cSocket.getInputStream(),
              getBufferSize());
      while (state == State.Connected) {
        int read = dataInterpreter.parseData(in);
        if (-1 == read) {
          setState(State.ClosedByPeer);
        }

      }
    } catch (Exception exp) {
      log.warning(exp.toString());
    } finally {
      if (cSocket != null) {
        try {
          cSocket.close();
        } catch (IOException e) {
          log.warning("Unable to close connection.");

        }
      }
      if (state == State.Connecting) {
        setState(State.Failed);
      } else if (state == State.ClosedByPeer) {
        // Don't set the state.
      } else {
        setState(State.Closed);
      }
    }
  }

  /**
   * Closes the socket connection. This method will swallow any errors
   * associated with closing the socket.
   */
  public void close() {
    try {
      if (isConnected() && null != cSocket) {
        setState(State.Closed);
        cSocket.close();
      }
    } catch (IOException err) {
      log.warning("Could not close the socket. Reason: " + err.toString());
    }
  }

  /**
   * Returns true if the socket is connect to the peer.
   *
   * @return
   */
  public Boolean isConnected() {
    return (state == State.Connected);
  }

  /**
   * Returns the current connection state of the socket.
   *
   * @return
   */
  public SocketConnector.State getConnectionState() {
    return this.state;
  }

  /**
   * Writes the byte data to the socket if the socket is connected. If the
   * socket is not connected this method just returns.
   *
   * @param data
   * @throws IOException
   */
  public void send(byte[] data) throws IOException {
    if (state == State.Connected && cSocket.getOutputStream() != null) {
      cSocket.getOutputStream().write(data);
      cSocket.getOutputStream().flush();
      bytesSent += data.length;
    }
  }

  private final ArrayList<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();

  /**
   * Register a change listener for receiving state change updates.
   *
   * @param listener
   */
  public void addChangeListener(PropertyChangeListener listener) {
    listeners.add(listener);
  }

  /**
   * Removes a previously registered listener.
   *
   * @param listener
   */
  public void removeChangeListener(PropertyChangeListener listener) {
    listeners.remove(listener);
  }

  protected void notifyListeners(String property, Object oldValue,
                                 Object newValue) {
    for (PropertyChangeListener listener : listeners) {
      listener.propertyChange(new PropertyChangeEvent(this, property,
              oldValue, newValue));
    }
  }

  @Override
  public String toString(){
    return String.format("%s:%d", host, port);
  }
}
