package com.daqifi.io.generators;

/**
 * Generates a Sine wave signal
 */
public class SineGenerator implements Generator {

  float amplitude = 1;
  float frequency = 1;
  float offset = 0;

  public SineGenerator(float amplitude, float frequency, float offset) {
    this.amplitude = amplitude;
    this.frequency = frequency;
    this.offset = offset;
  }

  @Override
  public float getValue(float x) {
    return amplitude * (float) Math.sin(x * frequency) + offset;
  }
}
