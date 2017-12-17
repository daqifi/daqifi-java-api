// Copyright Marc Bernardini 2013
package com.lp.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import com.lp.io.messages.Message;

/**
 * The DataInterpreter produces messages using data received from a source.
 * Typically the source is a Socket but can be anything that produces bytes of
 * data.
 *
 * @author marc
 */
public class DataInterpreter implements MessageProducer {
  private static Logger log = Logger.getLogger(DataInterpreter.class
          .getName());

  private final List<MessageConsumer> observers = new CopyOnWriteArrayList<MessageConsumer>();

  public int parseData(InputStream in) throws IOException {
    return 0;
  }

  @Override
  public void registerObserver(MessageConsumer consumer) {
    if (consumer == null) {
      throw new NullPointerException(
              "Cannot register null MessageConsumer.");
    }
    observers.add(consumer);
  }

  @Override
  public void removeObserver(MessageConsumer consumer) {
    observers.remove(consumer);
  }

  @Override
  public void notifyObservers(Message message) {
    for (MessageConsumer consumer : observers) {
      try {
        consumer.onMessage(message);
      } catch (Throwable error) {
        log.warning("Unable to notify consumer:" + consumer.getClass()
                + "; Error: " + error.toString());
      }
    }
  }

}
