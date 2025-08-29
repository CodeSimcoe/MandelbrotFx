package com.codesimcoe.mandelbrotfx.fractal;

import com.codesimcoe.mandelbrotfx.Named;
import com.codesimcoe.mandelbrotfx.Region;
import com.codesimcoe.mandelbrotfx.RegionOfInterest;

import java.util.List;

public interface Fractal extends Named {
  int compute(double x, double y, int max);
  Region getDefaultRegion();

  default List<RegionOfInterest> getRegionsOfInterest() {
    return List.of();
  }
}
