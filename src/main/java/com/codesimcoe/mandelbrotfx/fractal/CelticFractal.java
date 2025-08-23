package com.codesimcoe.mandelbrotfx.fractal;

public enum CelticFractal implements Fractal {

  INSTANCE;

  @Override
  public String getName() {
    return "Celtic";
  }

  @Override
  public int compute(final double x0, final double y0, final int max) {
    double x = 0.0, y = 0.0;
    double x2 = 0.0, y2 = 0.0;
    int i = 0;
    double mod2 = x2 + y2;

    while (mod2 <= 4.0 && i < max) {
      // Celtic Mandelbrot: z_{n+1} = |x^2 - y^2| + 2xy*i + c
      final double newX = Math.abs(x * x - y * y) + x0;
      final double newY = 2.0 * x * y + y0;

      x = newX;
      y = newY;

      x2 = x * x;
      y2 = y * y;
      mod2 = x2 + y2;
      i++;
    }
    return i;
  }
}
