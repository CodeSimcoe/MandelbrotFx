package com.codesimcoe.mandelbrotfx.escape;

import com.codesimcoe.mandelbrotfx.Configuration;
import com.codesimcoe.mandelbrotfx.ValueComplex;
import com.codesimcoe.mandelbrotfx.Viewport;
import com.codesimcoe.mandelbrotfx.fractal.Fractal;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.Locale;

public class EscapeViewer {

  // The pane to attach viewer to
  private final Pane pane;

  private final Text coordinatesText = new Text();
  private final Line[] escapeLines = new Line[Configuration.ESCAPE_MAX_POINTS];
  private final Circle[] escapeDots = new Circle[Configuration.ESCAPE_MAX_POINTS];
  private final Text[] escapeTexts = new Text[Configuration.ESCAPE_MAX_POINTS];

  private final IntegerProperty escapeMaxPointsProperty = new SimpleIntegerProperty(Configuration.DEFAULT_ESCAPE_POINTS);

  private final EventHandler<MouseEvent> escapeMouseMovedHandler = this::onEscapeMouseMoved;

  private final Viewport viewport;

  private Fractal fractal;
  private double x;
  private double y;

  private boolean textsVisible = true;

  public EscapeViewer(
    Pane pane,
    Viewport viewport,
    Fractal fractal) {

    this.pane = pane;
    this.viewport = viewport;
    this.fractal = fractal;

    this.initialize();
  }

  private void initialize() {

    this.coordinatesText.setStroke(Color.DARKRED);
    this.coordinatesText.setFont(Font.font("Monospace", 12));

    for (int i = 0; i < Configuration.ESCAPE_MAX_POINTS; i++) {

      Line line = new Line();
      line.setMouseTransparent(true);
      line.setStroke(Color.CORNFLOWERBLUE);
      line.setStrokeWidth(1.5);
      line.setOpacity(.75);

      Circle dot = new Circle(3, Color.MEDIUMSLATEBLUE);
      dot.setMouseTransparent(true);
      dot.setOpacity(.75);

      Text text;
      if (i == 0) {
        text = new Text("");
      } else {
        text = new Text("z" + (i + 1));
      }

      this.escapeLines[i] = line;
      this.escapeDots[i] = dot;
      this.escapeTexts[i] = text;
    }

    // Color first point differently
    this.escapeDots[0].setFill(Color.DARKRED);
  }

  public void update(boolean enabled) {
    ObservableList<Node> children = this.pane.getChildren();
    if (enabled) {
      children.add(this.coordinatesText);
      children.addAll(this.escapeLines);
      children.addAll(this.escapeDots);
      children.addAll(this.escapeTexts);

      this.pane.addEventHandler(MouseEvent.MOUSE_MOVED, this.escapeMouseMovedHandler);

    } else {
      children.remove(this.coordinatesText);
      children.removeAll(this.escapeLines);
      children.removeAll(this.escapeDots);
      children.removeAll(this.escapeTexts);

      this.pane.removeEventHandler(MouseEvent.MOUSE_MOVED, this.escapeMouseMovedHandler);
    }
  }

  private void onEscapeMouseMoved(MouseEvent event) {
    this.x = event.getX();
    this.y = event.getY();
    this.updateUI();
  }

  public void updateUI() {
    double re = this.viewport.screenToRe(this.x);
    double im = this.viewport.screenToIm(this.y);

    String coordsText = String.format(Locale.ROOT, "z1 = c = [%.2f, %.2f]", re, im);
    this.coordinatesText.setText(coordsText);
    this.coordinatesText.setX(this.x + 12);
    this.coordinatesText.setY(this.y + 12);

    for (int i = 0; i < Configuration.ESCAPE_MAX_POINTS; i++) {
      this.escapeLines[i].setVisible(false);
      this.escapeDots[i].setVisible(false);
      this.escapeTexts[i].setVisible(false);
    }

    int maxEscapePoints = this.escapeMaxPointsProperty.get();

    ValueComplex z = this.fractal.initialZ(re, im);
    ValueComplex zPrev = ValueComplex.ZERO;
    ValueComplex c = this.fractal.constantC(re, im);

    double prevScreenX = this.viewport.complexToX(z.re());
    double prevScreenY = this.viewport.complexToY(z.im());

    for (int i = 0; i < maxEscapePoints; i++) {
      ValueComplex next = this.fractal.computeIteration(z, zPrev, c);

      zPrev = z;
      z = next;

      double screenX = this.viewport.complexToX(z.re());
      double screenY = this.viewport.complexToY(z.im());

      Line line = this.escapeLines[i];
      line.setStartX(prevScreenX);
      line.setStartY(prevScreenY);
      line.setEndX(screenX);
      line.setEndY(screenY);
      line.setVisible(true);

      Circle dot = this.escapeDots[i];
      dot.setCenterX(screenX);
      dot.setCenterY(screenY);
      dot.setVisible(true);

      Text text = this.escapeTexts[i];
      text.setX(screenX + 5);
      text.setY(screenY + 5);
      text.setVisible(this.textsVisible);

      if (z.re() * z.re() + z.im() * z.im() > 4.0) {
        break;
      }

      prevScreenX = screenX;
      prevScreenY = screenY;
    }
  }

  public void setFractal(Fractal fractal) {
    this.fractal = fractal;
  }

  public IntegerProperty getEscapeMaxPointsProperty() {
    return this.escapeMaxPointsProperty;
  }

  void updateMaxPoints(int value) {
    this.escapeMaxPointsProperty.set(value);
    this.updateUI();
  }

  void toggleTextVisible() {
    this.textsVisible = !this.textsVisible;
    this.updateUI();
  }
}
