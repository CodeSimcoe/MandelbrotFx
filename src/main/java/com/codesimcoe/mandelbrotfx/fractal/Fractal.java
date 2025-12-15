package com.codesimcoe.mandelbrotfx.fractal;

import com.codesimcoe.mandelbrotfx.Named;
import com.codesimcoe.mandelbrotfx.Region;
import com.codesimcoe.mandelbrotfx.RegionOfInterest;
import com.codesimcoe.mandelbrotfx.ValueComplex;

import java.util.List;

public interface Fractal extends Named {

  int computeEscape(double re, double im, int max);

  Region getDefaultRegion();

  // Compute a single iteration (Zn+1 given Zn, possibly Zn-1, and c)
  ValueComplex computeIteration(ValueComplex z, ValueComplex zPrev, ValueComplex c);

  // Returns initial z for a given point
  default ValueComplex initialZ(double re, double im) {
    return ValueComplex.ZERO;
  }

  // Returns constant c for a given point
  default ValueComplex constantC(double re, double im) {
    return new ValueComplex(re, im);
  }

  default List<RegionOfInterest> getRegionsOfInterest() {
    return List.of();
  }
}
