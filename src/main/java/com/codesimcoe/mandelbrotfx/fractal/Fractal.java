package com.codesimcoe.mandelbrotfx.fractal;

import com.codesimcoe.mandelbrotfx.Complex;
import com.codesimcoe.mandelbrotfx.Named;
import com.codesimcoe.mandelbrotfx.Region;
import com.codesimcoe.mandelbrotfx.RegionOfInterest;

import java.util.List;

public interface Fractal extends Named {

  int computeEscape(double re, double im, int max);

  Region getDefaultRegion();

  // Compute a single iteration (Zn+1 given Zn, possibly Zn-1, and c)
  Complex computeIteration(Complex z, Complex zPrev, Complex c);

  // Returns initial z for a given point
  default Complex initialZ(double re, double im) {
    return Complex.ZERO;
  }

  // Returns constant c for a given point
  default Complex constantC(double re, double im) {
    return new Complex(re, im);
  }

  default List<RegionOfInterest> getRegionsOfInterest() {
    return List.of();
  }
}
