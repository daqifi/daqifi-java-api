/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lp.io;

import com.lp.io.messages.Message;

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
  public void registerObserver(MessageConsumer<T> o);

  /**
   * Remove an observer
   *
   * @param o
   */
  public void removeObserver(MessageConsumer<T> o);

  /**
   * Used by implementations of this class to notify observers.
   *
   * @param message
   */
  public void notifyObservers(T message);
}
