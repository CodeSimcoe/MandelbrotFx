package com.codesimcoe.mandelbrotfx.escape;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class EscapeViewer extends Application {

  private static final double WIDTH = 1024;
  private static final double HEIGHT = 1024;

  private static final int POINTS_COUNT = 25;

  private final Line[] lines = new Line[POINTS_COUNT];

  private final double centerX;
  private final double centerY;

  public EscapeViewer() {

    this.centerX = WIDTH / 2;
    this.centerY = HEIGHT / 2;

    for (int i = 0; i < POINTS_COUNT; i++) {
      Line line = new Line();
      line.setStroke(Color.CORNFLOWERBLUE);
      this.lines[i] = line;
    }
  }

  @Override
  public void start(final Stage primaryStage) {

    // X axis
    Line xAxis = new Line(0, this.centerY, WIDTH, this.centerY);
    xAxis.setStroke(Color.WHITE);

    // Y axis
    Line yAxis = new Line(this.centerX, 0, this.centerX, HEIGHT);
    yAxis.setStroke(Color.WHITE);

    Pane root = new Pane(xAxis, yAxis);
    root.getChildren().addAll(this.lines);
    Scene scene = new Scene(root, WIDTH, HEIGHT, Color.BLACK);

    // Ticks and labels
    for (double i = -1; i <= 1; i+=.5) {
      if (i == 0) {
        Text label = new Text(this.centerX + 5, this.centerY + 15, "0");
        label.setFill(Color.WHITE);
        root.getChildren().add(label);
        continue;
      }

      // X ticks
      double x = this.centerX + i * (WIDTH / 2);
      Line xtick = new Line(x, this.centerY - 5, x, this.centerY + 5);
      xtick.setStroke(Color.WHITE);
      Text xlabel = new Text(x - 10, this.centerY + 20, String.valueOf(i));
      xlabel.setFill(Color.WHITE);

      // Y ticks
      double y = this.centerY - i * (HEIGHT / 2); // invert Y axis
      Line ytick = new Line(this.centerX - 5, y, this.centerX + 5, y);
      ytick.setStroke(Color.WHITE);
      Text ylabel = new Text(this.centerX + 10, y + 5, String.valueOf(i));
      ylabel.setFill(Color.WHITE);

      root.getChildren().addAll(xtick, xlabel, ytick, ylabel);
    }

    root.setOnMouseMoved(this::computeLines);

    primaryStage.setScene(scene);
    primaryStage.setOnCloseRequest(_ -> System.exit(0));
    primaryStage.show();
  }

  private void computeLines(final MouseEvent e) {
    // Map mouse position -> complex [-1,1]
    double cx = (e.getX() - this.centerX) / (WIDTH / 2);
    double cy = (this.centerY - e.getY()) / (HEIGHT / 2);

    // Hide all lines initially
    for (Line line : this.lines) {
      line.setVisible(false);
    }

    double zx = 0, zy = 0;
    double prevScreenX = this.centerX;
    double prevScreenY = this.centerY;

    for (int i = 0; i < POINTS_COUNT; i++) {
      // Mandelbrot iteration
      double newZx = zx * zx - zy * zy + cx;
      double newZy = 2 * zx * zy + cy;

      // Stop if modulus > 2 (squared > 4)
      if (newZx * newZx + newZy * newZy > 4.0) {
        break;
      }

      // Convert to screen coordinates
      double screenX = this.centerX + newZx * (WIDTH / 2);
      double screenY = this.centerY - newZy * (HEIGHT / 2);

      // Update line
      this.lines[i].setStartX(prevScreenX);
      this.lines[i].setStartY(prevScreenY);
      this.lines[i].setEndX(screenX);
      this.lines[i].setEndY(screenY);
      this.lines[i].setVisible(true);

      zx = newZx;
      zy = newZy;
      prevScreenX = screenX;
      prevScreenY = screenY;
    }
  }
}
