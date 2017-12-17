package com.tacuna.common.devices;

import com.tacuna.common.devices.channels.Channel;
import com.tacuna.common.devices.channels.ChannelInterface;

/**
 * Linear equation decorator is used to decorate a channelIndex with a fancy m*x+b
 * (where x is the original channelIndex value) equation.
 *
 * @author Marc
 */
public class LinearEquationDecorator extends Channel.ChannelDecorator {

  private final float aa;
  private final float bb;

  /**
   * Constructor.
   *
   * @param a
   * @param b
   * @param channelIndex
   */
  public LinearEquationDecorator(float a, float b, ChannelInterface channel) {
    super(channel);
    this.aa = a;
    this.bb = b;
  }


  public void add(final long time, final float value) {
    //super.add(timestamp, aa * value + bb);
  }
}
