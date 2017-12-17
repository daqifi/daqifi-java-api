package com.lp.io.generators;

/**
 * Created by Marc on 1/9/15.
 */
public class SawtoothGenerator implements Generator {

  float amplitude = 1;
  float period = 1;
  float offset = 0;

  public SawtoothGenerator() {

  }

  public SawtoothGenerator(float amplitude, float period, float offset) {
    this.amplitude = amplitude;
    this.period = period;
    this.offset = offset;
  }

  @Override
  public float getValue(float x) {

    return (float) (-(2 * amplitude / Math.PI) * Math.atan(1d / Math.tan(x * Math.PI / (double) period))) + offset;
  }
}
