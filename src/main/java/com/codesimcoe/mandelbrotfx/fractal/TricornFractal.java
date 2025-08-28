package com.codesimcoe.mandelbrotfx.fractal;

import com.codesimcoe.mandelbrotfx.Region;

public enum TricornFractal implements Fractal {

  INSTANCE;

  @Override
  public String getName() {
    return "Tricorn";
  }

  @Override
  public Region getDefaultRegion() {
    return new Region(-0.1, 0, 4);
  }

  @Override
  public int compute(final double x0, final double y0, final int max) {
    double x = 0.0;
    double y = 0.0;

    double x2 = 0.0;
    double y2 = 0.0;

    int i = 0;
    double modulusSquared = x2 + y2;

    while (modulusSquared <= 4.0 && i < max) {
      // Tricorn: z_{n+1} = (conj(z_n))^2 + c
      // => x_{n+1} = x^2 - y^2 + x0
      //    y_{n+1} = -2xy + y0
      final double newX = x * x - y * y + x0;
      final double newY = -2.0 * x * y + y0;

      x = newX;
      y = newY;

      x2 = x * x;
      y2 = y * y;
      modulusSquared = x2 + y2;
      i++;
    }
    return i;
  }
}
