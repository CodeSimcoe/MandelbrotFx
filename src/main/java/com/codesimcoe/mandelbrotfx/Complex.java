package com.codesimcoe.mandelbrotfx;

public record Complex(double re, double im) {

  public Complex add(Complex other) {
    return new Complex(re + other.re(), im + other.im());
  }

  public Complex square() {
    double newReal = re * re - im * im;
    double newImaginary = 2 * re * im;
    return new Complex(newReal, newImaginary);
  }

  public double magnitudeSquared() {
    return re * re + im * im;
  }
}
