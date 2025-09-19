package com.codesimcoe.mandelbrotfx.fractal;

import com.codesimcoe.mandelbrotfx.Complex;
import com.codesimcoe.mandelbrotfx.Region;

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
  public int computeEscape(final double x0, final double y0, final int max) {
    double x = 0.0, y = 0.0;
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
  public Complex computeIteration(final Complex z, final Complex zPrev, final Complex c) {
    // z^2
    double re = z.re() * z.re() - z.im() * z.im();
    double im = 2.0 * z.re() * z.im();

    // Apply abs to both parts
    double newX = Math.abs(re) + c.re();
    double newY = Math.abs(im) + c.im();

    return new Complex(newX, newY);
  }
}
