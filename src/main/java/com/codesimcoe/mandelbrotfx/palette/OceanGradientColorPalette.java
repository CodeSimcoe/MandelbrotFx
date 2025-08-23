package com.codesimcoe.mandelbrotfx.palette;

import javafx.scene.paint.Color;

public enum OceanGradientColorPalette implements ColorPalette {

  INSTANCE;

  @Override
  public String getName() {
    return "Ocean gradient";
  }

  @Override
  public int[] computeColors(final int max) {
    Color[] keyColors = {
      Color.rgb(0, 0, 50),
      Color.rgb(0, 50, 150),
      Color.rgb(0, 100, 200),
      Color.rgb(100, 200, 255),
      Color.rgb(200, 255, 255)
    };

    int[] colors = new int[max + 1];

    for (int i = 0; i <= max; i++) {
      if (i == max) {
        colors[i] = 0xFF000000; // Black for points inside the set
      } else {
        double position = (double) i / max * (keyColors.length - 1);
        int index = (int) position;
        double fraction = position - index;

        Color c;
        if (index >= keyColors.length - 1) {
          c = keyColors[keyColors.length - 1];
        } else {
          c = keyColors[index].interpolate(keyColors[index + 1], fraction);
        }

        int r = (int) (c.getRed() * 255);
        int g = (int) (c.getGreen() * 255);
        int b = (int) (c.getBlue() * 255);

        colors[i] = (0xFF << 24) | (r << 16) | (g << 8) | b; // ARGB
      }
    }

    return colors;
  }
}