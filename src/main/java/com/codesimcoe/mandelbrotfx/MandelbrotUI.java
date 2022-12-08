package com.codesimcoe.mandelbrotfx;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class MandelbrotUI {

    private final Pane root;

    private final double width;
    private final double height;
    private final int max;

    private final PixelWriter pixelWriter;
    
    private final Color[] colors;

    public MandelbrotUI(final int width, final int height, final int max) {

        this.width = width;
        this.height = height;
        this.max = max;

        Canvas canvas = new Canvas(width, height);
        this.root = new Pane(canvas);

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        this.pixelWriter = graphicsContext.getPixelWriter();
        
        // Initialize and cache all availableColors
        this.colors = new Color[max + 1];
        for (int i = 0; i <= max; i++) {
            double hue = 360 * i / max;
            double brightness = (i == max) ? 0 : 1;
            Color color = Color.hsb(hue, 1, brightness);
            this.colors[i] = color;
        }
    }

    public Pane getRoot() {
        return this.root;
    }
    
    public int compute(double re, double im) {

        double re0 = re;
        double im0 = im;
        
        for (int i = 0; i < this.max; i++) {

            double squaredNorm = re * re + im * im;
            if (squaredNorm > 4) {
                // Divergence
                return i;
            }

            // Complex number product formula
            // (x + yi)(x + yi) = x² − y² + 2xyi
            double newRe = re * re - im * im + re0;
            im = 2 * re * im + im0;
            re = newRe;
        }

        return this.max;
    }

    public void update() {

        double size = 2;
        double xc = -0.5;
        double yc = 0;

        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                
                double x0 = xc - size / 2 + size * x / this.width;
                double y0 = yc - size / 2 + size * y / this.height;
                
                int mand = this.compute(x0, y0);
                
                Color color = this.colors[mand];
                
                this.pixelWriter.setColor(x, y, color);
            }
        }
    }
}