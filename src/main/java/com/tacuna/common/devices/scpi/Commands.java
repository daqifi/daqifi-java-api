// Copyright 2013 Marc Bernardini.
package com.tacuna.common.devices.scpi;

/**
 * TODO: I'm not sure if we need to keep this around since its not used
 * anywhere.
 * <p/>
 * Enumeration of SCPI commands used to communicate with the device.
 *
 * @author marc
 */
public enum Commands {
  //
  // IEEE 488.2 Mandated Commands
  //
  /**
   * Clear Status command.
   */
  CLS,
  /**
   * Standard Event Status Enable Query
   */
  ESE,
  /**
   * Standard Event Status Register Query
   */
  ESR,
  /**
   * Identification query
   */
  IDN,
  /**
   * Operation complete command/query
   */
  OPC,
  /**
   * Reset
   */
  RST,
  /**
   * Service request enable command/query
   */
  SRE,
  /**
   * Read status byte query
   */
  STB,
  /**
   * Self-test query
   */
  TST,
  /**
   * Wait to continue command.
   */
  WAI,

  /**
   * SYSTem command.
   */
  SYST,
  /**
   * MEASure command.
   */
  MEAS,
  /**
   * VOLTage command.
   */
  VOLT,
  /**
   * DC. Used with MEAS:VOLT to measure a DC value.
   */
  DC,

}
