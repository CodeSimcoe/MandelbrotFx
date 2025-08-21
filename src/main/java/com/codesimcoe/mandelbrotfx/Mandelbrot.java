package com.codesimcoe.mandelbrotfx;

import com.codesimcoe.mandelbrotfx.fractal.Fractal;
import com.codesimcoe.mandelbrotfx.fractal.JuliaFractal;
import com.codesimcoe.mandelbrotfx.fractal.MandelbrotFractal;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import java.util.stream.IntStream;

/**
 * Aims at having a fast rendering, at the cost of low precision
 */
public class Mandelbrot {

  private static final double ZOOM_FACTOR = 2;

  private final BorderPane root;

  // Used fractal algorithm
  private final ObjectProperty<NamedFractal> fractal = new SimpleObjectProperty<>();

  // Image size, in pixels
  private final int width;
  private final int height;

  // Max algorithm iterations
  private final int max;

  // Center
  private double xc;
  private double yc;

  // Region size
  private double regionSize;

  // Mandelbrot set result
  // Iterations are store for each pixel
  private final int[][] iterationsPixels;

  // Image pixel content
  private final int[] imagePixels;

  // Canvas graphics context
  private final GraphicsContext graphicsContext;
  private final PixelWriter pixelWriter;

  // Cached colors (int argb values)
  private final int[] colors;

  private final Configuration configuration;

  public Mandelbrot(
    final int width,
    final int height,
    final int max) {

    // Fractals
    NamedFractal mandelbrot = new NamedFractal("Mandelbrot", MandelbrotFractal.INSTANCE);
    NamedFractal julia1 = new NamedFractal("Julia - Rabbit", new JuliaFractal(-0.8, 0.156));
    NamedFractal julia2 = new NamedFractal("Julia - Cubic Like",   new JuliaFractal(-0.4, 0.6));
    NamedFractal julia3 = new NamedFractal("Julia - Spiral", new JuliaFractal(0.285, 0.01));
    NamedFractal julia4 = new NamedFractal("Julia - Seahorse tail", new JuliaFractal(-0.70176, -0.3842));

    this.fractal.setValue(mandelbrot);

    this.width = width;
    this.height = height;
    this.max = max;

    this.iterationsPixels = new int[this.width][this.height];

    this.configuration = Configuration.getInstance();

    // Image
    this.imagePixels = new int[width * height];
    Canvas canvas = new Canvas(width, height);

    this.graphicsContext = canvas.getGraphicsContext2D();
    this.pixelWriter = this.graphicsContext.getPixelWriter();

    // Algorithm
    Label fractalAlgorithmLabel = new Label("Algorithm");
    ComboBox<NamedFractal> fractalComboBox = new ComboBox<>();
    fractalComboBox.setConverter(new StringConverter<>() {
      @Override
      public String toString(final NamedFractal namedFractal) {
        return namedFractal.name();
      }

      @Override
      public NamedFractal fromString(final String s) {
        return null;
      }
    });
    fractalComboBox.getItems().addAll(mandelbrot, julia1, julia2, julia3, julia4);
    fractalComboBox.valueProperty().bindBidirectional(this.fractal);
    fractalComboBox.valueProperty().addListener((_, _, _) -> this.reset());

    // Color offset
    Label colorOffsetLabel = new Label("Color offset");
    Slider colorOffsetSlider = this.newSlider(0, max, 0, 64, this.configuration.getColorOffsetProperty());
    this.configuration.getColorOffsetProperty().addListener((_, _, _) -> {
      this.computeColors();
      this.drawImage();
    });

    // Reset
    Button resetPositionButton = new Button("Reset position");
    resetPositionButton.setOnAction(_ -> this.reset());

    VBox settingsBox = new VBox(
      5,
      fractalAlgorithmLabel,
      fractalComboBox,
      colorOffsetLabel,
      colorOffsetSlider,
      resetPositionButton
    );
    settingsBox.setPadding(new Insets(5));
    settingsBox.setPrefWidth(Configuration.SETTINGS_WIDTH);

    // Assemble (border pane)
    this.root = new BorderPane();
    this.root.setCenter(canvas);
    this.root.setRight(settingsBox);

    // Initialize and cache all available colors
    this.colors = new int[max + 1];
    for (int i = 0; i <= max; i++) {
      double hue = 360.0 * i / max;
      double brightness = (i == max) ? 0 : 1;
      Color color = Color.hsb(hue, 1, brightness);

      int a = 255;
      int r = (int) (color.getRed() * 255);
      int g = (int) (color.getGreen() * 255);
      int b = (int) (color.getBlue() * 255);

      int argb = (a << 24) | (r << 16) | (g << 8) | b;

      this.colors[i] = argb;
    }

    // Actions
    this.root.setOnMousePressed(e -> {
      if (e.isPrimaryButtonDown()) {
        // Move
        this.move(e.getX(), e.getY());
      }
    });

    // Zoom in and out
    this.root.setOnScroll(e -> {
      if (e.getDeltaY() > 0) {
        this.zoomIn();
      } else {
        this.zoomOut();
      }

      this.update();
    });
  }

