package com.codesimcoe.mandelbrotfx;

import com.codesimcoe.mandelbrotfx.component.PaletteCellFactory;
import com.codesimcoe.mandelbrotfx.fractal.BuffaloFractal;
import com.codesimcoe.mandelbrotfx.fractal.BurningShipFractal;
import com.codesimcoe.mandelbrotfx.fractal.CelticFractal;
import com.codesimcoe.mandelbrotfx.fractal.Fractal;
import com.codesimcoe.mandelbrotfx.fractal.JuliaFractal;
import com.codesimcoe.mandelbrotfx.fractal.MandelbrotFractal;
import com.codesimcoe.mandelbrotfx.fractal.PhoenixFractal;
import com.codesimcoe.mandelbrotfx.fractal.TricornFractal;
import com.codesimcoe.mandelbrotfx.music.Music;
import com.codesimcoe.mandelbrotfx.palette.ColorPalette;
import com.codesimcoe.mandelbrotfx.palette.GradientColorPalettes;
import com.codesimcoe.mandelbrotfx.palette.GrayscaleColorPalette;
import com.codesimcoe.mandelbrotfx.palette.SpectrumColorPalette;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.util.converter.NumberStringConverter;

import java.util.stream.IntStream;

/**
 * Aims at having a fast rendering, at the cost of low precision
 */
public class Mandelbrot {

  private final BorderPane root;

  // Used fractal algorithm
  private final ObjectProperty<Fractal> fractal = new SimpleObjectProperty<>();

  // Used color palette
  private final ObjectProperty<ColorPalette> colorPalette = new SimpleObjectProperty<>();

  // Region
  private final RegionProperty regionProperty = new RegionProperty(
    new SimpleDoubleProperty(),
    new SimpleDoubleProperty(),
    new SimpleDoubleProperty()
  );

  // Zoom mode
  private final ObjectProperty<ZoomMode> zoomModeProperty = new SimpleObjectProperty<>(Configuration.DEFAULT_ZOOM_MODE);

  // Zoom factor
  private final DoubleProperty zoomFactorProperty = new SimpleDoubleProperty(Configuration.DEFAULT_ZOOM_FACTOR);

  // Max algorithm iterations
  private final IntegerProperty max = new SimpleIntegerProperty();

  // Color offset
  private final IntegerProperty colorOffsetProperty = new SimpleIntegerProperty();

  // Music
  private final ObjectProperty<NamedMusic> musicProperty = new SimpleObjectProperty<>();
  private final DoubleProperty musicVolumeProperty = new SimpleDoubleProperty(Configuration.DEFAULT_MUSIC_VOLUME);

  // Image size, in pixels
  private final int width;
  private final int height;

  // Mandelbrot set result
  // Iterations are store for each pixel
  private final int[][] iterationsPixels;

