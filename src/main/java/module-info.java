module MandelbrotFx {
	requires transitive javafx.graphics;
  requires javafx.controls;
  requires javafx.media;

  exports com.codesimcoe.mandelbrotfx;
  exports com.codesimcoe.mandelbrotfx.fractal;
}