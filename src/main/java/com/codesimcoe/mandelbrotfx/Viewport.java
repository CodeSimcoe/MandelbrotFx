package com.codesimcoe.mandelbrotfx;

/// Represents a rectangular mapping between:
/// - **Screen space** (pixel coordinates: `x`, `y`)
/// - **Complex plane** (mathematical coordinates: `re`, `im`)
///
/// The viewport is defined by:
///
/// - The center of the region in the complex plane: `(xc, yc)`
/// - The size of the region (width in complex units)
/// - The screen resolution (`width` x `height` in pixels)
/// Provides conversion between screen and complex plane :
/// - Conversion from screen `(x, y)` to complex `(re, im)`
/// - Conversion from complex `(re, im)` to screen `(x, y)`
public class Viewport {

  private double centerRe;
  private double centerIm;
  private double size;
  private final int width;
  private final int height;

  public Viewport(
    double centerRe,
    double centerIm,
    double size,
    int width,
    int height) {

    this.centerRe = centerRe;
    this.centerIm = centerIm;
    this.size = size;
    this.width = width;
    this.height = height;
  }

  public double screenToRe(double x) {
    return this.screenToRe(x, this.width);
  }

  public double screenToRe(double x, double width) {
    return this.centerRe + (x - width / 2.0) * (this.size / width);
  }

  public double screenToIm(double y) {
    return this.screenToIm(y, this.height);
  }

  public double screenToIm(double y, double height) {
    return this.centerIm + (y - height / 2.0) * (this.size / height);
  }

  public int complexToX(double re) {
    return (int) Math.round((re - this.centerRe) * (this.width / this.size) + this.width / 2.0);
  }

  public int complexToY(double im) {
    return (int) Math.round((im - this.centerIm) * (this.height / this.size) + this.height / 2.0);
  }

  public void moveBy(double dre, double dim) {
    this.centerRe += dre;
    this.centerIm += dim;
  }

  public void update(Region region) {
    this.centerRe = region.centerRe();
    this.centerIm = region.centerIm();
    this.size = region.size();
  }

  public void complexMoveTo(double re, double im) {
    this.centerRe = re;
    this.centerIm = im;
  }

  public void screenMoveTo(double x, double y) {
    this.centerRe = this.screenToRe(x);
    this.centerIm = this.screenToIm(y);
  }

  public void zoomInBy(double factor) {
    this.size /= factor;
  }

  public void zoomOutBy(double factor) {
    this.size *= factor;
  }

  public double getCenterRe() {
    return this.centerRe;
  }

  public double getCenterIm() {
    return this.centerIm;
  }

  public double getSize() {
    return this.size;
  }

  public void setSize(double size) {
    this.size = size;
  }
}
