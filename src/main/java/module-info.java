module MandelbrotFx {
	requires transitive javafx.graphics;
  requires javafx.controls;
  requires javafx.media;

  requires jmh.core;

  exports com.codesimcoe.mandelbrotfx;
  exports com.codesimcoe.mandelbrotfx.fractal;
}