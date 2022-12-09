package com.codesimcoe.mandelbrotfx;

import java.util.stream.IntStream;

import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Mandelbrot {

    private static final double ZOOM_FACTOR = 2;

    private final Pane root;

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

    // Image
    private final PixelWriter pixelWriter;
    private final int[] imagePixels;

    // Cached images (int argb values)
    private final int[] colors;

    public Mandelbrot(
        final int width,
        final int height,
        final int max) {

        this.width = width;
        this.height = height;
        this.max = max;

        // Image
        this.imagePixels = new int[width * height];
        WritableImage image = new WritableImage(width, height);
        this.pixelWriter = image.getPixelWriter();

        ImageView imageView = new ImageView(image);
        this.root = new Pane(imageView);

        // Initialize and cache all availableColors
        this.colors = new int[max + 1];
        for (int i = 0; i <= max; i++) {
            double hue = 360 * i / max;
            double brightness = (i == max) ? 0 : 1;
            Color color = Color.hsb(hue, 1, brightness);

            int a = 255;
            int r = (int) (color.getRed() * 255);
            int g = (int) (color.getGreen() * 255);
            int b = (int) (color.getBlue() * 255);

            int rgb = (a << 24) | (r << 16) | (g << 8) | b;

            this.colors[i] = rgb;
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
                this.zoomOut();
            } else {
                this.zoomIn();
            }

            this.update();
        });
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
        this.regionSize *= ZOOM_FACTOR;
    }

    private void zoomOut() {
        this.regionSize /= ZOOM_FACTOR;
    }
    
    private double xPixelsToValue(final double x) {
        return this.xc - this.regionSize / 2 + this.regionSize * x / this.width;
    }
    
    private double yPixelsToValue(final double y) {
        return this.yc - this.regionSize / 2 + this.regionSize * y / this.height;
    }

    /**
     * Set the region we are looking at, defined by its center (xc, yc) and its size
     * @param xc
     * @param yc
     * @param regionSize
     */
    public void setRegion(
        final double xc,
        final double yc,
        final double regionSize) {

        this.regionSize = regionSize;
        this.xc = xc;
        this.yc = yc;
    }

    private int compute(double re, double im) {

        double re0 = re;
        double im0 = im;

        for (int i = 0; i < this.max; i++) {

            double squaredNorm = re * re + im * im;
            if (squaredNorm > 4) {
                // Divergence
                return i;
            }

            // Complex number product formula
            // (x + yi)(x + yi) = x2 + y2 + 2xyi
            double newRe = re * re - im * im + re0;
            im = 2 * re * im + im0;
            re = newRe;
        }

        return this.max;
    }

    public void update() {

        long start = System.currentTimeMillis();

        // Parallelize computations
        IntStream.range(0, this.width).parallel().forEach(x -> {
            double x0 = this.xPixelsToValue(x);
            
            IntStream.range(0, this.height).forEach(y -> {
                double y0 = this.yPixelsToValue(y);

                int mand = this.compute(x0, y0);
                int color = this.colors[mand];
                this.imagePixels[y * this.height + x] = color;
            });
        });

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

        long elapsed = System.currentTimeMillis() - start;
        System.out.println(elapsed + "ms");
    }
}