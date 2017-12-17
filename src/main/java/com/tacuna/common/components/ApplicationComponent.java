// Copyright 2013 Marc Bernardini.
package com.tacuna.common.components;

public interface ApplicationComponent {

  public abstract void addComponent(final ApplicationComponent component);

  public abstract void removeComponent(final ApplicationComponent component);
}
