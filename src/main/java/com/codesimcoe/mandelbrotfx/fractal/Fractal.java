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
  Complex computeIteration(final Complex z, Complex zPrev, final Complex c);

  // Returns initial z for a given point
  default Complex initialZ(final double re, final double im) {
    return Complex.ZERO;
  }

  // Returns constant c for a given point
  default Complex constantC(final double re, final double im) {
    return new Complex(re, im);
  }

  default List<RegionOfInterest> getRegionsOfInterest() {
    return List.of();
  }
}
