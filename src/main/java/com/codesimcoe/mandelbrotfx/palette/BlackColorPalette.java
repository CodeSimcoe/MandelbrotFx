package com.codesimcoe.mandelbrotfx.palette;

import javafx.scene.paint.Color;

import java.util.Arrays;

public class BlackColorPalette implements ColorPalette {

  @Override
  public String getName() {
    return "Black & White";
  }

  @Override
  public Color[] getKeyColors() {
    return new Color[] { Color.BLACK, Color.WHITE };
  }

  @Override
  public int[] computeColors(int max) {

    // Black for points inside the set, white outside
    int[] colors = new int[max + 1];
    Arrays.fill(colors, 0xFFFFFFFF);
    colors[max] = 0xFF000000;

    return colors;
  }
}
