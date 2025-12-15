package com.codesimcoe.mandelbrotfx.fractal.extended;

import com.codesimcoe.mandelbrotfx.Region;
import com.codesimcoe.mandelbrotfx.RegionOfInterest;
import com.codesimcoe.mandelbrotfx.ValueComplex;
import com.codesimcoe.mandelbrotfx.fractal.Fractal;
import com.codesimcoe.mandelbrotfx.fractal.MandelbrotFractal;

import java.util.List;

public abstract class AbstractMandelbrotFractal implements Fractal {

  @Override
  public int computeEscape(double re, double im, int max) {
    return MandelbrotFractal.MANDELBROT.computeEscape(re, im, max);
  }

  @Override
  public Region getDefaultRegion() {
    return MandelbrotFractal.MANDELBROT.getDefaultRegion();
  }

  @Override
  public List<RegionOfInterest> getRegionsOfInterest() {
    return MandelbrotFractal.MANDELBROT.getRegionsOfInterest();
  }

  @Override
  public ValueComplex computeIteration(ValueComplex z, ValueComplex zPrev, ValueComplex c) {
    throw new UnsupportedOperationException();
  }
}
