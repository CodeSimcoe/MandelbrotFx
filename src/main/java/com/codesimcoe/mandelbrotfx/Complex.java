package com.codesimcoe.mandelbrotfx;

public record Complex(double re, double im) {
  public static final Complex ZERO = new Complex(0, 0);
}
