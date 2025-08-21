package com.codesimcoe.mandelbrotfx.fractal;

public record JuliaFractal(double x0, double y0) implements Fractal {

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
