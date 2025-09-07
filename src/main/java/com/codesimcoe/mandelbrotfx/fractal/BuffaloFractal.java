package com.codesimcoe.mandelbrotfx.fractal;

import com.codesimcoe.mandelbrotfx.Complex;
import com.codesimcoe.mandelbrotfx.Region;

public enum BuffaloFractal implements Fractal {

  BUFFALO;

  @Override
  public String getName() {
    return "Buffalo";
  }

  @Override
  public Region getDefaultRegion() {
    return new Region(-0.45, -0.7, 4);
  }

  @Override
  public int computeEscape(final double x0, final double y0, final int max) {
    double x = 0.0;
    double y = 0.0;

    double x2 = 0.0;
    double y2 = 0.0;

    int i = 0;
    double modulusSquared = x2 + y2;

    while (modulusSquared <= 4.0 && i < max) {
      // Buffalo fractal: z_{n+1} = (|Re(z_n)| - |Im(z_n)|)^2 + c
      final double ax = Math.abs(x);
      final double ay = Math.abs(y);

      final double newY = 2.0 * ax * ay + y0;
      final double newX = ax * ax - ay * ay + x0;

      // Expand: (ax - ay)^2 = ax^2 - 2*ax*ay + ay^2
      // So next x = ax^2 - 2*ax*ay + ay^2 + x0
      final double newXBuffalo = ax * ax - 2.0 * ax * ay + ay * ay + x0;

      x = newXBuffalo;
      y = newY;

      x2 = x * x;
      y2 = y * y;
      modulusSquared = x2 + y2;
      i++;
    }

    return i;
  }

  @Override
  public Complex computeIteration(final Complex z, final Complex zPrev, final Complex c) {
    double x = Math.abs(z.re());
    double y = Math.abs(z.im());

    double newY = 2.0 * x * y + c.im();
    double newX = x * x - 2.0 * x * y + y * y + c.re();

    return new Complex(newX, newY);
  }
}
