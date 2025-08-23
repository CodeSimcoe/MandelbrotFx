package com.codesimcoe.mandelbrotfx;

import com.codesimcoe.mandelbrotfx.component.PaletteCellFactory;
import com.codesimcoe.mandelbrotfx.fractal.BurningShipFractal;
import com.codesimcoe.mandelbrotfx.fractal.CelticFractal;
import com.codesimcoe.mandelbrotfx.fractal.Fractal;
import com.codesimcoe.mandelbrotfx.fractal.JuliaFractal;
import com.codesimcoe.mandelbrotfx.fractal.MandelbrotFractal;
import com.codesimcoe.mandelbrotfx.fractal.TricornFractal;
import com.codesimcoe.mandelbrotfx.music.Music;
import com.codesimcoe.mandelbrotfx.palette.ColorPalette;
import com.codesimcoe.mandelbrotfx.palette.GradientColorPalettes;
import com.codesimcoe.mandelbrotfx.palette.GrayscaleColorPalette;
import com.codesimcoe.mandelbrotfx.palette.SpectrumColorPalette;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.stream.IntStream;

/**
 * Aims at having a fast rendering, at the cost of low precision
 */
public class Mandelbrot {

  record NamedMusic(String name, Media media) implements Named {
    NamedMusic(final String name, final String path) {
      this(name, new Media(new File(path).toURI().toString()));
    }

    @Override
    public String getName() {
      return this.name;
    }
  }

  private static final double ZOOM_FACTOR = 2;

  private final BorderPane root;

  // Used fractal algorithm
  private final ObjectProperty<Fractal> fractal = new SimpleObjectProperty<>();

  // Used color palette
  private final ObjectProperty<ColorPalette> colorPalette = new SimpleObjectProperty<>();

  // Display position
  private final BooleanProperty displayPosition = new SimpleBooleanProperty(true);

  // Max algorithm iterations
  private final IntegerProperty max = new SimpleIntegerProperty();

  // Color offset
  private final IntegerProperty colorOffsetProperty = new SimpleIntegerProperty();

  // Music
  private final ObjectProperty<NamedMusic> musicProperty = new SimpleObjectProperty<>();

  private final DoubleProperty musicVolumeProperty = new SimpleDoubleProperty(0.5);

  // Image size, in pixels
  private final int width;
  private final int height;

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
  private int[] colors;

  // Media player
  private MediaPlayer mediaPlayer;
  private boolean musicPlaying = false;

