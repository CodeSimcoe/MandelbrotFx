package com.codesimcoe.mandelbrotfx;

public enum ZoomMode implements Named {
  // Zoom relative to area's center
  CENTER("Center"),

  // Zoom relative to pointer's location
  POINTER("Pointer");

  private final String name;

  ZoomMode(final String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return this.name;
  }
}
