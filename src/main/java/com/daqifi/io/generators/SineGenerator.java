package main.java.com.daqifi.io.generators;

/**
 * Created by Marc on 1/9/15.
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