  // Image pixel content
  private final int[] imagePixels;

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
      BuffaloFractal.INSTANCE,
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
      new PhoenixFractal("Phoenix 0.56667", 0.56667),
      new PhoenixFractal("Phoenix 0.75", 0.75),
      new PhoenixFractal("Phoenix 1", 1)
    };

    Fractal selectedFractal = fractals[0];
    this.fractal.set(selectedFractal);
    this.regionProperty.update(selectedFractal.getDefaultRegion());

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
    NamedMusic[] musics = Music.MUSICS.entrySet()
      .stream()
      .map(e -> new NamedMusic(e.getKey(), e.getValue()))
      .toArray(NamedMusic[]::new);
    this.musicProperty.set(musics[0]);
    this.createMediaPlayer();

    // Image
    this.imagePixels = new int[width * height];
    Canvas canvas = new Canvas(width, height);

    // Canvas graphics context
    this.pixelWriter = canvas.getGraphicsContext2D().getPixelWriter();

    VBox settingsBox = this.buildSettingsBox(fractals, palettes, musics);

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

      boolean zoomIn = e.getDeltaY() > 0;
      if (!zoomIn && this.regionProperty.size().get() >= Configuration.MAX_REGION_SIZE) {
        // Prevent zooming out too far
        return;
      }

      switch (this.zoomModeProperty.get()) {
        case CENTER -> this.zoomOnCenter(zoomIn);
        case POINTER -> this.zoomOnPointer(e.getX(), e.getY(), zoomIn);
      }

      this.update();
    });
  }

  private void zoomOnCenter(final boolean zoomIn) {
    if (zoomIn) {
      this.zoomIn();
    } else {
      this.zoomOut();
    }
  }

  private void zoomOnPointer(final double mouseX, final double mouseY, final boolean zoomIn) {
    // Step 1: fractal coordinates under mouse before zoom
    double fxBefore = this.xPixelsToValue(mouseX);
    double fyBefore = this.yPixelsToValue(mouseY);

    // Step 2: zoom
    if (zoomIn) {
      this.zoomIn();
    } else {
      this.zoomOut();
    }

    // Step 3: fractal coordinates under mouse after zoom
    double fxAfter = this.xPixelsToValue(mouseX);
    double fyAfter = this.yPixelsToValue(mouseY);

    // Step 4: adjust center so the point stays under the mouse
    this.regionProperty.xc().set(this.regionProperty.xc().get() + (fxBefore - fxAfter));
    this.regionProperty.yc().set(this.regionProperty.yc().get() + (fyBefore - fyAfter));
  }

  private void reset() {
    this.regionProperty.update(this.fractal.get().getDefaultRegion());
    this.update();
  }

  public Pane getRoot() {
    return this.root;
  }

  private void move(
    final double x,
    final double y) {

    this.regionProperty.xc().set(this.xPixelsToValue(x));
    this.regionProperty.yc().set(this.yPixelsToValue(y));

    this.update();
  }

  private void zoomIn() {
    this.regionProperty.size().set(
      this.regionProperty.size().get() / this.zoomFactorProperty.get()
    );
  }

  private void zoomOut() {
    this.regionProperty.size().set(
      this.regionProperty.size().get() * this.zoomFactorProperty.get()
    );
  }

  // Convert a pixel abscissa position to a "real" mathematical value
  private double xPixelsToValue(final double x) {
    double xc   = this.regionProperty.xc().get();
    double size = this.regionProperty.size().get();
    return xc - size * 0.5 + size * x / this.width;
  }

  // Concert a pixel ordinate position to "real" mathematical value
  private double yPixelsToValue(final double y) {
    double yc   = this.regionProperty.yc().get();
    double size = this.regionProperty.size().get();
    return yc - size * 0.5 + size * y / this.height;
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

  private VBox buildSettingsBox(
    final Fractal[] fractals,
    final ColorPalette[] palettes,
    final NamedMusic[] musics) {

    // Algorithm
    ComboBox<Fractal> fractalComboBox = new ComboBox<>();
    fractalComboBox.setConverter(new NamedConverter<>());
    fractalComboBox.getItems().setAll(fractals);
    fractalComboBox.valueProperty().bindBidirectional(this.fractal);
    fractalComboBox.valueProperty().addListener((_, _, _) -> this.reset());

    // Max iterations
    Slider maxIterationsSlider = newSlider(0, 1_000, 100, this.max);
    this.max.addListener((_, _, newValue) -> {
      this.colors = this.colorPalette.get().computeColors(newValue.intValue());
      this.computeColors();
      this.update();
    });
    TitledPane algorithmPane = buildTitledPane(
      "∑ Algorithm",
      fractalComboBox,
      new Label("Max iterations"),
      maxIterationsSlider
    );

    // Zoom
    ComboBox<ZoomMode> zoomModeComboBox = new ComboBox<>();
    zoomModeComboBox.setConverter(new NamedConverter<>());
    zoomModeComboBox.getItems().setAll(ZoomMode.values());
    zoomModeComboBox.valueProperty().bindBidirectional(this.zoomModeProperty);

    // Reset
    Button resetPositionButton = new Button("Reset position");
    resetPositionButton.setOnAction(_ -> this.reset());

    TextField regionXcTextField = new TextField();
    TextField regionYcTextField = new TextField();
    TextField regionSizeTextField = new TextField();
    Label xLabel = new Label("x");
    Label yLabel = new Label("y");
    Label sizeLabel = new Label("size");
    xLabel.setPrefWidth(25);
    yLabel.setPrefWidth(25);
    sizeLabel.setPrefWidth(25);
    HBox xcBox = new HBox(5, xLabel, regionXcTextField);
    HBox ycBox = new HBox(5, yLabel, regionYcTextField);
    HBox sizeBox = new HBox(5, sizeLabel, regionSizeTextField);
    Bindings.bindBidirectional(
      regionXcTextField.textProperty(),
      this.regionProperty.xc(),
      new NumberStringConverter()
    );
    Bindings.bindBidirectional(
      regionYcTextField.textProperty(),
      this.regionProperty.yc(),
      new NumberStringConverter()
    );
    Bindings.bindBidirectional(
      regionSizeTextField.textProperty(),
      this.regionProperty.size(),
      new NumberStringConverter()
    );
    ChangeListener<? super Number> updateListener = (_, _, _) -> this.update();
    this.regionProperty.xc().addListener(updateListener);
    this.regionProperty.yc().addListener(updateListener);
    this.regionProperty.size().addListener(updateListener);

    TitledPane navigationPane = buildTitledPane(
      "Navigation",
      new Label("\uD83D\uDD0E Zoom mode"),
      zoomModeComboBox,
      new Label("Zoom factor"),
      newSlider(1, 4, 1, this.zoomFactorProperty),
      new Label("Position"),
      xcBox,
      ycBox,
      sizeBox,
      resetPositionButton
    );

    // Color palette
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

    // Color offset
    Slider colorOffsetSlider = newSlider(0, this.max.get(), 64, this.colorOffsetProperty);
    this.colorOffsetProperty.addListener((_, _, _) -> {
      this.computeColors();
      this.drawImage();
    });
    TitledPane colorPane = buildTitledPane(
      "Color",
      new Label("Palette"),
      colorPaletteComboBox,
      new Label("Offset"),
      colorOffsetSlider
    );

    // Select music
    ComboBox<NamedMusic> musicSelectionComboBox = new ComboBox<>();
    musicSelectionComboBox.getItems().setAll(musics);
    musicSelectionComboBox.valueProperty().bindBidirectional(this.musicProperty);
    musicSelectionComboBox.setConverter(new NamedConverter<>());
    musicSelectionComboBox.setOnAction(_ -> {
      // Stop current track
      this.mediaPlayer.stop();
      this.createMediaPlayer();
    });

    Label musicVolumeLabel = new Label("\uD83D\uDD08 Volume");
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
    TitledPane musicPane = buildTitledPane(
      "\uD83C\uDFB5 Ambient music",
      musicSelectionComboBox,
      musicVolumeLabel,
      musicVolumeSlider,
      playPauseButton
    );

    VBox settingsBox = new VBox(
      5,
      algorithmPane,
      navigationPane,
      colorPane,
      musicPane
    );
    settingsBox.setPadding(new Insets(5));
    settingsBox.setPrefWidth(Configuration.SETTINGS_WIDTH);

    return settingsBox;
  }

  private static TitledPane buildTitledPane(final String title, final Node... content) {
    VBox vBox = new VBox(5, content);
    TitledPane titledPane = new TitledPane(title, vBox);
    titledPane.setCollapsible(false);
    titledPane.setExpanded(true);

    return titledPane;
  }
}