package com.tacuna.common.components;

/**
 * Created by Marc on 1/26/16.
 */
public class ExponentialMovingAverage {

  private float s = Float.NaN;
  private float alpha;

  public ExponentialMovingAverage(float alpha) {
    this.alpha = alpha;
  }

  public float getAverage() {
    return s;
  }

  /**
   * Adds a value to the moving average and removes the oldest sample if past
   * the sample size.
   *
   * @param value
   */
  public void add(float value) {
    if(Float.isNaN(s)) {
      s = value;
    }
    else{
      s = alpha*value+(1-alpha)*s;
    }
  }

  public void reset(){
    s = Float.NaN;
  }
}
