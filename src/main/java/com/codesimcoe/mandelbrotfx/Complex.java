package com.codesimcoe.mandelbrotfx;

public record Complex(double re, double im) {

  public Complex add(final Complex other) {
    return new Complex(this.re + other.re(), this.im + other.im());
  }

  public Complex square() {
    double newReal = this.re * this.re - this.im * this.im;
    double newImaginary = 2 * this.re * this.im;
    return new Complex(newReal, newImaginary);
  }

  public double magnitudeSquared() {
    return this.re * this.re + this.im * this.im;
  }
}
