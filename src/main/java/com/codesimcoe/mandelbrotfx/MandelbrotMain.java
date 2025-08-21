package com.codesimcoe.mandelbrotfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MandelbrotMain extends Application {

  public static void main(final String[] args) {
    launch(args);
  }

  @Override
  public void start(final Stage primaryStage) {

    int width = 1_000;
    int height = 1_000;

    Mandelbrot mandelbrot = new Mandelbrot(
      width,
      height,
      Configuration.DEFAULT_MAX_ITERATIONS
    );

    Scene scene = new Scene(mandelbrot.getRoot(), width + Configuration.SETTINGS_WIDTH, height);
    primaryStage.setScene(scene);
    primaryStage.show();

    mandelbrot.setRegion(Configuration.DEFAULT_XC, Configuration.DEFAULT_YC, Configuration.DEFAULT_SIZE);
    mandelbrot.update();
  }
}