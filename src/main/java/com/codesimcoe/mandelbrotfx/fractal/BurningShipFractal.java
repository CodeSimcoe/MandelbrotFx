package com.codesimcoe.mandelbrotfx.fractal;

import com.codesimcoe.mandelbrotfx.Region;
import com.codesimcoe.mandelbrotfx.ValueComplex;

/// Burning Ship: z_{n+1} = (|Re(z_n)| + i|Im(z_n)|)^2 + c
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
  public int computeEscape(double x0, double y0, int max) {

    double x = 0;
    double y = 0;

    // Squared values
    double x2;
    double y2;
    double modulus2 = 0;

    int i = 0;

    while (modulus2 <= 4.0 && i < max) {
      double ax = Math.abs(x);
      double ay = Math.abs(y);

      double newY = 2.0 * ax * ay + y0;
      double newX = ax * ax - ay * ay + x0;

      x = newX;
      y = newY;

      x2 = x * x;
      y2 = y * y;
      modulus2 = x2 + y2;
      i++;
    }
    return i;
  }

  @Override
  public ValueComplex computeIteration(ValueComplex z, ValueComplex zPrev, ValueComplex c) {
    double x = Math.abs(z.re());
    double y = Math.abs(z.im());

    double newX = x * x - y * y + c.re();
    double newY = 2.0 * x * y + c.im();

    return new ValueComplex(newX, newY);
  }
}
