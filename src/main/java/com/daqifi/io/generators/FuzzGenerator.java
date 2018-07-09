package com.daqifi.io.generators;

/**
 * Generates a fuzzy signa;.
 */
public class FuzzGenerator implements Generator {

  float amplitude = 1;

  public FuzzGenerator() {
  }

  public FuzzGenerator(float amplitude) {
    this.amplitude = amplitude;
  }

  @Override
  public float getValue(float x) {
    return amplitude * (float) Math.random();
  }
}
