package com.codesimcoe.mandelbrotfx.palette;

import javafx.scene.paint.Color;

public enum BlueOrangeGradientColorPalette implements ColorPalette {

  INSTANCE;

  @Override
  public String getName() {
    return "Blue-Orange gradient";
  }

  @Override
  public int[] computeColors(final int numColors) {
    // Generate a smooth gradient with as many colors as iterations
    Color[] gradient = this.createSmoothGradient(numColors + 1);

    int[] colors = new int[numColors + 1];
    for (int i = 0; i <= numColors; i++) {
      if (i == numColors) {
        colors[i] = 0xFF000000; // Black for inside the set
      } else {
        Color c = gradient[i];
        int r = (int) (c.getRed() * 255);
        int g = (int) (c.getGreen() * 255);
        int b = (int) (c.getBlue() * 255);
        colors[i] = (0xFF << 24) | (r << 16) | (g << 8) | b;
      }
    }
    return colors;
  }

  private Color[] createSmoothGradient(final int numColors) {
    Color[] gradient = new Color[numColors];
    Color[] keyColors = {
        Color.rgb(0, 7, 100),    // Deep blue
        Color.rgb(32, 107, 203), // Lighter blue
        Color.rgb(237, 255, 255),// Near white
        Color.rgb(255, 170, 0),  // Orange
        Color.rgb(0, 2, 0)       // Near black/greenish
    };

    for (int i = 0; i < numColors; i++) {
      double position = (double) i / (numColors - 1) * (keyColors.length - 1);
      int index = (int) position;
      double fraction = position - index;

      if (index >= keyColors.length - 1) {
        gradient[i] = keyColors[keyColors.length - 1];
      } else {
        gradient[i] = keyColors[index].interpolate(keyColors[index + 1], fraction);
      }
    }
    return gradient;
  }
}
