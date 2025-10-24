package com.codesimcoe.mandelbrotfx.fractal;

import com.codesimcoe.mandelbrotfx.Region;
import com.codesimcoe.mandelbrotfx.ValueComplex;

/// Celtic Mandelbrot: z_{n+1} = |x^2 - y^2| + 2xy * i + c
public enum CelticFractal implements Fractal {

  CELTIC;

  @Override
  public String getName() {
    return "Celtic";
  }

  @Override
  public Region getDefaultRegion() {
    return new Region(-0.9, 0, 4);
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
      double newX = Math.abs(x * x - y * y) + x0;
      double newY = 2.0 * x * y + y0;

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
    double zr = z.re();
    double zi = z.im();

    double newX = Math.abs(zr * zr - zi * zi) + c.re();
    double newY = 2 * zr * zi + c.im();

    return new ValueComplex(newX, newY);
  }
}
