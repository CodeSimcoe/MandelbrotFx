package com.codesimcoe.mandelbrotfx.fractal;

import com.codesimcoe.mandelbrotfx.Region;
import com.codesimcoe.mandelbrotfx.ValueComplex;

public enum TricornFractal implements Fractal {

  TRICORN;

  @Override
  public String getName() {
    return "Tricorn";
  }

  @Override
  public Region getDefaultRegion() {
    return new Region(-0.1, 0, 4);
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
      // Tricorn: z_{n+1} = (conj(z_n))^2 + c
      // => x_{n+1} = x^2 - y^2 + x0
      //    y_{n+1} = -2xy + y0
      double newX = x * x - y * y + x0;
      double newY = -2.0 * x * y + y0;

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
    double x = z.re();
    double y = z.im();
    double newX = x * x - y * y + c.re();
    double newY = -2 * x * y + c.im();
    return new ValueComplex(newX, newY);
  }
}
