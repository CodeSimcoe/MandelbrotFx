package com.codesimcoe.mandelbrotfx.fractal;

import com.codesimcoe.mandelbrotfx.Region;
import com.codesimcoe.mandelbrotfx.ValueComplex;

public record PhoenixFractal(String name, double p) implements Fractal {

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public Region getDefaultRegion() {
    return new Region(-0.5, 0, 2);
  }

  @Override
  public int computeEscape(double re, double im, int max) {

    double zRe = 0;
    double zIm = 0;

    double zRePrev = 0;
    double zImPrev = 0;

    int iterations = 0;

    while ((zRe * zRe + zIm * zIm) < 4 && iterations < max) {
      // z^2
      double zx2 = zRe * zRe - zIm * zIm;
      double zy2 = 2 * zRe * zIm;

      // zNext = z^2 + c + p * zPrev
      double zxNext = zx2 + re + this.p * zRePrev;
      double zyNext = zy2 + im + this.p * zImPrev;

      // update previous z
      zRePrev = zRe;
      zImPrev = zIm;

      // update current z
      zRe = zxNext;
      zIm = zyNext;

      iterations++;
    }

    return iterations;
  }

  @Override
  public ValueComplex computeIteration(ValueComplex z, ValueComplex zPrev, ValueComplex c) {
    double zr = z.re();
    double zi = z.im();
    double zr2 = zr * zr - zi * zi;
    double zi2 = 2 * zr * zi;

    return new ValueComplex(
      zr2 + c.re() + this.p * zPrev.re(),
      zi2 + c.im() + this.p * zPrev.im()
    );
  }
}
