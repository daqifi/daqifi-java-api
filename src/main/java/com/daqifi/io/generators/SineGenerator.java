package com.daqifi.io.generators;

/**
 * Generates a Sine wave signal
 */
public class SineGenerator implements Generator {
    private final float amplitude;
    private final float angularFrequency;  // in radians/second
    private final float offset;

    /**
     * Creates a sine wave generator
     * @param amplitude Peak amplitude of the sine wave
     * @param angularFrequency Angular frequency in radians/second (2π * frequency)
     * @param offset DC offset to add to the signal
     */
    public SineGenerator(float amplitude, float angularFrequency, float offset) {
        this.amplitude = amplitude;
        this.angularFrequency = angularFrequency;
        this.offset = offset;
    }

    @Override
    public float getValue(long timeNanos) {
        // Convert nanoseconds to seconds
        double timeSeconds = timeNanos / 1_000_000_000.0;
        // Calculate phase angle in radians
        double phase = angularFrequency * timeSeconds;
        return amplitude * (float)Math.sin(phase) + offset;
    }
}
