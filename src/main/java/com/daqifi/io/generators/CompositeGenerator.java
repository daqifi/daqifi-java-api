package com.daqifi.io.generators;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @hide
 * Multiples several Generators together to create a composite signal
 */
class CompositeGenerator implements Generator {

  private ArrayList<Generator> generators;

  public CompositeGenerator(Generator... generator) {
    generators = new ArrayList<Generator>(Arrays.asList(generator));
  }

  @Override
  public float getValue(float x) {
    float value = 1;
    for (Generator gen : generators) {
      value *= gen.getValue(x);
    }
    return value;
  }
}
