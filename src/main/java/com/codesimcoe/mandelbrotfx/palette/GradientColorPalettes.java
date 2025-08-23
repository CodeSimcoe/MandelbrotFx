package com.codesimcoe.mandelbrotfx.palette;

import javafx.scene.paint.Color;

public final class GradientColorPalettes {

  public static final ColorPalette OCEAN = new GradientColorPalette(
    "Ocean",
    new Color[] {
      Color.rgb(0, 0, 50),
      Color.rgb(0, 50, 150),
      Color.rgb(0, 100, 200),
      Color.rgb(100, 200, 255),
      Color.rgb(200, 255, 255)
    }
  );

  public static final ColorPalette BLUE_ORANGE = new GradientColorPalette(
    "Blue / Orange",
    new Color[] {
      Color.rgb(0, 7, 100),
      Color.rgb(32, 107, 203),
      Color.rgb(237, 255, 255),
      Color.rgb(255, 170, 0),
      Color.rgb(0, 2, 0)
    }
  );

  public static final ColorPalette SUNSET = new GradientColorPalette(
    "Sunset",
    new Color[] {
      Color.rgb(30, 0, 60),
      Color.rgb(150, 0, 50),
      Color.rgb(255, 80, 0),
      Color.rgb(255, 200, 50),
      Color.rgb(255, 255, 220)
    }
  );

  public static final ColorPalette NEON = new GradientColorPalette(
    "Neon",
    new Color[] {
      Color.rgb(10, 0, 50),
      Color.rgb(100, 0, 200),
      Color.rgb(0, 200, 255),
      Color.rgb(255, 0, 255),
      Color.rgb(255, 100, 200)
    }
  );

  public static final ColorPalette NATURE = new GradientColorPalette(
    "Nature",
    new Color[] {
      Color.rgb(10, 30, 10),
      Color.rgb(30, 100, 30),
      Color.rgb(80, 160, 60),
      Color.rgb(200, 220, 100),
      Color.rgb(150, 100, 50)
    }
  );

  public static final ColorPalette COFFEE = new GradientColorPalette(
    "Coffee",
    new Color[] {
      Color.rgb(42, 26, 14),
      Color.rgb(84, 54, 30),
      Color.rgb(133, 94, 66),
      Color.rgb(186, 140, 95),
      Color.rgb(222, 185, 140)
    }
  );

  public static final ColorPalette TROPICAL = new GradientColorPalette(
    "Tropical",
    new Color[] {
      Color.rgb(0, 105, 92),
      Color.rgb(0, 150, 136),
      Color.rgb(255, 193, 7),
      Color.rgb(255, 87, 34),
      Color.rgb(244, 67, 54)
    }
  );

  public static final ColorPalette MOON = new GradientColorPalette(
    "Moon",
    new Color[] {
      Color.rgb(50, 50, 60),
      Color.rgb(120, 120, 130),
      Color.rgb(180, 180, 190),
      Color.rgb(220, 220, 225)
    }
  );


  private GradientColorPalettes() {
    // Constants
  }
}
