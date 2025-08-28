package com.codesimcoe.mandelbrotfx.fractal;

import com.codesimcoe.mandelbrotfx.Region;

public enum MandelbrotFractal implements Fractal {

  INSTANCE;

  @Override
  public String getName() {
    return "Mandelbrot";
  }

  @Override
  public Region getDefaultRegion() {
    return new Region(-0.5, 0, 2);
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
