// Copyright 2013 Marc Bernardini.
package com.tacuna.common.components;

/**
 * Converts a ADC value to a voltage.
 *
 * @author Marc
 */
public class DtoAConverter {

  /**
   * Converts a device sample to a voltage value
   *
   * @param sampleValue
   * @return
   */
  public static double convertSampleToVoltage(int sampleValue) {
    return convertSampleToVoltage(sampleValue, 1);
  }

  public static double convertSampleToVoltage(int sampleValue, int extAdcRange) {
    double extAdcResolution = 131072.0;
    return ((sampleValue * ((extAdcRange * 10.0) + 10.0)) / (extAdcResolution));
  }

  /**
   * Converts an analog floating point number to an integer. This method is
   * used as software analog to digital conversion.
   *
   * @param value
   * @param resolution
   * @return
   */
  public static int convertVoltageToInt(double value, double resolution) {
    return convertVoltageToInt(value, resolution, 1);
  }

  public static int convertVoltageToInt(double value, double resolution, int extAdcRange) {
    return (int) Math.round(value * resolution
            / ((extAdcRange * 10.0) + 10.0));
  }
}
