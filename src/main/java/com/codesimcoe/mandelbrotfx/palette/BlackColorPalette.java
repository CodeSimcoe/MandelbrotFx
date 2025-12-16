package com.codesimcoe.mandelbrotfx.palette;

import javafx.scene.paint.Color;

public class BlackColorPalette implements ColorPalette {

  @Override
  public String getName() {
    return "Black";
  }

  @Override
  public Color[] getKeyColors() {
    return new Color[] { Color.BLACK, Color.WHITE };
  }

  @Override
  public int[] computeColors(int max) {

    int[] colors = new int[max + 1];

    colors[max] = 0xFF000000; // Black for points inside the set

    for (int i = 0; i < max; i++) {
      colors[i] = 0xFFFFFFFF; // White outside
    }

    return colors;
  }
}
