package com.lp.io.messages;

import com.tacuna.common.devices.channels.InputInterface;
import com.tacuna.common.devices.channels.ChannelInterface;

/**
 * Data message type sent by the sensor device. The single line message is
 * passed to the constructor and parsed into the appropriate fields.
 *
 * @author marc
 */
public class SimpleDeviceMessage extends Message {
  private final int CHANNEL_TIME_VALUE_NUM_PARAMS = 3;
  private final int CHANNEL_INDEX = 0;
  private final int TIME_STAMP_INDEX = 1;
  private final int VALUE_INDEX = 2;

  public final int INVALID_CHANNEL = -1;
  int channelIndex = INVALID_CHANNEL;
  ChannelInterface channel;
  long deviceTimestamp;
  double value;

  /**
   * String Constructor. An attempt is made to parse the string into the
   * proper structure.
   *
   * @param data
   */
  public SimpleDeviceMessage(final String data) {
    super(data);
    String[] split = data.split(",");
    // Data from the device formatted as 'channelIndex, timestamp, value'
    if (split.length == CHANNEL_TIME_VALUE_NUM_PARAMS) {
      channelIndex = Integer.parseInt(split[CHANNEL_INDEX].trim());
      deviceTimestamp = Long.parseLong(split[TIME_STAMP_INDEX].trim());
      value = Double.parseDouble(split[VALUE_INDEX].trim());
    } else {
      // Value only data (like a SCPI response).
      value = Double.parseDouble(data);
    }

  }

  public SimpleDeviceMessage(long time, ChannelInterface channel, double value) {
    super("");
    this.deviceTimestamp = time;
    this.channel = channel;
    this.channelIndex = channel.getDeviceIndex();
    this.value = value;
  }

  /**
   * Returns the channelIndex associated with this message.
   *
   * @return channelIndex
   */
  public ChannelInterface getChannel() {
    return channel;
  }

  /**
   * Returns the channelIndex associated with this message.
   *
   * @return channelIndex
   */
  public void setChannel(final ChannelInterface channel) {
    this.channel = channel;
  }

  /**
   * @return the channelIndex
   */
  public int getChannelIndex() {
    return channelIndex;
  }

  /**
   * @param channelIndex the channelIndex to set
   */
  public void setChannelIndex(int channelIndex) {
    this.channelIndex = channelIndex;
  }

  /**
   * Returns the value that this channelIndex reported at this timestamp.
   *
   * @return value
   */
  public double getValue() {
    return value;
  }

  /**
   * Returns the timestamp stamp sent from the device
   *
   * @Return deviceTimestamp ticks for the start of the device.
   */
  public long getDeviceTimestamp() {
    return deviceTimestamp;
  }

  @Override
  public String toString() {
    return (String) getData();
  }
}
