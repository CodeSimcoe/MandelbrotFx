package com.codesimcoe.mandelbrotfx;

public record RegionOfInterest(String name, Region region, int iterations) implements Named {
  @Override
  public String getName() {
    return this.name;
  }
}
