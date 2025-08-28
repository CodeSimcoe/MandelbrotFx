package com.codesimcoe.mandelbrotfx.fractal;

import com.codesimcoe.mandelbrotfx.Region;

public record JuliaFractal(String name, double x0, double y0) implements Fractal {

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public Region getDefaultRegion() {
    return new Region(0, 0, 3.25);
  }

  @Override
  public int compute(final double x, final double y, final int max) {

    double zx = x;
    double zy = y;

    double zx2 = zx * zx;
    double zy2 = zy * zy;

    int i = 0;

    double modulusSquared = zx2 + zy2;
    while (modulusSquared <= 4 && i < max) {
      zy = 2.0 * zx * zy + this.y0;
      zx = zx2 - zy2 + this.x0;

      zx2 = zx * zx;
      zy2 = zy * zy;
      modulusSquared = zx2 + zy2;
      i++;
    }
    return i;
  }
}
