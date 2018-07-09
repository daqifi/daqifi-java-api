package com.daqifi.common.devices.channels;

/**
 *
 */
public interface InputInterface<T> {
  /**
   * Adds a value that was measured at the specified timestamp.
   *
   * @param time  The timestamp, in milliseconds from the epoch that the value was
   *              measured.
   * @param value The value measured
   */
  void add(long time, T value);

  /**
   * Queries the channelIndex and returns the channels current value.
   *
   * @return
   */
  T getCurrentValue();

  /**
   * Get series maximum.
   */
  T getMaximum();

  /**
   * Get series minimum.
   */
  T getMinimum();
}