  private void reset() {
    this.setRegion(Configuration.DEFAULT_XC, Configuration.DEFAULT_YC, Configuration.DEFAULT_SIZE);
    this.update();
  }

  public Pane getRoot() {
    return this.root;
  }

  private void move(
    final double x,
    final double y) {

    // Pixels to value
    this.xc = this.xPixelsToValue(x);
    this.yc = this.yPixelsToValue(y);

    this.update();
  }

  private void zoomIn() {
    this.regionSize /= ZOOM_FACTOR;
  }

  private void zoomOut() {
    this.regionSize *= ZOOM_FACTOR;
  }

  // Concert a pixel abscissa position to "real" mathematical value
  private double xPixelsToValue(final double x) {
    return this.xc - this.regionSize / 2 + this.regionSize * x / this.width;
  }

  // Concert a pixel ordinate position to "real" mathematical value
  private double yPixelsToValue(final double y) {
    return this.yc - this.regionSize / 2 + this.regionSize * y / this.height;
  }

  /**
   * Set the region we are looking at, defined by its center (xc, yc) and its size
   */
  public void setRegion(
    final double xc,
    final double yc,
    final double regionSize) {

    this.regionSize = regionSize;
    this.xc = xc;
    this.yc = yc;
  }

  public void update() {

    Fractal algorithm = this.fractal.get().fractal();

    // Parallelize computations
    IntStream.range(0, this.width)
      .parallel()
      .forEach(x -> {
        double x0 = this.xPixelsToValue(x);

        for (int y = 0; y < this.height; y++) {
          double y0 = this.yPixelsToValue(y);

          // Compute iterations
          int iterations = algorithm.compute(x0, y0, this.max);

          // Store result for this pixel
          this.iterationsPixels[x][y] = iterations;
        }
      });

    this.computeColors();
    this.drawImage();
  }

  private void computeColors() {
    // Determine color
    // Apply offset
    int color;
    for (int x = 0; x < this.width; x++) {
      for (int y = 0; y < this.height; y++) {
        int iterations = this.iterationsPixels[x][y];
        if (iterations == this.max) {
          color = this.colors[this.max];
        } else {
          int colorIndex = (iterations + this.configuration.getColorOffsetProperty().intValue()) % this.max;
          color = this.colors[colorIndex];
        }

        this.imagePixels[y * this.height + x] = color;
      }
    }
  }

  private void drawImage() {
    // Draw image
    this.pixelWriter.setPixels(
      0,
      0,
      this.width,
      this.height,
      PixelFormat.getIntArgbInstance(),
      this.imagePixels,
      0,
      this.width
    );

    // Info (zoom and centering)
    // Draw shadow
    this.graphicsContext.setStroke(Color.BLACK);
    this.graphicsContext.strokeText("region's size: " + this.regionSize, 4, 15);
    this.graphicsContext.strokeText("x: " + this.xc, 4, 35);
    this.graphicsContext.strokeText("y: " + this.yc, 4, 55);

    // Draw plain text
    this.graphicsContext.setStroke(Color.WHITE);
    this.graphicsContext.strokeText("region's size: " + this.regionSize, 5, 16);
    this.graphicsContext.strokeText("x: " + this.xc, 5, 36);
    this.graphicsContext.strokeText("y: " + this.yc, 5, 56);
  }

  private Slider newSlider(
    final double min,
    final double max,
    final int minorTickCount,
    final int majorTickUnit,
    final Property<Number> property) {

    Slider slider = new Slider();

    slider.setMin(min);
    slider.setMax(max);

    slider.setMinorTickCount(minorTickCount);
    slider.setMajorTickUnit(majorTickUnit);

    slider.setShowTickMarks(true);
    slider.setShowTickLabels(true);

    slider.valueProperty().bindBidirectional(property);

    return slider;
  }
}