package com.daqifi.io;

import com.daqifi.io.messages.Message;

/**
 * Message Producer interface. Message producer implementations allow objects to
 * register to receive notifications and can notify observers with the notifyObservers method.
 *
 * @author marc
 */
public interface MessageProducer<T extends Message> {
  /**
   * Register an observer
   *
   * @param o
   */
  void registerObserver(MessageConsumer<T> o);

  /**
   * Remove an observer
   *
   * @param o
   */
  void removeObserver(MessageConsumer<T> o);

  /**
   * Used by implementations of this class to notify observers.
   *
   * @param message
   */
  void notifyObservers(T message);
}
