package com.daqifi.io.generators;

/**
 * Limits a generator to be between min and max (inclusive)
 */
public class Limiter implements Generator {

  private final Generator gen;
  private final float min;
  private final float max;

  public Limiter(Generator gen, float min, float max) {
    this.gen = gen;
    this.min = min;
    this.max = max;
  }

  @Override
  public float getValue(float x) {
    float value = gen.getValue(x);
    if (value > max) {
      value = max;
    }
    if (value < min) {
      value = min;
    }
    return value;
  }

}
