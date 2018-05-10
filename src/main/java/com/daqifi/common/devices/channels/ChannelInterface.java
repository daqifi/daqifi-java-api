// Copyright 2013 Marc Bernardini.
package com.daqifi.common.devices.channels;

import com.daqifi.common.devices.DeviceInterface;

/**
 * The Channel Interface is used by all device channels.
 */
public interface ChannelInterface {

  /**
   * Device's channelIndex type.
   */
  enum Type {
    ANALOG_IN, ANALOG_OUT, ANALOG_IO, DIGITAL_IN, DIGITAL_OUT, DIGITAL_IO, ANALOG_MATH_IN;

    public static Type fromInt(int value){
      switch(value) {
        case 0:
          return ANALOG_IN;
        case 1:
          return ANALOG_OUT;
        case 2:
          return ANALOG_IO;
        case 3:
          return DIGITAL_IN;
        case 4:
          return DIGITAL_OUT;
        case 5:
          return DIGITAL_IO;
      }
      return null;
    }
  }

  /**
   * Returns they channelIndex type.
   *
   * @return
   */
  public abstract Type getType();

  /**
   * Returns the channelIndex name.
   *
   * @return
   */
  public abstract String getName();
  public abstract void setName(String name);
  /**
   * Returns the channelIndex index of the channelIndex on the physical
   * device.
   *
   * @return
   */
  public abstract int getDeviceIndex();

  /**
   * Returns the channels unit of measure
   */
  public abstract String getUnit();
  public abstract void setUnit(String name);


  /**
   * Returns the channels display color
   */
  public abstract int getDisplayColor();
  public abstract void setDisplayColor(int color);

  /**
   * Returns the channels plot number
   */
  int getPlotNumber();
  void setPlotNumber(int plotIndex);


  /**
   * @param device
   */
  public abstract void setDevice(final DeviceInterface device);

  /**
   * Returns the device that this channelIndex is associated to.
   *
   * @return device
   */
  public abstract DeviceInterface getDevice();

  /**
   * Returns the number of samples that have been made.
   *
   * @return The number of samples that have been taken
   */
  public abstract int getNumberOfSamples();

  /**
   * Returns true if the channel is active. Active means that the device is
   * measuring values on this channel
   */
  public abstract boolean isActive();

  /**
   * Sets the channels active flag.
   *
   * @param value
   */
  public abstract void setActive(boolean value);

  /**
   * The NotSupportedException is thrown when the Channel implementation does
   * not support a particular operation.
   */
  public class NotSupportedException extends Exception {

    public NotSupportedException() {
      super();
    }

    public NotSupportedException(String message, Throwable cause) {
      super(message, cause);
    }

    public NotSupportedException(String message) {
      super(message);
    }

    public NotSupportedException(Throwable cause) {
      super(cause);
    }
  }
}
