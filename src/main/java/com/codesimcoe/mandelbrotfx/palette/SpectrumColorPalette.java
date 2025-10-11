package com.codesimcoe.mandelbrotfx.palette;

import javafx.scene.paint.Color;

public class SpectrumColorPalette implements ColorPalette {

  private static final Color[] KEY_COLORS = { Color.RED, Color.GREEN, Color.BLUE, Color.BLACK };

  @Override
  public String getName() {
    return "Spectrum";
  }

  @Override
  public Color[] getKeyColors() {
    return KEY_COLORS;
  }

  @Override
  public int[] computeColors(int max) {
    int[] colors = new int[max + 1];
    for (int i = 0; i <= max; i++) {
      double hue = 360.0 * i / max;
      double brightness = (i == max) ? 0 : 1;
      Color color = Color.hsb(hue, 1, brightness);

      int a = 255;
      int r = (int) (color.getRed() * 255);
      int g = (int) (color.getGreen() * 255);
      int b = (int) (color.getBlue() * 255);

      int argb = (a << 24) | (r << 16) | (g << 8) | b;

      colors[i] = argb;
    }
    return colors;
  }
}
