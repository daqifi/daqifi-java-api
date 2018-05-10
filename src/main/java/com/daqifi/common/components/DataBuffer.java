package com.daqifi.common.components;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.lp.io.messages.Message;

/**
 * Data buffer used in the application. Data buffers are used to transfer messages
 * between different threads within the application. All buffers block on calls to
 * take(); if data loss is allowable, the push method does not block.
 */
public class DataBuffer {
  public static DataBuffer UI_BUFFER = new DataBuffer("UI Buffer", 1000, true);
  public static DataBuffer DATABASE_WRITE_BUFFER = new DataBuffer("Database Buffer", 2000, false);

  public String getBufferName() {
    return bufferName;
  }

  private String bufferName;
  private BlockingQueue<Message> queue;
  private int capacity;
  private boolean allowDataLoss;

  /**
   * Data buffer constructor. Sets the buffer name, maximum size, and wither the buffer allows data loss.
   *
   * @param name
   * @param bufferSize
   * @param allowDataLoss
   */
  public DataBuffer(String name, int bufferSize, boolean allowDataLoss) {
    this.bufferName = name;
    this.capacity = bufferSize;
    this.queue = new ArrayBlockingQueue<Message>(bufferSize);
    this.allowDataLoss = allowDataLoss;
  }

  /**
   * Adds a Message to the data buffer. If data loss is allowed, this method
   * fails silently if full. Loss-less buffers block until the data can be added to the buffer.
   *
   * @param msg
   */
  public void push(Message msg) {
    if (allowDataLoss) {
      queue.offer(msg);
    } else {
      try {
        queue.put(msg);
      } catch (InterruptedException e) {

      }
    }
  }

  /**
   * Removes a message from the buffer. This method blocks if no message is available.
   *
   * @return A message
   * @throws InterruptedException
   */
  public Message take() throws InterruptedException {
    return queue.take();
  }

  /**
   * Returns the size of the of the buffer.
   *
   * @return number of items in the buffer.
   */
  public int size() {
    return queue.size();
  }

  public int getCapacity() {
    return capacity;
  }

  /**
   * Returns true if the buffer is empty.
   *
   * @return
   */
  public boolean isEmpty() {
    return queue.isEmpty();
  }


}
