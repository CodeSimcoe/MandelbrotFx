module MandelbrotFx {
	requires transitive javafx.graphics;
  requires javafx.controls;
  requires javafx.media;

  requires jmh.core;
  requires java.desktop;

  exports com.codesimcoe.mandelbrotfx to javafx.graphics;
  exports com.codesimcoe.mandelbrotfx.fractal to javafx.graphics;
  exports com.codesimcoe.mandelbrotfx.escape to javafx.graphics;
}