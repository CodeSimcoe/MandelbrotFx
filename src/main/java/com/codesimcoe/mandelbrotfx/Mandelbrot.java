package com.codesimcoe.mandelbrotfx;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import atlantafx.base.theme.Styles;
import com.codesimcoe.mandelbrotfx.MandelbrotStrategy.MandelbrotStrategyType;
import com.codesimcoe.mandelbrotfx.component.PaletteCellFactory;
import com.codesimcoe.mandelbrotfx.escape.EscapeViewer;
import com.codesimcoe.mandelbrotfx.fractal.BuffaloFractal;
import com.codesimcoe.mandelbrotfx.fractal.BurningShipFractal;
import com.codesimcoe.mandelbrotfx.fractal.CelticFractal;
import com.codesimcoe.mandelbrotfx.fractal.Fractal;
import com.codesimcoe.mandelbrotfx.fractal.JuliaFractal;
import com.codesimcoe.mandelbrotfx.fractal.MandelbrotFractal;
import com.codesimcoe.mandelbrotfx.fractal.NewtonSinFractal;
import com.codesimcoe.mandelbrotfx.fractal.PhoenixFractal;
import com.codesimcoe.mandelbrotfx.fractal.TricornFractal;
import com.codesimcoe.mandelbrotfx.fractal.extended.DoubleVectorMandelbrotFractal;
import com.codesimcoe.mandelbrotfx.fractal.extended.FFMAVXFloatMandelbrotFractal;
import com.codesimcoe.mandelbrotfx.fractal.extended.FFMCudaDoubleMandelbrotFractal;
import com.codesimcoe.mandelbrotfx.fractal.extended.FFMCudaFloatMandelbrotFractal;
import com.codesimcoe.mandelbrotfx.fractal.extended.FloatVectorMandelbrotFractal;
import com.codesimcoe.mandelbrotfx.music.Music;
import com.codesimcoe.mandelbrotfx.palette.ColorPalette;
import com.codesimcoe.mandelbrotfx.palette.GradientColorPalettes;
import com.codesimcoe.mandelbrotfx.palette.GrayscaleColorPalette;
import com.codesimcoe.mandelbrotfx.palette.SpectrumColorPalette;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

public class Mandelbrot {

  // Common layout gap between elements
  private static final double GAP = 5;

  private final BorderPane root;
  private final Label logLabel = new Label();

  // Used fractal algorithm
  private final ObjectProperty<Fractal> fractal = new SimpleObjectProperty<>();

  // Used color palette
  private final ObjectProperty<ColorPalette> colorPalette = new SimpleObjectProperty<>();

  // Viewport
  private final Viewport viewport;

  private TextField regionReCenterTextField;
  private TextField regionImCenterTextField;
  private TextField regionSizeTextField;

  // Zoom mode
  private final ObjectProperty<ZoomMode> zoomModeProperty = new SimpleObjectProperty<>(Configuration.DEFAULT_ZOOM_MODE);

  // Zoom factor
  private final DoubleProperty zoomFactorProperty = new SimpleDoubleProperty(Configuration.DEFAULT_ZOOM_FACTOR);

  // Max algorithm iterations
  private final IntegerProperty maxIterations = new SimpleIntegerProperty();

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

  // Media player (music)
  private MediaPlayer mediaPlayer;
  private Button playPauseButton;
  private boolean musicPlaying = false;

  // Snapshot
  private final List<Button> snapshotButtons = new ArrayList<>();
  private final ProgressBar snapshotProgressBar = new ProgressBar(ProgressIndicator.INDETERMINATE_PROGRESS);

  // Points of interest
  private final ComboBox<RegionOfInterest> regionsOfInterestComboBox = new ComboBox<>();

  // Escape viewer
  private final EscapeViewer escapeViewer;

  // Theme
  private Theme currentTheme = Theme.LIGHT;

