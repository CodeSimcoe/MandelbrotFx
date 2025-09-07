package com.codesimcoe.mandelbrotfx;

public value record ValueComplex(double re, double im) {

  public static final ValueComplex ZERO = new ValueComplex(0, 0);

  public ValueComplex add(final ValueComplex other) {
    return new ValueComplex(this.re + other.re(), this.im + other.im());
  }

  public ValueComplex square() {
    double newReal = this.re * this.re - this.im * this.im;
    double newImaginary = 2 * this.re * this.im;
    return new ValueComplex(newReal, newImaginary);
  }

  public double magnitudeSquared() {
    return this.re * this.re + this.im * this.im;
  }
}