  public Mandelbrot(
    final int width,
    final int height,
    final int max) {

    // Fractals
    Fractal[] fractals = {
      MandelbrotFractal.INSTANCE,
      BurningShipFractal.INSTANCE,
      TricornFractal.INSTANCE,
      CelticFractal.INSTANCE,
      new JuliaFractal("Julia - Dragon", -0.8, 0.156),
      new JuliaFractal("Julia - Rabbit", -0.123, 0.745),
      new JuliaFractal("Julia - Feather", -0.4, 0.6),
      new JuliaFractal("Julia - Spiral", 0.285, 0.01),
      new JuliaFractal("Julia - Seahorse tail", -0.70176, -0.3842),
      new JuliaFractal("Julia - Siegel Disk", -0.391, -0.587),
      new JuliaFractal("Julia - San Marco", -0.75, 0),
      new JuliaFractal("Julia - Dendrite", 0, 1),
    };

    this.fractal.set(fractals[0]);

    // Color palettes
    ColorPalette[] palettes = {
      new SpectrumColorPalette(),
      new GrayscaleColorPalette(),
      GradientColorPalettes.BLUE_ORANGE,
      GradientColorPalettes.OCEAN,
      GradientColorPalettes.SUNSET,
      GradientColorPalettes.NEON,
      GradientColorPalettes.NATURE,
      GradientColorPalettes.COFFEE,
      GradientColorPalettes.TROPICAL,
      GradientColorPalettes.MOON
    };
    this.colorPalette.set(palettes[0]);

    this.width = width;
    this.height = height;
    this.max.set(max);

    this.iterationsPixels = new int[this.width][this.height];

    // Music
    NamedMusic[] musics = {
      new NamedMusic("Ambient 1", Music.MUSIC_AMBIENT_1),
      new NamedMusic("Ambient 2", Music.MUSIC_AMBIENT_2)
    };
    this.musicProperty.set(musics[0]);
    this.createMediaPlayer();

    // Image
    this.imagePixels = new int[width * height];
    Canvas canvas = new Canvas(width, height);

    this.graphicsContext = canvas.getGraphicsContext2D();
    this.pixelWriter = this.graphicsContext.getPixelWriter();

    // Algorithm
    Label fractalAlgorithmLabel = new Label("Algorithm");
    ComboBox<Fractal> fractalComboBox = new ComboBox<>();
    fractalComboBox.setConverter(new NamedConverter<>());
    fractalComboBox.getItems().setAll(fractals);
    fractalComboBox.valueProperty().bindBidirectional(this.fractal);
    fractalComboBox.valueProperty().addListener((_, _, _) -> this.reset());

    // Color palette
    Label colorPaletteLabel = new Label("Color palette");
    ComboBox<ColorPalette> colorPaletteComboBox = new ComboBox<>();
    colorPaletteComboBox.setConverter(new NamedConverter<>());
    PaletteCellFactory.apply(colorPaletteComboBox);
    colorPaletteComboBox.getItems().setAll(palettes);
    colorPaletteComboBox.valueProperty().bindBidirectional(this.colorPalette);
    colorPaletteComboBox.valueProperty().addListener((_, _, newValue) -> {
      this.colors = newValue.computeColors(this.max.get());
      this.computeColors();
      this.drawImage();
    });

    // Max iterations
    Label maxIterationsLabel = new Label("Max iterations");
    Slider maxIterationsSlider = newSlider(0, 1_000, 100, this.max);
    this.max.addListener((_, _, newValue) -> {
      this.colors = this.colorPalette.get().computeColors(newValue.intValue());
      this.computeColors();
      this.update();
    });

    // Color offset
    Label colorOffsetLabel = new Label("Color offset");
    Slider colorOffsetSlider = newSlider(0, max, 64, this.colorOffsetProperty);
    this.colorOffsetProperty.addListener((_, _, _) -> {
      this.computeColors();
      this.drawImage();
    });

    // Reset
    Button resetPositionButton = new Button("Reset position");
    resetPositionButton.setOnAction(_ -> this.reset());

    // Display position
    CheckBox displayPositionCheckBox = new CheckBox("Display position / scale");
    displayPositionCheckBox.selectedProperty().bindBidirectional(this.displayPosition);
    displayPositionCheckBox.selectedProperty().addListener((_, _, _) -> this.drawImage());

    // Music
    Label ambientMusicLabel = new Label("Ambient music");

    // ComboBox to select music
    ComboBox<NamedMusic> musicSelectionComboBox = new ComboBox<>();
    musicSelectionComboBox.getItems().setAll(musics);
    musicSelectionComboBox.valueProperty().bindBidirectional(this.musicProperty);
    musicSelectionComboBox.setConverter(new NamedConverter<>());
    musicSelectionComboBox.setOnAction(_ -> {
      // Stop current track
      this.mediaPlayer.stop();
      this.createMediaPlayer();
    });

    Label musicVolumeLabel = new Label("Volume");
    Slider musicVolumeSlider = newSlider(0, 1, .25, this.musicVolumeProperty);

    Button playPauseButton = new Button("▶ Play");
    playPauseButton.setOnAction(_ -> {
      if (this.musicPlaying) {
        this.mediaPlayer.pause();
        playPauseButton.setText("▶ Play");
        this.musicPlaying = false;
      } else {
        this.mediaPlayer.play();
        playPauseButton.setText("⏸ Pause");
        this.musicPlaying = true;
      }
    });

    VBox settingsBox = new VBox(
      5,
      fractalAlgorithmLabel,
      fractalComboBox,

      colorPaletteLabel,
      colorPaletteComboBox,

      maxIterationsLabel,
      maxIterationsSlider,

      colorOffsetLabel,
      colorOffsetSlider,

      resetPositionButton,
      displayPositionCheckBox,

      ambientMusicLabel,
      musicSelectionComboBox,
      musicVolumeLabel,
      musicVolumeSlider,
      playPauseButton
    );
    settingsBox.setPadding(new Insets(5));
    settingsBox.setPrefWidth(Configuration.SETTINGS_WIDTH);

    // Assemble (border pane)
    this.root = new BorderPane();
    this.root.setCenter(canvas);
    this.root.setRight(settingsBox);

    // Initialize and cache all available colors
    this.colors = this.colorPalette.get().computeColors(max);

    // Actions
    canvas.setOnMousePressed(e -> {
      if (e.isPrimaryButtonDown()) {
        // Move
        this.move(e.getX(), e.getY());
      }
    });

    // Zoom in and out
    canvas.setOnScroll(e -> {
      double mouseX = e.getX();
      double mouseY = e.getY();

      // Step 1: fractal coordinates under mouse before zoom
      double fxBefore = this.xPixelsToValue(mouseX);
      double fyBefore = this.yPixelsToValue(mouseY);

      // Step 2: zoom
      if (e.getDeltaY() > 0) {
        this.zoomIn();
      } else {
        if (this.regionSize >= Configuration.MAX_REGION_SIZE) return;
        this.zoomOut();
      }

      // Step 3: fractal coordinates under mouse after zoom
      double fxAfter = this.xPixelsToValue(mouseX);
      double fyAfter = this.yPixelsToValue(mouseY);

      // Step 4: adjust center so the point stays under the mouse
      this.xc += fxBefore - fxAfter;
      this.yc += fyBefore - fyAfter;

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

    Fractal algorithm = this.fractal.get();
    int max = this.max.get();

    // Parallelize computations
    // Cache locality : order matters
    IntStream.range(0, this.height)
      .parallel()
      .forEach(y -> {
        double y0 = this.yPixelsToValue(y);
        for (int x = 0; x < this.width; x++) {
          double x0 = this.xPixelsToValue(x);
          int iterations = algorithm.compute(x0, y0, max);
          this.iterationsPixels[x][y] = iterations;
        }
      });


    this.computeColors();
    this.drawImage();
  }

  private void computeColors() {

    int max = this.max.get();

    if (max == 0) return;

    // Determine color
    // Apply offset
    int color;
    for (int x = 0; x < this.width; x++) {
      for (int y = 0; y < this.height; y++) {
        int iterations = this.iterationsPixels[x][y];
        if (iterations == max) {
          color = this.colors[max];
        } else {
          int colorIndex = (iterations + this.colorOffsetProperty.intValue()) % max;
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

    if (this.displayPosition.get()) {
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
  }

  private static Slider newSlider(
    final double min,
    final double max,
    final double majorTickUnit,
    final Property<Number> property) {

    Slider slider = new Slider();

    slider.setMin(min);
    slider.setMax(max);

    slider.setMinorTickCount(0);
    slider.setMajorTickUnit(majorTickUnit);

    slider.setShowTickMarks(true);
    slider.setShowTickLabels(true);

    slider.valueProperty().bindBidirectional(property);

    return slider;
  }

  private void createMediaPlayer() {
    this.mediaPlayer = new MediaPlayer(this.musicProperty.get().media());
    this.mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
    this.mediaPlayer.volumeProperty().bind(this.musicVolumeProperty);
    if (this.musicPlaying) {
      this.mediaPlayer.play();
    }
  }
}