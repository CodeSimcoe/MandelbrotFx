package com.codesimcoe.mandelbrotfx.fractal;

public sealed interface Fractal permits MandelbrotFractal, JuliaFractal {
  int compute(double x, double y, int max);
}
