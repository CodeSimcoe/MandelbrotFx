package com.codesimcoe.mandelbrotfx.fractal;

import com.codesimcoe.mandelbrotfx.Configuration;
import com.codesimcoe.mandelbrotfx.Region;
import com.codesimcoe.mandelbrotfx.RegionOfInterest;

import java.util.List;

public enum MandelbrotFractal implements Fractal {

  INSTANCE;

  private final Region defaultRegion =  new Region(-0.5, 0, 2);
  private final List<RegionOfInterest> regionsOfInterest = List.of(
    new RegionOfInterest("Julia Island", new Region(-1.768778833, 0.001738996, 1e-6), 800),
    new RegionOfInterest("Flower", new Region(-1.999985881163, 0, 125e-11), Configuration.DEFAULT_MAX_ITERATIONS)
  );

  @Override
  public String getName() {
    return "Mandelbrot";
  }

  @Override
  public Region getDefaultRegion() {
    return this.defaultRegion;
  }

  @Override
  public List<RegionOfInterest> getRegionsOfInterest() {
    return this.regionsOfInterest;
  }

  @Override
  public int compute(final double x0, final double y0, final int max) {

    double x = 0;
    double y = 0;

    // Squared values
    double x2 = 0;
    double y2 = 0;

    // Iteration
    int i = 0;

    double modulusSquared = x2 + y2;
    while (modulusSquared <= 4 && i < max) {
      y = 2 * x * y + y0;
      x = x2 - y2 + x0;
      x2 = x * x;
      y2 = y * y;
      modulusSquared = x2 + y2;
      i++;
    }

    return i;
  }
}
