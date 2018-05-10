package com.daqifi.common.components;

import java.util.List;

/**
 * The MovingAverage class can be used to calculate a running moving average
 * with a fixed sample size.
 *
 * @author Marc
 */
public class MovingAverage {

  private final List<Float> samples;
  private final int sampleSize;

  /**
   * Constructs a running moving with a fixed sample size.
   *
   * @param sampleSize
   */
  public MovingAverage(int sampleSize) {
    this.samples = new CircularArrayList<Float>(sampleSize);
    this.sampleSize = sampleSize;
  }

  /**
   * Returns the average of the sample size.
   *
   * @return
   */
  public synchronized float getAverage() {
    float sum = 0;
    for (int ii = 0; ii < samples.size(); ii++) {
      sum += samples.get(ii);
    }
    return sum / samples.size();
  }

  /**
   * Adds a value to the moving average and removes the oldest sample if past
   * the sample size.
   *
   * @param value
   */
  public synchronized void add(float value) {
    if (samples.size() == sampleSize) {
      samples.remove(0);
    }
    samples.add(value);
  }

  public synchronized void reset() {
    samples.clear();
  }
}
