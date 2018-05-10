// Copyright 2013 Marc Bernardini.
package com.daqifi.common.components;

public interface ApplicationComponent {

  public abstract void addComponent(final ApplicationComponent component);

  public abstract void removeComponent(final ApplicationComponent component);
}
