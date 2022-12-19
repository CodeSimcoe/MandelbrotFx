package com.codesimcoe.mandelbrotfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MandelbrotMain extends Application {

    public static void main(final String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {

        int width = 1000;
        int height = 1000;
        int max = 255;

        Mandelbrot mandelbrot = new Mandelbrot(width, height, max);

        Scene scene = new Scene(mandelbrot.getRoot(), width + Configuration.SETTINGS_WIDTH, height);
        primaryStage.setScene(scene);
        primaryStage.show();

        mandelbrot.setRegion(-0.5, 0, 2);
        mandelbrot.update();
    }
}