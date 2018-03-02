package com.lp.io.messages;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Somewhat generic message class. This class is used to encapsulate the message
 * data read off of a socket.
 *
 * @author marc
 */
public class Message {
  /**
   * Utility for print message byte arrays as HEX
   * @param bytes
   * @return
   */
  public static String print(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    sb.append("[ ");
    for (byte b : bytes) {
      sb.append(String.format("0x%02X ", b));
    }
    sb.append("]");
    return sb.toString();
  }
  /**
   * Time the message was created/received.
   */
  private final long timestamp;
  /**
   * The message data. Currently a string, but if encoding changes so too can
   * the data type.
   */
  private final Object data;

  public Message(Object data) {
    this.timestamp = System.currentTimeMillis();
    this.data = data;
  }

  /**
   * Converts the message to UTF8 encoded byte array.
   *
   * @return
   * @throws UnsupportedEncodingException
   */
  public byte[] toBytes() throws UnsupportedEncodingException {
    return ((String) data).getBytes("UTF-8");
  }

  /**
   * Returns the message data.
   *
   * @return
   */
  public Object getData() {
    return data;
  }

  /**
   * Returns the timestamp this message was received
   *
   * @return
   */
  public long getTimestamp() {
    return timestamp;
  }
}
