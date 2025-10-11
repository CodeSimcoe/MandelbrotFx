package com.codesimcoe.mandelbrotfx.fractal;

import com.codesimcoe.mandelbrotfx.Complex;
import com.codesimcoe.mandelbrotfx.Region;

public record JuliaFractal(String name, double re0, double im0) implements Fractal {

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public Region getDefaultRegion() {
    return new Region(0, 0, 3.25);
  }

  @Override
  public int computeEscape(double re, double im, int max) {

    double zRe = re;
    double zIm = im;

    double zre2 = zRe * zRe;
    double zim2 = zIm * zIm;

    int i = 0;

    double modulusSquared = zre2 + zim2;
    while (modulusSquared <= 4 && i < max) {
      zIm = 2.0 * zRe * zIm + this.im0;
      zRe = zre2 - zim2 + this.re0;

      zre2 = zRe * zRe;
      zim2 = zIm * zIm;
      modulusSquared = zre2 + zim2;
      i++;
    }
    return i;
  }

  @Override
  public Complex computeIteration(Complex z, Complex zPrev, Complex c) {
    double zr = z.re();
    double zi = z.im();

    double zr2 = zr * zr - zi * zi;
    double zi2 = 2 * zr * zi;

    // Julia constant (fixed for whole fractal)
    return new Complex(zr2 + this.re0, zi2 + this.im0);
  }

  @Override
  public Complex initialZ(double re, double im) {
    // Start at the pixel coordinate
    return new Complex(re, im);
  }

  @Override
  public Complex constantC(double re, double im) {
    return new Complex(this.re0, this.im0);
  }
}
