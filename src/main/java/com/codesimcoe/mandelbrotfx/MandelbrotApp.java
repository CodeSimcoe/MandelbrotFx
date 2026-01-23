package com.codesimcoe.mandelbrotfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.FloatVector;

import java.io.InputStream;
import java.util.Objects;

public class MandelbrotApp extends Application {

  @Override
  public void start(Stage primaryStage) {

    IO.println("Vector-API : Preferred species: " + FloatVector.SPECIES_PREFERRED
                 + ", " + DoubleVector.SPECIES_PREFERRED);

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
    primaryStage.setTitle("Mandelbrot FX");

    mandelbrot.update();

    primaryStage.show();
    primaryStage.toFront();

    // Scenario to generate JFR files, using different strategies
//    MandelbrotFractal.MANDELBROT.setStrategy(ValueRecordStrategy.INSTANCE);
//
//    record TimedROI(long waitMs, RegionOfInterest roi) {}
//    var exploration = List.of(
//      new TimedROI(0,    new RegionOfInterest("Start", new Region(-0.75, 0.1, 1.5), 100)),
//      new TimedROI(1000, new RegionOfInterest("Zoom1", new Region(-0.748, 0.102, 0.5), 150)),
//      new TimedROI(900,  new RegionOfInterest("Zoom2", new Region(-0.7478, 0.1018, 0.45), 160)),
//      new TimedROI(800,  new RegionOfInterest("Zoom3", new Region(-0.7476, 0.1016, 0.4), 170)),
//      new TimedROI(1000, new RegionOfInterest("Zoom4", new Region(-0.7475, 0.1015, 0.25), 200)),
//      new TimedROI(900,  new RegionOfInterest("Zoom5", new Region(-0.74745, 0.10152, 0.2), 210)),
//      new TimedROI(800,  new RegionOfInterest("Zoom6", new Region(-0.74742, 0.10155, 0.15), 230)),
//      new TimedROI(1000, new RegionOfInterest("Zoom7", new Region(-0.747421, 0.101625, 0.1), 250)),
//      new TimedROI(800,  new RegionOfInterest("Zoom8", new Region(-0.7474215, 0.10163, 0.08), 280)),
//      new TimedROI(900,  new RegionOfInterest("Zoom9", new Region(-0.74742155, 0.101631, 0.06), 300)),
//      new TimedROI(1000, new RegionOfInterest("Zoom10", new Region(-0.74742157, 0.101632, 0.05), 320)),
//      new TimedROI(900,  new RegionOfInterest("Zoom11", new Region(-0.74742158, 0.1016325, 0.04), 350)),
//      new TimedROI(800,  new RegionOfInterest("Zoom12", new Region(-0.74742159, 0.1016326, 0.03), 380)),
//      new TimedROI(1000, new RegionOfInterest("Zoom13", new Region(-0.74742160, 0.10163265, 0.02), 420)),
//      new TimedROI(900,  new RegionOfInterest("Zoom14", new Region(-0.747421605, 0.10163266, 0.015), 450)),
//      new TimedROI(800,  new RegionOfInterest("Zoom15", new Region(-0.747421607, 0.101632665, 0.01), 500)),
//      new TimedROI(1000, new RegionOfInterest("Zoom16", new Region(-0.747421608, 0.101632667, 0.008), 550)),
//      new TimedROI(900,  new RegionOfInterest("Zoom17", new Region(-0.7474216085, 0.1016326675, 0.005), 600)),
//      new TimedROI(800,  new RegionOfInterest("Zoom18", new Region(-0.7474216086, 0.1016326676, 0.003), 650)),
//      new TimedROI(1000, new RegionOfInterest("Zoom19", new Region(-0.74742160865, 0.10163266765, 0.002), 700)),
//      new TimedROI(900,  new RegionOfInterest("Zoom20", new Region(-0.747421608655, 0.101632667655, 0.001), 750))
//    );
//
//    CompletableFuture.runAsync(() -> {
//      for (var step : exploration) {
//        try {
//          Thread.sleep(step.waitMs());
//        } catch (InterruptedException e) {
//          throw new RuntimeException(e);
//        }
//        mandelbrot.jumpToRegionOfInterest(step.roi());
//      }
//    });
  }
}