package com.codesimcoe.mandelbrotfx.palette;

import com.codesimcoe.mandelbrotfx.Named;
import javafx.scene.paint.Color;

public interface ColorPalette extends Named {

  // Color as argb int
  int[] computeColors(int max);

  // Used for interpolation (gradient palette) and preview
  Color[] getKeyColors();
}
