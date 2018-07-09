// Copyright 2013 Marc Bernardini.
package com.daqifi.common.devices.scpi;

import com.daqifi.common.devices.channels.ChannelInterface;

/**
 * Wrapper class for sending SCPI commands to Devices
 */
public class Command {

  private final String command;
  private boolean channelSet;

  private ChannelInterface channelInterface;

  /**
   * Creates a SCPI command using the command and a channelIndex number.
   *
   * @param command
   */
  public Command(String command, ChannelInterface channel) {
    super();
    this.command = command;
    this.channelSet = true;
    this.channelInterface = channel;
  }

  /**
   * Creates a SCPI command using the string command.
   *
   * @param command
   */
  public Command(String command) {
    super();
    this.command = command;
  }

  public Command(String format, Object ... args){
    super();
    this.command = String.format(format, args);
  }

  @Override
  /**
   * Returns the command as a line terminated string.
   * @return string
   */
  public String toString() {
    if (channelSet) {
      return command + " " + channelInterface.getDeviceIndex();
    }
    return command;
  }

  /**
   * Returns the command as a byte array suitable for sending across a socket.
   *
   * @return command as a byte array
   */
  public byte[] getBytes() {
    return toString().getBytes();
  }

  /**
   * Returns true if the command has a channelIndex associated with it.
   *
   * @return true if there is a channelIndex.
   */
  public boolean isChannelSet() {
    return channelSet;
  }

  /**
   * @return the command
   */
  public String getCommand() {
    return command;
  }

  /**
   * @return the channelIndex
   */
  public ChannelInterface getChannel() {
    return channelInterface;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime
            * result
            + ((channelInterface == null) ? 0 : channelInterface.hashCode());
    result = prime * result + (channelSet ? 1231 : 1237);
    result = prime * result + ((command == null) ? 0 : command.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Command other = (Command) obj;
    if (channelInterface != other.channelInterface)
      return false;
    if (channelSet != other.channelSet)
      return false;
    if (command == null) {
      if (other.command != null)
        return false;
    } else if (!command.equals(other.command))
      return false;
    return true;
  }
}
