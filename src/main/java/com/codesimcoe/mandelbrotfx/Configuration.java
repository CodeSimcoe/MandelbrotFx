package com.codesimcoe.mandelbrotfx;

public final class Configuration {

  public static final int SETTINGS_WIDTH = 300;

  public static final double DEFAULT_XC = -0.5;
  public static final double DEFAULT_YC = 0;
  public static final double DEFAULT_SIZE = 2;
  public static final double MAX_REGION_SIZE = 64;

  public static final int DEFAULT_MAX_ITERATIONS = 255;
  public static final double DEFAULT_ZOOM_FACTOR = 2;

  public static final ZoomMode DEFAULT_ZOOM_MODE = ZoomMode.POINTER;
  public static final double DEFAULT_MUSIC_VOLUME = .5;

  private Configuration() {
    //
  }
}