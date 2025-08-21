package com.codesimcoe.mandelbrotfx;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Configuration {

  public static final int SETTINGS_WIDTH = 300;

  public static final double DEFAULT_XC = -0.5;
  public static final double DEFAULT_YC = 0;
  public static final double DEFAULT_SIZE = 2;

  private final IntegerProperty colorOffsetProperty = new SimpleIntegerProperty();

  // Eagerly instantiated singleton
  private static final Configuration instance = new Configuration();

  public static Configuration getInstance() {
    return instance;
  }

  public IntegerProperty getColorOffsetProperty() {
    return this.colorOffsetProperty;
  }
}