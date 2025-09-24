package com.codesimcoe.mandelbrotfx.palette;

import javafx.scene.paint.Color;

public class GrayscaleColorPalette implements ColorPalette {

  private static final Color[] KEY_COLORS = { Color.WHITE, Color.GRAY, Color.BLACK };

  @Override
  public String getName() {
    return "Grayscale";
  }

  @Override
  public Color[] getKeyColors() {
    return KEY_COLORS;
  }

  @Override
  public int[] computeColors(int max) {
    int[] colors = new int[max + 1];

    for (int i = 0; i <= max; i++) {
      if (i == max) {
        colors[i] = 0xFF000000; // Black for points inside the set
      } else {
        int gray = (int) (255.0 * i / max);
        colors[i] = (0xFF << 24) | (gray << 16) | (gray << 8) | gray;
      }
    }

    return colors;
  }
}
