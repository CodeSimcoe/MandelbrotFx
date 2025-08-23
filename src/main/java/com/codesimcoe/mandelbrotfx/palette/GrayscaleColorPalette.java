package com.codesimcoe.mandelbrotfx.palette;

public enum GrayscaleColorPalette implements ColorPalette {

  INSTANCE;

  @Override
  public String getName() {
    return "Grayscale";
  }

  @Override
  public int[] computeColors(final int max) {
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
