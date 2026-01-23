package com.codesimcoe.mandelbrotfx.escape;

import com.codesimcoe.mandelbrotfx.Configuration;
import com.codesimcoe.mandelbrotfx.Viewport;
import com.codesimcoe.mandelbrotfx.fractal.MandelbrotFractal;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.Objects;

public class EscapeViewerDemo extends Application {

  private static final int WIDTH = 800;
  private static final int HEIGHT = 800;
  private EscapeViewer escapeViewer;

  @Override
  public void start(Stage stage) {

    Pane root = new Pane();
    root.setPrefSize(WIDTH, HEIGHT);

    // Viewport (centered on origin, square view)
    Viewport viewport = new Viewport(
      0.0, 0.0,
      2.0,
      WIDTH,
      HEIGHT
    );

    // Axes
    Line xAxis = new Line(
      0, HEIGHT / 2.0,
      WIDTH,
      HEIGHT / 2.0
    );
    xAxis.setStroke(Color.BLACK);
    xAxis.setStrokeWidth(1);

    Line yAxis = new Line(
      WIDTH / 2.0, 0,
      WIDTH / 2.0,
      HEIGHT
    );
    yAxis.setStroke(Color.BLACK);
    yAxis.setStrokeWidth(1);

    root.getChildren().addAll(xAxis, yAxis);

    // Fractal
    // Escape viewer
    this.escapeViewer = new EscapeViewer(root, viewport, MandelbrotFractal.MANDELBROT);
    this.escapeViewer.getEscapeMaxPointsProperty().set(1);
    this.escapeViewer.update(true);

    InputStream iconResource = Objects.requireNonNull(this.getClass().getResourceAsStream("/icon.png"));
    stage.getIcons().add(new Image(iconResource));

    Scene scene = new Scene(root);
    stage.setTitle("Escape Viewer");
    stage.setScene(scene);

    scene.addEventFilter(
      KeyEvent.KEY_PRESSED, e -> {
        switch (e.getCode()) {
          case ADD -> this.adjustEscapePoints(1);
          case SUBTRACT -> this.adjustEscapePoints(-1);
          default -> IO.println(e.getCode());
        }
      }
    );

    scene.addEventFilter(
      ScrollEvent.SCROLL, e -> {
        if (e.getDeltaY() > 0) {
          this.adjustEscapePoints(1);
        } else {
          this.adjustEscapePoints(-1);
        }
      }
    );

    scene.addEventFilter(
      MouseEvent.MOUSE_PRESSED, e -> {
        if (e.isSecondaryButtonDown()) {
          this.escapeViewer.toggleTextVisible();
        }
      }
    );

    stage.show();
  }

  private void adjustEscapePoints(int delta) {
    IntegerProperty property = this.escapeViewer.getEscapeMaxPointsProperty();
    int value = Math.clamp(property.get() + delta, 1, Configuration.DEFAULT_ESCAPE_POINTS);
    this.escapeViewer.updateMaxPoints(value);
  }
}
