package com.codesimcoe.mandelbrotfx.benchmark;

import com.codesimcoe.mandelbrotfx.fractal.Fractal;
import com.codesimcoe.mandelbrotfx.fractal.MandelbrotFractal;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class MandelbrotBenchmark {

  private final int width = 800;
  private final int height = 600;
  private final int max = 1000;
  private int[][] iterationsPixels;
  private Fractal algorithm;

  @Setup(Level.Iteration)
  public void setup() {
    this.iterationsPixels = new int[this.width][this.height];
    this.algorithm = MandelbrotFractal.MANDELBROT;
  }

  @Benchmark
  public void computeMandelbrotWidth() {
    IntStream.range(0, this.width)
      .parallel()
      .forEach(x -> {
        double x0 = this.xPixelsToValue(x);
        for (int y = 0; y < this.height; y++) {
          double y0 = this.yPixelsToValue(y);
          int iterations = this.algorithm.computeEscape(x0, y0, this.max);
          this.iterationsPixels[x][y] = iterations;
        }
      });
  }

  @Benchmark
  public void computeMandelbrotHeight() {
    IntStream.range(0, this.height)
      .parallel()
      .forEach(y -> {
        double y0 = this.yPixelsToValue(y);
        for (int x = 0; x < this.width; x++) {
          double x0 = this.xPixelsToValue(x);
          int iterations = this.algorithm.computeEscape(x0, y0, this.max);
          this.iterationsPixels[x][y] = iterations;
        }
      });
  }

  private double xPixelsToValue(final int x) {
    return (x - this.width / 2.0) * 4.0 / this.width;
  }

  private double yPixelsToValue(final int y) {
    return (y - this.height / 2.0) * 4.0 / this.width;
  }
}
