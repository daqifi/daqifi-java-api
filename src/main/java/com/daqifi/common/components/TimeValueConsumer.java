// Copyright 2013 Marc Bernardini.
package com.daqifi.common.components;

public interface TimeValueConsumer {

  public abstract void add(final long time, final float value);
}
