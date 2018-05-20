package main.java.com.daqifi.io.generators;

/**
 * Created by Marc on 1/9/15.
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
