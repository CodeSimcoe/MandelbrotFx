package com.codesimcoe.mandelbrotfx.component;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public final class ColorPreviewSquare {

  private ColorPreviewSquare() {
    // Non-instantiable
  }

  public static Node newColorPreviewSquare(final Color color) {
    Rectangle rectangle = new Rectangle(12, 12, color);
    rectangle.setStroke(Color.BLACK);
    return rectangle;
  }
}
