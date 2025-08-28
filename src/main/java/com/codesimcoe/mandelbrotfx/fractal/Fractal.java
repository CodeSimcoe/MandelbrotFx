package com.codesimcoe.mandelbrotfx.fractal;

import com.codesimcoe.mandelbrotfx.Named;
import com.codesimcoe.mandelbrotfx.Region;

public interface Fractal extends Named {
  int compute(double x, double y, int max);
  Region getDefaultRegion();
}
