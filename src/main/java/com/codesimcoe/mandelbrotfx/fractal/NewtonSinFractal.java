package com.codesimcoe.mandelbrotfx.fractal;

import com.codesimcoe.mandelbrotfx.Region;
import com.codesimcoe.mandelbrotfx.ValueComplex;

public enum NewtonSinFractal implements Fractal {

  INSTANCE;

  private static final double TOLERANCE = 1e-6;
  private static final double RELAX = 1.0;
  private static final double BLOWUP_THRESHOLD = 1e-30;

  @Override
  public String getName() {
    return "Newton Sin";
  }

  @Override
  public int computeEscape(double re, double im, int max) {
    double zr = re;
    double zi = im;

    int i = 0;
    while (i < max) {
      // f(z) = sin(z) = sin(x)cosh(y) + i cos(x)sinh(y)
      double fRe = Math.sin(zr) * Math.cosh(zi);
      double fIm = Math.cos(zr) * Math.sinh(zi);

      // f'(z) = cos(z) = cos(x)cosh(y) - i sin(x)sinh(y)
      double dfRe = Math.cos(zr) * Math.cosh(zi);
      double dfIm = -Math.sin(zr) * Math.sinh(zi);

      double denom = dfRe * dfRe + dfIm * dfIm;
      if (denom < BLOWUP_THRESHOLD) {
        break;
      }

      // step = f(z) / f'(z)
      double stepRe = (fRe * dfRe + fIm * dfIm) / denom;
      double stepIm = (fIm * dfRe - fRe * dfIm) / denom;

      zr -= RELAX * stepRe;
      zi -= RELAX * stepIm;

      if (stepRe * stepRe + stepIm * stepIm < TOLERANCE * TOLERANCE) {
        break;
      }

      i++;
    }

    return i;
  }

  @Override
  public Region getDefaultRegion() {
    return new Region(-Math.PI / 2, 0, 0.625);
  }

  @Override
  public ValueComplex computeIteration(ValueComplex z, ValueComplex zPrev, ValueComplex c) {
    // Unmanaged
    return ValueComplex.ZERO;
  }

  @Override
  public ValueComplex initialZ(double re, double im) {
    return new ValueComplex(re, im);
  }

  @Override
  public ValueComplex constantC(double re, double im) {
    return ValueComplex.ZERO;
  }
}
