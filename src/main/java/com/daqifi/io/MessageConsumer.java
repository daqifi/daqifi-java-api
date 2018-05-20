package com.daqifi.io;

import com.daqifi.io.messages.Message;

/**
 * Message consumer interface used by asynch consumers
 * that need to be notified of a new message.
 *
 * @author marc
 */
public interface MessageConsumer<T extends Message> {
  /**
   * This method is called when a new message has been received.
   * Note that this method is called from the same thread
   * as the socket read call. Ensure that implementation
   * is thread safe.
   *
   * @param message
   */
  public void onMessage(T message);
}
