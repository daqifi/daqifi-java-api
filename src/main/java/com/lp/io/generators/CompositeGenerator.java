package com.lp.io.generators;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Marc on 1/9/15.
 */
public class CompositeGenerator implements Generator {

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
