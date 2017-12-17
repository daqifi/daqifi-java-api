/**
 *
 */
package com.tacuna.common.devices.channels;

import com.tacuna.common.devices.DeviceInterface;

/**
 * @author Marc
 */
public class DigitalInputChannel extends Channel implements InputInterface<Byte> {
  private byte bitMask = 0;
  private int byteIndex = 0;

  public DigitalInputChannel(String name, int index) {
    this(name, index, null);
  }

  public DigitalInputChannel(String name, int index, DeviceInterface device) {
    super(name, index, "", device);
    // Calculate the bit mask based on the index.
    int bitIndex = index;
    while (bitIndex > 8) {
      bitIndex -= 8;
      byteIndex++;
    }
    bitMask = (byte) (1 << bitIndex);
  }

  private byte latestValue;

  @Override
  public void add(long time, Byte value) {
    latestValue = value;
  }

  @Override
  public Byte getCurrentValue() {
    return latestValue;
  }

  @Override
  public Byte getMaximum() {
    return (byte) -1;
  }

  @Override
  public Byte getMinimum() {
    return (byte) 0;
  }

  /*
       * (non-Javadoc)
       *
       * @see com.tacuna.common.devices.channels.ChannelInterface#getType()
       */
  @Override
  public Type getType() {
    return Type.DIGITAL_IN;
  }

  public byte getBitMask() {
    return bitMask;
  }

  public int getByteIndex() {
    return byteIndex;
  }

}
