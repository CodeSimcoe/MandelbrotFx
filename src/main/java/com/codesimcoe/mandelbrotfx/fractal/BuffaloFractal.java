package com.codesimcoe.mandelbrotfx.fractal;

import com.codesimcoe.mandelbrotfx.Region;
import com.codesimcoe.mandelbrotfx.ValueComplex;

// Buffalo fractal: z_{n+1} = (|Re(z_n)| - |Im(z_n)|)^2 + c
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
  public int computeEscape(double x0, double y0, int max) {

    double x = 0;
    double y = 0;

    int i = 0;

    while (x * x + y * y <= 4.0 && i < max) {
      // z^2 = (x + i y)^2 = (x^2 - y^2) + i(2xy)
      double re = x * x - y * y;
      double im = 2.0 * x * y;

      // Apply abs() on both parts
      double newX = Math.abs(re) + x0;
      double newY = Math.abs(im) + y0;

      x = newX;
      y = newY;
      i++;
    }

    return i;
  }

  @Override
  public ValueComplex computeIteration(ValueComplex z, ValueComplex zPrev, ValueComplex c) {
    double x = Math.abs(z.re());
    double y = Math.abs(z.im());

    double newY = 2.0 * x * y + c.im();
    double newX = x * x - 2.0 * x * y + y * y + c.re();

    return new ValueComplex(newX, newY);
  }
}
