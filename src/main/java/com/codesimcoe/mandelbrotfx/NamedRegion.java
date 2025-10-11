package com.codesimcoe.mandelbrotfx;

public value record NamedRegion(String name, Region region) implements Named {
  @Override
  public String getName() {
    return this.name;
  }
}
