package com.codesimcoe.mandelbrotfx.fractal;

import com.codesimcoe.mandelbrotfx.Complex;
import com.codesimcoe.mandelbrotfx.Region;

/// Multibrot algorithm : Zn+1 = Zn^p + c
public record MultibrotFractal(int power) implements Fractal {

  @Override
  public String getName() {
    return "Multibrot " + this.power;
  }

  @Override
  public Region getDefaultRegion() {
    return new Region(-0.5, 0, 2);
  }

  @Override
  public int computeEscape(double cr, double ci, int maxIter) {
    double zr = 0.0;
    double zi = 0.0;

    int iter = 0;
    while (iter < maxIter) {
      double zr2 = zr * zr;
      double zi2 = zi * zi;

      if (zr2 + zi2 > 4.0) {
        break;
      }

      double newRe;
      double newIm;

      switch (this.power) {
        case 2 -> {
          newRe = zr2 - zi2 + cr;
          newIm = 2.0 * zr * zi + ci;
        }
        case 3 -> {
          newRe = zr * (zr2 - 3.0 * zi2) + cr;
          newIm = zi * (3.0 * zr2 - zi2) + ci;
        }
        case 4 -> {
          double zr4 = zr2 * zr2;
          double zi4 = zi2 * zi2;
          double zr2zi2 = zr2 * zi2;
          newRe = zr4 - 6.0 * zr2zi2 + zi4 + cr;
          newIm = 4.0 * zr * zi * (zr2 - zi2) + ci;
        }
        default -> {
          double[] pow = powComplex(zr, zi, this.power);
          newRe = pow[0] + cr;
          newIm = pow[1] + ci;
        }
      }

      zr = newRe;
      zi = newIm;
      iter++;
    }

    return iter;
  }

  private static double[] powComplex(double zr, double zi, int power) {
    double rr = 1.0;
    double ri = 0.0;

    double br = zr;
    double bi = zi;
    int exp = power;

    while (exp > 0) {
      if ((exp & 1) != 0) {
        double tr = rr * br - ri * bi;
        double ti = rr * bi + ri * br;
        rr = tr;
        ri = ti;
      }

      double tr = br * br - bi * bi;
      double ti = 2.0 * br * bi;
      br = tr;
      bi = ti;

      exp >>= 1;
    }

    return new double[]{rr, ri};
  }

  @Override
  public Complex computeIteration(Complex z, Complex zPrev, Complex c) {
    double zr = z.re();
    double zi = z.im();

    double r = Math.hypot(zr, zi);
    double theta = Math.atan2(zi, zr);

    double rp = Math.pow(r, this.power);
    double angle = this.power * theta;

    double newRe = rp * Math.cos(angle) + c.re();
    double newIm = rp * Math.sin(angle) + c.im();

    return new Complex(newRe, newIm);
  }
}
