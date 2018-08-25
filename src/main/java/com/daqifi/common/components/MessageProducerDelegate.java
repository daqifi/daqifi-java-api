package com.daqifi.common.components;

import com.daqifi.io.DataInterpreter;
import com.daqifi.io.MessageConsumer;
import com.daqifi.io.MessageProducer;
import com.daqifi.io.messages.Message;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by marc on 8/8/18.
 */

class MessageProducerDelegate implements MessageProducer {
    private static Logger log = Logger.getLogger(DataInterpreter.class
            .getName());

    private final List<MessageConsumer> observers = new CopyOnWriteArrayList<MessageConsumer>();

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
                log.log(Level.WARNING, "Unable to notify consumer:" + consumer.getClass()
                        + "; Error: " + error.toString(), error);
            }
        }
    }
}
