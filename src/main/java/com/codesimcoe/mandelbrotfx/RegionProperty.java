package com.codesimcoe.mandelbrotfx;

import javafx.beans.property.DoubleProperty;

public record RegionProperty(
  DoubleProperty xc,
  DoubleProperty yc,
  DoubleProperty size) {

  void update(final Region region) {
    this.xc.set(region.xc());
    this.yc.set(region.yc());
    this.size.set(region.size());
  }
}
