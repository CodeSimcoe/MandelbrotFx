package com.codesimcoe.mandelbrotfx;

public record Complex(double re, double im) {
  public static final Complex ZERO = new Complex(0, 0);

  public double magnitudeSquared() {
    return this.re * this.re + this.im * this.im;
  }

  public Complex sin() {
    // sin(x + i y) = sin(x) cosh(y) + i cos(x) sinh(y)
    double real = Math.sin(this.re) * Math.cosh(this.im);
    double imag = Math.cos(this.re) * Math.sinh(this.im);
    return new Complex(real, imag);
  }

  public Complex cos() {
    // cos(x + i y) = cos(x) cosh(y) - i sin(x) sinh(y)
    double real = Math.cos(this.re) * Math.cosh(this.im);
    double imag = -Math.sin(this.re) * Math.sinh(this.im);
    return new Complex(real, imag);
  }

  public Complex div(final Complex other) {
    double denom = other.re * other.re + other.im * other.im;
    double real = (this.re * other.re + this.im * other.im) / denom;
    double imag = (this.im * other.re - this.re * other.im) / denom;
    return new Complex(real, imag);
  }

  public Complex sub(final Complex other) {
    return new Complex(this.re - other.re, this.im - other.im);
  }

  public Complex scale(final double factor) {
    return new Complex(this.re * factor, this.im * factor);
  }
}
