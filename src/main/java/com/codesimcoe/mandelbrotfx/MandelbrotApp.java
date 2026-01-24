package com.codesimcoe.mandelbrotfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class MandelbrotApp extends Application {

  @Override
  public void start(Stage primaryStage) {

    int width = 1024;
    int height = 1024;

    List<String> args = this.getParameters().getRaw();

    switch (args.size()) {
      case 1 -> {
        try {
          width = Integer.parseInt(args.getFirst());
          height = width;
        } catch (NumberFormatException e) {
          System.err.println("Provided parameter shall be size, in pixels");
          System.exit(1);
        }
      }
      case 2 -> {
        try {
          width = Integer.parseInt(args.get(0));
          height = Integer.parseInt(args.get(1));
        } catch (NumberFormatException e) {
          System.err.println("Provided parameters shall be width and height, in pixels (or only ");
          System.exit(1);
        }
      }
    }


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