package com.codesimcoe.mandelbrotfx.fractal;

import com.codesimcoe.mandelbrotfx.Named;

public interface Fractal extends Named {
  int compute(double x, double y, int max);
}
