package com.daqifi.common.components;

/**
 * ExponentialMovingAverage
 */
public class ExponentialMovingAverage {

  private float s = Float.NaN;
  private float alpha;

  public ExponentialMovingAverage(float alpha) {
    this.alpha = alpha;
  }

  /**
   * Returns the calculated average
   * @return
   */
  public float getAverage() {
    return s;
  }

  /**
   * Adds a value to the moving average
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
