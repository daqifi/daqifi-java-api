package com.daqifi.io.messages;

import com.daqifi.common.messages.ProtoMessageV2;

import java.util.concurrent.TimeUnit;

/**
 * Data message type sent by the sensor device. The single line message is
 * passed to the constructor and parsed into the appropriate fields.
 *
 * @author marc
 */
public class SimpleProtobufMessage extends Message {

  private ProtoMessageV2.DaqifiOutMessage protoMessage;

  /**
   * @return the protoMessage
   */
  public ProtoMessageV2.DaqifiOutMessage getProtoMessage() {
    return protoMessage;
  }

  /**
   * @param protoMessage the protoMessage to set
   */
  public void setProtoMessage(ProtoMessageV2.DaqifiOutMessage protoMessage) {
    this.protoMessage = protoMessage;
  }

  long deviceTimestamp;

  protected SimpleProtobufMessage() {
    super(null);
  }

  /**
   * String Constructor. An attempt is made to parse the string into the
   * proper structure.
   *
   * @param message
   */
  public SimpleProtobufMessage(final ProtoMessageV2.DaqifiOutMessage message) {
    super(message);
    protoMessage = message;
  }


  /**
   * Returns the timestamp stamp sent from the device
   *
   * @return deviceTimestamp ticks for the start of the device.
   */
  public long getDeviceTimestamp() {
    return protoMessage.getMsgTimeStamp();
  }

  public long getDeviceTimestamp(TimeUnit unit, int timeStampFreq){
    long tick = protoMessage.getMsgTimeStamp() & 0x00000000ffffffffL;
    double timeInSeconds = ((double)tick)/ ((double)timeStampFreq);
    switch(unit){
      case NANOSECONDS:
        return Math.round(timeInSeconds * 1000000000);
      case MICROSECONDS:
        return Math.round(timeInSeconds * 1000000);
      case MILLISECONDS:
        return Math.round(timeInSeconds * 1000);
      case SECONDS:
        return Math.round(timeInSeconds);
    }
      return Math.round(timeInSeconds);
  }
  /**
   * Returns the analog value at the given array index.
   *
   * @param index
   * @return measurement value
   */
  public int getAnalogInValue(int index) {
    if (index >= protoMessage.getAnalogInDataCount()) {
      return 0;
    }
    return protoMessage.getAnalogInData(index);
  }

  @Override
  public String toString() {
    return protoMessage.toString();
  }

  public String toCsvString() {
    StringBuilder builder = new StringBuilder();
    builder.append(getDeviceTimestamp());

    for (int ii = 0; ii < protoMessage.getAnalogInDataCount(); ii++) {
      builder.append(',');
      builder.append(protoMessage.getAnalogInData(ii));
    }
    return builder.toString();
  }

  /**
   * Returns the digital byte value at the given index.
   *
   * @param index
   * @return
   */
  public byte getDigitalInValue(int index) {
    if (protoMessage.hasDigitalData()) {
      return protoMessage.getDigitalData().byteAt(index);
    }
    return (byte) 0;
  }

  public static final byte[] NO_DIGITAL_DATA = {};
  public byte[] getDigitalData(){
    if (protoMessage.hasDigitalData()) {
      return protoMessage.getDigitalData().toByteArray();
    }
    return NO_DIGITAL_DATA;
  }

  public boolean isSysInfoResponse(){
    return protoMessage.hasDevicePn();
  }
}
