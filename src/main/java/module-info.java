module mandelbrotfx {
	requires transitive javafx.graphics;
  requires javafx.controls;
  requires javafx.media;

  requires jmh.core;
  requires java.desktop;
  requires atlantafx.base;
  requires java.logging;
  requires jdk.incubator.vector;

  exports com.codesimcoe.mandelbrotfx to javafx.graphics;
  exports com.codesimcoe.mandelbrotfx.fractal to javafx.graphics;
  exports com.codesimcoe.mandelbrotfx.fractal.extended to javafx.graphics;
  exports com.codesimcoe.mandelbrotfx.escape to javafx.graphics;
}