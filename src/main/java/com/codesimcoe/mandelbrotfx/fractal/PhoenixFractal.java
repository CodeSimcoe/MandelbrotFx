package com.codesimcoe.mandelbrotfx.fractal;

import com.codesimcoe.mandelbrotfx.Region;

public record PhoenixFractal(String name, double p) implements Fractal {

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public Region getDefaultRegion() {
    return new Region(-0.5, 0, 2);
  }

  @Override
  public int compute(final double x, final double y, final int max) {
    double zx = 0.0;
    double zy = 0.0;
    double zxPrev = 0.0;
    double zyPrev = 0.0;
    int iterations = 0;

    while ((zx * zx + zy * zy) < 4 && iterations < max) {
      // z^2
      double zx2 = zx * zx - zy * zy;
      double zy2 = 2 * zx * zy;

      // zNext = z^2 + c + p * zPrev
      double zxNext = zx2 + x + this.p * zxPrev;
      double zyNext = zy2 + y + this.p * zyPrev;

      // update previous z
      zxPrev = zx;
      zyPrev = zy;

      // update current z
      zx = zxNext;
      zy = zyNext;

      iterations++;
    }

    return iterations;
  }
}
