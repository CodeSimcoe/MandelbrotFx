package com.codesimcoe.mandelbrotfx.fractal;

import com.codesimcoe.mandelbrotfx.Region;
import com.codesimcoe.mandelbrotfx.RegionOfInterest;
import com.codesimcoe.mandelbrotfx.ValueComplex;

import java.util.List;

public enum FFMCudaMandelbrotFractal implements Fractal {
  FFM_CUDA_MANDELBROT;

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
    throw new UnsupportedOperationException("FFMCudaMandelbrotFractal shall uses dedicated FFM computation");
  }

  @Override
  public String getName() {
    return "FFM CUDA Mandelbrot";
  }
}
