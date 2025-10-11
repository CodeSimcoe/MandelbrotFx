package com.codesimcoe.mandelbrotfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.Objects;

public class MandelbrotApp extends Application {

  @Override
  public void start(Stage primaryStage) {

    int width = 1024;
    int height = 1024;

    Mandelbrot mandelbrot = new Mandelbrot(
      width,
      height,
      Configuration.DEFAULT_MAX_ITERATIONS
    );

    Scene scene = new Scene(mandelbrot.getRoot(), width + Configuration.SETTINGS_WIDTH, height);
    primaryStage.setScene(scene);
    primaryStage.setOnCloseRequest(_ -> System.exit(0));
    InputStream iconResource = Objects.requireNonNull(this.getClass().getResourceAsStream("/icon.png"));
    primaryStage.getIcons().add(new Image(iconResource));

    mandelbrot.update();

    primaryStage.show();
    primaryStage.toFront();
  }
}