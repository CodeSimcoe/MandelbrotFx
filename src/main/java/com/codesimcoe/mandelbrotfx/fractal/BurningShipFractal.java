package com.codesimcoe.mandelbrotfx.fractal;

import com.codesimcoe.mandelbrotfx.Region;
import com.codesimcoe.mandelbrotfx.ValueComplex;

public enum BurningShipFractal implements Fractal {

  BURNING_SHIP;

  @Override
  public String getName() {
    return "Burning ship";
  }

  @Override
  public Region getDefaultRegion() {
    return new Region(-0.4, -0.5, 4);
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
      // Burning Ship: z_{n+1} = (|Re(z_n)| + i|Im(z_n)|)^2 + c
      final double ax = Math.abs(x);
      final double ay = Math.abs(y);

      final double newY = 2.0 * ax * ay + y0;
      final double newX = ax * ax - ay * ay + x0;

      x = newX;
      y = newY;

      x2 = x * x;
      y2 = y * y;
      modulusSquared = x2 + y2;
      i++;
    }
    return i;
  }

  @Override
  public ValueComplex computeIteration(final ValueComplex z, final ValueComplex zPrev, final ValueComplex c) {
    double x = Math.abs(z.re());
    double y = Math.abs(z.im());

    double newX = x * x - y * y + c.re();
    double newY = 2.0 * x * y + c.im();

    return new ValueComplex(newX, newY);
  }
}
