package com.daqifi.io.generators;

/**
 * Interface for a signal generator.
 */
public interface Generator {
    /**
     * Gets the signal value at a specific time
     * @param timeNanos Time in nanoseconds
     * @return Signal value at the specified time
     */
    float getValue(long timeNanos);
}