  public Mandelbrot(
    int width,
    int height,
    int maxIterations) {

    // Fractals
    Fractal[] fractals = {
      MandelbrotFractal.MANDELBROT,
      new DoubleVectorMandelbrotFractal(),
      new FloatVectorMandelbrotFractal(),
      new FFMAVXFloatMandelbrotFractal(),
      new FFMCudaDoubleMandelbrotFractal(),
      new FFMCudaFloatMandelbrotFractal(),
      BurningShipFractal.BURNING_SHIP,
      BuffaloFractal.BUFFALO,
      TricornFractal.TRICORN,
      CelticFractal.CELTIC,
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
      new PhoenixFractal("Phoenix 1", 1),
      NewtonSinFractal.INSTANCE
    };

    Fractal selectedFractal = fractals[0];
    this.fractal.set(selectedFractal);
    Region region = selectedFractal.getDefaultRegion();
    this.viewport = new Viewport(region.centerRe(), region.centerIm(), region.size(), width, height);
    this.fillRegionsOfInterest();

    // Color palettes
    ColorPalette[] palettes = {
      GradientColorPalettes.BLUE_ORANGE,
      new SpectrumColorPalette(),
      new GrayscaleColorPalette(),
      GradientColorPalettes.OCEAN,
      GradientColorPalettes.SUNSET,
      GradientColorPalettes.ELEGANT,
      GradientColorPalettes.NEON,
      GradientColorPalettes.NATURE,
      GradientColorPalettes.COFFEE,
      GradientColorPalettes.TROPICAL,
      GradientColorPalettes.MOON,
      GradientColorPalettes.PURPLE_GREEN,
      GradientColorPalettes.BLUE_RED,
      GradientColorPalettes.RED_GREEN,
      GradientColorPalettes.CYAN_MAGENTA,
    };
    this.colorPalette.set(palettes[0]);

    this.width = width;
    this.height = height;
    this.maxIterations.set(maxIterations);

    this.iterationsPixels = new int[this.height][this.width];

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

    Pane mainPane = new Pane(canvas);
    BorderPane.setAlignment(canvas, Pos.TOP_CENTER);

    // Canvas graphics context
    this.pixelWriter = canvas.getGraphicsContext2D().getPixelWriter();

    // Initialize and cache all available colors
    this.colors = this.colorPalette.get().computeColors(maxIterations);

    // Initialize escape viewer
    this.escapeViewer = new EscapeViewer(mainPane, this.viewport, this.fractal.get());

    // Settings
    VBox settingsBox = this.buildSettingsBox(fractals, palettes, musics);
    ScrollPane settingsScrollPane = new ScrollPane(settingsBox);
    settingsScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);

    // Log label : setup a background for better readability
    this.logLabel.setPadding(new Insets(10));
    this.logLabel.setBackground(new Background(new BackgroundFill(
      Color.rgb(0, 0, 0, 0.7),
      new CornerRadii(5),
      new Insets(5)
    )));

    // Assemble (border pane)
    this.root = new BorderPane();
    this.root.setCenter(mainPane);
    this.root.setRight(settingsScrollPane);
    this.root.setBottom(this.logLabel);

    // Trigger UI update
    this.manageViewportChange();

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
      if (!zoomIn && this.viewport.getSize() >= Configuration.MAX_REGION_SIZE) {
        // Prevent zooming out too far
        return;
      }

      switch (this.zoomModeProperty.get()) {
        case CENTER -> this.zoomOnCenter(zoomIn);
        case POINTER -> this.zoomOnPointer(e.getX(), e.getY(), zoomIn);
      }

