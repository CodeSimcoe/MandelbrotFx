package com.codesimcoe.mandelbrotfx.escape;

import com.codesimcoe.mandelbrotfx.Configuration;
import com.codesimcoe.mandelbrotfx.Viewport;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class EscapeViewer {

  // The pane to attach viewer to
  private final Pane pane;

  private final Line[] escapeLines = new Line[Configuration.ESCAPE_MAX_POINTS];
  private final Circle[] escapeDots = new Circle[Configuration.ESCAPE_MAX_POINTS];

  private final IntegerProperty escapeMaxPointsProperty = new SimpleIntegerProperty(Configuration.DEFAULT_ESCAPE_POINTS);
  
  private final EventHandler<MouseEvent> escapeMouseMovedHandler = this::onEscapeMouseMoved;

  private final Viewport viewport;

  public EscapeViewer(
    final Pane pane,
    final Viewport viewport) {
    
    this.pane = pane;
    this.viewport = viewport;

    this.initialize();
  }

  private void initialize() {
    for (int i = 0; i < Configuration.ESCAPE_MAX_POINTS; i++) {

      Line line = new Line();
      line.setMouseTransparent(true);
      line.setStroke(Color.CORNFLOWERBLUE);
      line.setStrokeWidth(1.5);
      line.setOpacity(.75);

      Circle dot = new Circle(3, Color.MEDIUMSLATEBLUE);
      dot.setMouseTransparent(true);
      dot.setOpacity(.75);

      this.escapeLines[i] = line;
      this.escapeDots[i] = dot;
    }
  }

  public void update(final boolean enabled) {
    if (enabled) {
      this.pane.getChildren().addAll(this.escapeLines);
      this.pane.getChildren().addAll(this.escapeDots);

      this.pane.addEventHandler(MouseEvent.MOUSE_MOVED, this.escapeMouseMovedHandler);

    } else {
      this.pane.getChildren().removeAll(this.escapeLines);
      this.pane.getChildren().removeAll(this.escapeDots);

      this.pane.removeEventHandler(MouseEvent.MOUSE_MOVED, this.escapeMouseMovedHandler);
    }
  }

  private void onEscapeMouseMoved(final MouseEvent event) {

    // Map mouse position -> complex [-1,1]
    double cx = this.viewport.screenToRe(event.getX());
    double cy = this.viewport.screenToIm(event.getY());

    // Hide all lines initially
    for (int i = 0; i < Configuration.ESCAPE_MAX_POINTS; i++) {
      this.escapeLines[i].setVisible(false);
      this.escapeDots[i].setVisible(false);
    }

    double zx = 0, zy = 0;
    double prevScreenX = this.viewport.complexToX(0);
    double prevScreenY = this.viewport.complexToY(0);

    int maxEscapePoints = this.escapeMaxPointsProperty.get();
    for (int i = 0; i < maxEscapePoints; i++) {
      // Mandelbrot iteration
      double newZx = zx * zx - zy * zy + cx;
      double newZy = 2 * zx * zy + cy;

      // Convert to screen coordinates
      double screenX = this.viewport.complexToX(newZx);
      double screenY = this.viewport.complexToY(newZy);

      // Update line
      Line line = this.escapeLines[i];
      line.setStartX(prevScreenX);
      line.setStartY(prevScreenY);
      line.setEndX(screenX);
      line.setEndY(screenY);
      line.setVisible(true);

      // Update dot
      Circle dot = this.escapeDots[i];
      dot.setCenterX(screenX);
      dot.setCenterY(screenY);
      dot.setVisible(true);

      // Stop if modulus > 2 (squared > 4)
      if (newZx * newZx + newZy * newZy > 4.0) {
        break;
      }

      zx = newZx;
      zy = newZy;
      prevScreenX = screenX;
      prevScreenY = screenY;
    }
  }

  public IntegerProperty getEscapeMaxPointsProperty() {
    return this.escapeMaxPointsProperty;
  }
}
