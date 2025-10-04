package com.codesimcoe.mandelbrotfx.palette;

import javafx.scene.paint.Color;

public record GradientColorPalette(String name, Color[] keyColors) implements ColorPalette {

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public Color[] getKeyColors() {
    return this.keyColors;
  }

  @Override
  public int[] computeColors(int numColors) {
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

  private Color[] createSmoothGradient(int numColors) {
    Color[] gradient = new Color[numColors];

    for (int i = 0; i < numColors; i++) {
      double position = (double) i / (numColors - 1) * (this.keyColors.length - 1);
      int index = (int) position;
      double fraction = position - index;

      if (index >= this.keyColors.length - 1) {
        gradient[i] = this.keyColors[this.keyColors.length - 1];
      } else {
        gradient[i] = this.keyColors[index].interpolate(this.keyColors[index + 1], fraction);
      }
    }
    return gradient;
  }
}