      this.manageViewportChange();
      this.update();
    });

    // Zoom (on center) upon key press
    this.root.setOnKeyPressed(e -> {
      switch (e.getCode()) {
        case PLUS, ADD -> {
          this.zoomIn();
          this.update();
        }
        case MINUS, SUBTRACT -> {
          this.zoomOut();
          this.update();
        }
      }
    });
  }

  private void zoomOnCenter(boolean zoomIn) {
    if (zoomIn) {
      this.zoomIn();
    } else {
      this.zoomOut();
    }
  }

  private void zoomOnPointer(double mouseX, double mouseY, boolean zoomIn) {
    // Step 1: fractal coordinates under mouse before zoom
    double fxBefore = this.viewport.screenToRe(mouseX);
    double fyBefore = this.viewport.screenToIm(mouseY);

    // Step 2: zoom
    if (zoomIn) {
      this.zoomIn();
    } else {
      this.zoomOut();
    }

    // Step 3: fractal coordinates under mouse after zoom
    double fxAfter = this.viewport.screenToRe(mouseX);
    double fyAfter = this.viewport.screenToIm(mouseY);

    // Step 4: adjust center so the point stays under the mouse
    this.viewport.moveBy(
      fxBefore - fxAfter,
      fyBefore - fyAfter
    );
  }

  private void reset() {
    this.viewport.update(this.fractal.get().getDefaultRegion());
    this.manageViewportChange();
    this.update();
  }

  public Pane getRoot() {
    return this.root;
  }

  private void move(double x, double y) {

    this.viewport.screenMoveTo(x, y);
    this.manageViewportChange();

    this.update();
  }

  private void zoomIn() {
    this.viewport.zoomInBy(this.zoomFactorProperty.get());

  }

  private void zoomOut() {
    this.viewport.zoomOutBy(this.zoomFactorProperty.get());
  }

  /// Compute iteration pixels
  /// @return the computation time, expressed in milliseconds
  private long computeIterationPixels(
    Fractal algorithm,
    int max,
    int width,
    int height,
    int[][] iterationsPixels) {

    long startTime = System.nanoTime();

    switch (algorithm) {
      case DoubleVectorMandelbrotFractal _ -> this.computeIterationPixelsVectorizedDouble(max, width, height, iterationsPixels);
      case FloatVectorMandelbrotFractal _ -> this.computeIterationPixelsVectorizedFloat(max, width, height, iterationsPixels);
      case FFMAVXFloatMandelbrotFractal _ -> this.computeIterationPixelsFFMAVX(max, width, height, iterationsPixels);
      case FFMCudaFloatMandelbrotFractal _ -> this.computeIterationPixelsCudaFloat(max, width, height, iterationsPixels);
      case FFMCudaDoubleMandelbrotFractal _ -> this.computeIterationPixelsCudaDouble(max, width, height, iterationsPixels);
      default -> this.computeIterationPixelsRegular(algorithm, max, width, height, iterationsPixels);
    }

    long elapsed = (System.nanoTime() - startTime) / 1_000_000;
    return elapsed;
  }

  private void computeIterationPixelsRegular(
    Fractal algorithm,
    int max,
    int width,
    int height,
    int[][] iterationsPixels) {

    // Parallelize computations
    // Cache locality : order matters
    IntStream.range(0, height)
      .parallel()
      .forEach(y -> {
        double y0 = this.viewport.screenToIm(y, height);
        for (int x = 0; x < width; x++) {
          double x0 = this.viewport.screenToRe(x, width);
          int iterations = algorithm.computeEscape(x0, y0, max);
          iterationsPixels[y][x] = iterations;
        }
      });
  }

  private void computeIterationPixelsFFMAVX(
    int max,
    int width,
    int height,
    int[][] iterationsPixels) {

    float xRes = (float) (this.viewport.getSize() / width);
    float yRes = (float) (this.viewport.getSize() / height);

    float xl = (float) (this.viewport.getCenterRe() - (width * 0.5) * xRes);
    float yl = (float) ((height * 0.5) * yRes - this.viewport.getCenterIm());

    IntStream.range(0, height)
      .parallel()
      .forEach(y ->
        MandelbrotFFMAVX.computeLine(
          y,
          width,
          max,
          xl,
          yl,
          xRes,
          yRes,
          iterationsPixels[y]
        )
      );
  }

  private void computeIterationPixelsCudaFloat(
    int max,
    int width,
    int height,
    int[][] iterationsPixels) {

    float cx0 = (float) this.viewport.getCenterRe();
    float cy0 = (float) this.viewport.getCenterIm();
    float scale = (float) (this.viewport.getSize() / width);

    MandelbrotFFMCuda.computeFloat(
      cx0,
      cy0,
      scale,
      width,
      height,
      max,
      iterationsPixels
    );
  }

  private void computeIterationPixelsCudaDouble(
    int max,
    int width,
    int height,
    int[][] iterationsPixels) {

    double cx0 = this.viewport.getCenterRe();
    double cy0 = this.viewport.getCenterIm();
    double scale = this.viewport.getSize() / width;

    MandelbrotFFMCuda.computeDouble(
      cx0,
      cy0,
      scale,
      width,
      height,
      max,
      iterationsPixels
    );
  }

  private void computeIterationPixelsVectorizedDouble(
    int max,
    int width,
    int height,
    int[][] iterationsPixels) {

    IntStream.range(0, height)
      .parallel()
      .forEach(py -> {

        double y0 = this.viewport.screenToIm(py, height);
        iterationsPixels[py] = MandelbrotVector.computeLineDouble(width, this.viewport, y0, max);
      });
  }

  private void computeIterationPixelsVectorizedFloat(
    int max,
    int width,
    int height,
    int[][] iterationsPixels) {

    IntStream.range(0, height)
      .parallel()
      .forEach(py -> {

        float y0 = (float) this.viewport.screenToIm(py, height);
        iterationsPixels[py] = MandelbrotVector.computeLineFloat(width, this.viewport, y0, max);
      });
  }

  void update() {

    long elapsed = this.computeIterationPixels(
      this.fractal.get(),
      this.maxIterations.get(),
      this.width,
      this.height,
      this.iterationsPixels
    );

    this.writeLog("Rendered in " + elapsed + "ms", LogLevel.NORMAL);

    this.computeColors();
    this.drawImage();
  }

  private void computeColors() {
    this.computeColors(
      this.maxIterations.get(),
      this.width,
      this.height,
      this.iterationsPixels,
      this.colors,
      this.imagePixels
    );
  }

  private void computeColors(
    int max,
    int width,
    int height,
    int[][] iterationsPixels,
    int[] colors,
    int[] imagePixels) {

    if (max == 0) {
      return;
    }

    // Determine color and apply offset
    int color;
    int colorOffset = this.colorOffsetProperty.intValue();
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int iterations = iterationsPixels[y][x];
        if (iterations == max) {
          color = colors[max];
        } else {
          int colorIndex = (iterations + colorOffset) % max;
          color = colors[colorIndex];
        }

        imagePixels[y * height + x] = color;
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
    double min,
    double max,
    double majorTickUnit,
    Property<Number> property) {

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
    Fractal[] fractals,
    ColorPalette[] palettes,
    NamedMusic[] musics) {

    // Theme
    ToggleGroup themeToggleGroup = new ToggleGroup();

    ToggleButton lightThemeButton = new ToggleButton("Light");
    ToggleButton darkThemeButton  = new ToggleButton("Dark");

    lightThemeButton.setToggleGroup(themeToggleGroup);
    darkThemeButton.setToggleGroup(themeToggleGroup);

    themeToggleGroup.selectedToggleProperty().addListener((_, _, newToggle) -> {
      this.currentTheme = (newToggle == lightThemeButton) ? Theme.LIGHT : Theme.DARK;
      this.updateTheme();
    });

    ToggleButton selectedThemeButton = switch (this.currentTheme) {
      case LIGHT -> lightThemeButton;
      case DARK -> darkThemeButton;
    };
    themeToggleGroup.selectToggle(selectedThemeButton);

    // Apply segmented style
    lightThemeButton.getStyleClass().addAll(Styles.LEFT_PILL);
    darkThemeButton.getStyleClass().addAll(Styles.RIGHT_PILL);
    HBox themeBox = new HBox(lightThemeButton, darkThemeButton);
    TitledPane themePane = buildTitledPane("Theme", themeBox);

    // Algorithm
    ComboBox<Fractal> fractalComboBox = new ComboBox<>();
    fractalComboBox.setConverter(new NamedConverter<>());
    fractalComboBox.getItems().setAll(fractals);
    fractalComboBox.valueProperty().bindBidirectional(this.fractal);
    fractalComboBox.valueProperty().addListener((_, _, _) -> this.manageAlgorithmChange());

    // Max iterations
    Slider maxIterationsSlider = newSlider(0, Configuration.TOTAL_MAX_ITERATIONS, 100, this.maxIterations);

    // Bind slider's value to maxIterations (but only commit when sliding ends)
    maxIterationsSlider.valueChangingProperty().addListener((_, _, changing) -> {
      if (!changing) {
        int newValue = maxIterationsSlider.valueProperty().intValue();
        this.updateMaxIterations(newValue);
      }
    });

    this.regionsOfInterestComboBox.setConverter(new NamedConverter<>());
    Button jumpToRegionOfInterestButton = new Button("_Jump");
    jumpToRegionOfInterestButton.setMnemonicParsing(true);
    jumpToRegionOfInterestButton.setOnAction(_ -> this.jumpToRegionOfInterest(this.regionsOfInterestComboBox.getValue()));
    HBox regionOfInterestBox = new HBox(GAP, this.regionsOfInterestComboBox, jumpToRegionOfInterestButton);

    Label strategyLabel = new Label("Strategy");
    ComboBox<MandelbrotStrategyType> strategyTypeComboBox = new ComboBox<>();
    strategyTypeComboBox.setValue(MandelbrotStrategyType.PRIMITIVE);
    strategyTypeComboBox.setConverter(new NamedConverter<>());
    strategyTypeComboBox.getItems().setAll(MandelbrotStrategyType.values());
    strategyTypeComboBox.setOnAction(_ -> {
      MandelbrotStrategy strategy = strategyTypeComboBox.getValue().getStrategy();
      MandelbrotFractal.MANDELBROT.setStrategy(strategy);
    });

    TitledPane algorithmPane = buildTitledPane(
      "∑ Algorithm",
      fractalComboBox,
      new Label("Max iterations"),
      maxIterationsSlider,
      strategyLabel,
      strategyTypeComboBox,
      new Label("Regions of interest"),
      regionOfInterestBox
    );

    // Zoom
    ComboBox<ZoomMode> zoomModeComboBox = new ComboBox<>();
    zoomModeComboBox.setConverter(new NamedConverter<>());
    zoomModeComboBox.getItems().setAll(ZoomMode.values());
    zoomModeComboBox.valueProperty().bindBidirectional(this.zoomModeProperty);

    // Current position
    this.regionReCenterTextField = new TextField();
    this.regionImCenterTextField = new TextField();
    this.regionSizeTextField = new TextField();
    Label reLabel = new Label("re");
    Label imLabel = new Label("im");
    Label sizeLabel = new Label("size");
    reLabel.setPrefWidth(25);
    imLabel.setPrefWidth(25);
    sizeLabel.setPrefWidth(25);
    HBox xcBox = new HBox(GAP, reLabel, this.regionReCenterTextField);
    HBox ycBox = new HBox(GAP, imLabel, this.regionImCenterTextField);
    HBox sizeBox = new HBox(GAP, sizeLabel, this.regionSizeTextField);
    xcBox.setAlignment(Pos.CENTER_LEFT);
    ycBox.setAlignment(Pos.CENTER_LEFT);
    sizeBox.setAlignment(Pos.CENTER_LEFT);

    this.regionReCenterTextField.setOnAction(_ -> {
      if (this.regionReCenterTextField.isFocused()) {
        try {
          double re = Double.parseDouble(this.regionReCenterTextField.getText());
          this.viewport.complexMoveTo(re, this.viewport.getCenterIm());
          this.update();
        } catch (NumberFormatException e) {
          // Silently ignore invalid input
        }
      }
    });
    this.regionImCenterTextField.setOnAction(_ -> {
      if (this.regionImCenterTextField.isFocused()) {
        try {
          double im = Double.parseDouble(this.regionImCenterTextField.getText());
          this.viewport.complexMoveTo(this.viewport.getCenterRe(), im);
          this.update();
        } catch (NumberFormatException e) {
          // Silently ignore invalid input
        }
      }
    });
    this.regionSizeTextField.setOnAction(_ -> {
      if (this.regionSizeTextField.isFocused()) {
        try {
          double size = Double.parseDouble(this.regionSizeTextField.getText());
          this.viewport.setSize(size);
          this.update();
        } catch (NumberFormatException e) {
          // Silently ignore invalid input
        }
      }
    });

    TitledPane navigationPane = buildTitledPane(
      "Navigation",
      new Label("\uD83D\uDD0E Zoom mode"),
      zoomModeComboBox,
      new Label("Zoom factor"),
      newSlider(1, 4, 1, this.zoomFactorProperty),
      new Label("Position"),
      xcBox,
      ycBox,
      sizeBox
    );

    // Color palette
    ComboBox<ColorPalette> colorPaletteComboBox = new ComboBox<>();
    colorPaletteComboBox.setConverter(new NamedConverter<>());
    PaletteCellFactory.apply(colorPaletteComboBox);
    colorPaletteComboBox.getItems().setAll(palettes);
    colorPaletteComboBox.valueProperty().bindBidirectional(this.colorPalette);
    colorPaletteComboBox.valueProperty().addListener((_, _, newValue) -> {
      this.colors = newValue.computeColors(this.maxIterations.get());
      this.computeColors();
      this.drawImage();
    });

    // Color offset
    Slider colorOffsetSlider = newSlider(0, this.maxIterations.get(), 64, this.colorOffsetProperty);
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

    // Snapshot
    this.snapshotProgressBar.setMaxWidth(Double.MAX_VALUE);
    for (SnapshotMode mode : SnapshotMode.values()) {
      Button snapshotButton = new Button(mode.getName());
      snapshotButton.setOnAction(_ -> {
        this.snapshotButtons.forEach(b -> b.setDisable(true));
        this.snapshotProgressBar.setVisible(true);
        CompletableFuture.runAsync(() -> this.takeSnapshot(mode));
      });
      this.snapshotButtons.add(snapshotButton);
    }
    HBox snapshotButtonsBox = new HBox(
      GAP,
      this.snapshotButtons.toArray(new Button[] {})
    );


    this.snapshotProgressBar.setVisible(false);
    TitledPane snapshotPane = buildTitledPane("Snapshot", snapshotButtonsBox, this.snapshotProgressBar);

    // Escape viewer
    Slider escapeViewerSlider = newSlider(0, Configuration.ESCAPE_MAX_POINTS, 10, this.escapeViewer.getEscapeMaxPointsProperty());
    ToggleButton escapeViewerToggleButton = new ToggleButton("_Overlay");
    escapeViewerToggleButton.setMnemonicParsing(true);
    escapeViewerToggleButton.setOnAction(_ -> this.escapeViewer.update(escapeViewerToggleButton.isSelected()));
    TitledPane escapeViewerPane = buildTitledPane(
      "Escape viewer",
      escapeViewerSlider,
      escapeViewerToggleButton
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

    this.playPauseButton = new Button("▶ _Play");
    this.playPauseButton.setMnemonicParsing(true);
    this.playPauseButton.setOnAction(_ -> this.toggleMusicPlay());
    TitledPane musicPane = buildTitledPane(
      "\uD83C\uDFB5 Ambient music",
      musicSelectionComboBox,
      musicVolumeLabel,
      musicVolumeSlider,
      this.playPauseButton
    );

    VBox settingsBox = new VBox(
      GAP,
      themePane,
      algorithmPane,
      navigationPane,
      colorPane,
      snapshotPane,
      escapeViewerPane,
      musicPane
    );
    settingsBox.setPadding(new Insets(GAP));
    settingsBox.setPrefWidth(Configuration.SETTINGS_WIDTH);

    return settingsBox;
  }

  private void toggleMusicPlay() {
    if (this.musicPlaying) {
      this.mediaPlayer.pause();
      this.playPauseButton.setText("▶ Play");
      this.musicPlaying = false;
    } else {
      this.mediaPlayer.play();
      this.playPauseButton.setText("⏸ Pause");
      this.musicPlaying = true;
    }
  }

  private void manageAlgorithmChange() {
    this.escapeViewer.setFractal(this.fractal.get());
    this.fillRegionsOfInterest();

    // XXX : no viewport reset so we can switch algorithms, staying on the same location
//    this.reset();
    this.update();
  }

  private void updateMaxIterations(int iterations) {
    this.maxIterations.set(iterations);
    this.colors = this.colorPalette.get().computeColors(iterations);
    this.computeColors();
    this.update();
  }

  private void fillRegionsOfInterest() {
    RegionOfInterest home = new RegionOfInterest(
      "Home",
      this.fractal.get().getDefaultRegion(),
      Configuration.DEFAULT_MAX_ITERATIONS
    );
    this.regionsOfInterestComboBox.getItems().clear();
    this.regionsOfInterestComboBox.getItems().addAll(this.fractal.get().getRegionsOfInterest());
    this.regionsOfInterestComboBox.getItems().addFirst(home);
    this.regionsOfInterestComboBox.setValue(home);
  }

  private static TitledPane buildTitledPane(String title, Node... content) {
    VBox vBox = new VBox(GAP, content);
    TitledPane titledPane = new TitledPane(title, vBox);
    titledPane.setCollapsible(false);
    titledPane.setExpanded(true);
    titledPane.setAnimated(false);
    titledPane.getStyleClass().add(Styles.DENSE);

    return titledPane;
  }

  private void takeSnapshot(SnapshotMode mode) {

    int w = mode.getResolution();
    int h = mode.getResolution();
    int[][] itPixels = new int[w][h];
    int[] pixels = new int[w * h];

    int max = this.maxIterations.get();
    int[] colors = this.colorPalette.get().computeColors(max);

    long elapsed = this.computeIterationPixels(this.fractal.get(), max, w, h, itPixels);
    this.computeColors(max, w, h, itPixels, colors, pixels);

    BufferedImage bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    bufferedImage.setRGB(0, 0, w, h, pixels, 0, w);

    long suffix = System.currentTimeMillis();
    String filename = this.fractal.get().getName() + "-" + mode.getName() + "-" + suffix + ".png";
    File outputFile = new File(filename);
    try {
      ImageIO.write(bufferedImage, "png", outputFile);
      this.writeLog("Rendered in " + elapsed + "ms - Image saved as " + outputFile.getAbsolutePath(), LogLevel.NORMAL);
    } catch (IOException e) {
      this.writeLog("Failed to save snapshot file " + outputFile.getAbsolutePath(), LogLevel.ERROR);
    } finally {
      Platform.runLater(() -> {
        this.snapshotButtons.forEach(b -> b.setDisable(false));
        this.snapshotProgressBar.setVisible(false);
      });
    }
  }

  void jumpToRegionOfInterest(RegionOfInterest regionOfInterest) {
    this.viewport.update(regionOfInterest.region());
    this.manageViewportChange();
    this.updateMaxIterations(regionOfInterest.iterations());
  }

  private void updateTheme() {
    String userAgentStylesheet = switch (this.currentTheme) {
      case LIGHT -> new PrimerLight().getUserAgentStylesheet();
      case DARK -> new PrimerDark().getUserAgentStylesheet();
    };
    Application.setUserAgentStylesheet(userAgentStylesheet);
  }

  private void manageViewportChange() {
    this.regionReCenterTextField.setText(String.valueOf(this.viewport.getCenterRe()));
    this.regionImCenterTextField.setText(String.valueOf(this.viewport.getCenterIm()));
    this.regionSizeTextField.setText(String.valueOf(this.viewport.getSize()));
  }

  private void writeLog(String text, LogLevel level) {
    Color color = switch (level) {
      case NORMAL -> Color.WHITE;
      case ERROR -> Color.DARKRED;
    };
    Platform.runLater(() -> {
      this.logLabel.setTextFill(color);
      this.logLabel.setText(text);
    });
  }
}