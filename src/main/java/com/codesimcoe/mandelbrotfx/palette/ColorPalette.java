package com.codesimcoe.mandelbrotfx.palette;

import com.codesimcoe.mandelbrotfx.Named;

public interface ColorPalette extends Named {

  // Color as argb int
  int[] computeColors(int max);
}
