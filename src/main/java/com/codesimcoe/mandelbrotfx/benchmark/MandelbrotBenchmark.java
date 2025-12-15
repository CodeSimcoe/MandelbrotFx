package com.codesimcoe.mandelbrotfx.benchmark;

import com.codesimcoe.mandelbrotfx.MandelbrotFFMAVX;
import com.codesimcoe.mandelbrotfx.MandelbrotFFMCuda;
import com.codesimcoe.mandelbrotfx.MandelbrotVector;
import com.codesimcoe.mandelbrotfx.Viewport;
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

// Benchmark                                  Mode  Cnt    Score   Error  Units
// MandelbrotBenchmark.regularBenchmark       avgt   25  214,994 ± 1,989  ms/op
// MandelbrotBenchmark.doubleVectorBenchmark  avgt   25   99,247 ± 0,914  ms/op
// MandelbrotBenchmark.floatVectorBenchmark   avgt   25   52,212 ± 0,852  ms/op
// MandelbrotBenchmark.avxFFMBenchmark        avgt   25   29,983 ± 0,123  ms/op
// MandelbrotBenchmark.cudaFFMBenchmark       avgt   25    3,958 ± 0,039  ms/op
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class MandelbrotBenchmark {

  private Viewport viewport;
  private int max;
  private int width;
  private int height;
  private int[][] iterationsPixels;

  @Setup(Level.Iteration)
  public void setup() {

    double re = -0.7397491455078126;
    double im = 0.12508239746093755;
    double size = 0.0078125;

    this.width = 1024;
    this.height = 1024;
    this.viewport = new Viewport(re, im, size, this.width, this.height);
    this.max = 2000;

    this.iterationsPixels = new int[this.height][this.width];
  }

  @Benchmark
  public void regularBenchmark() {
    IntStream.range(0, this.height)
      .parallel()
      .forEach(y -> {
        double y0 = this.viewport.screenToIm(y, this.height);
        for (int x = 0; x < this.width; x++) {
          double x0 = this.viewport.screenToRe(x, this.width);
          int iterations = MandelbrotFractal.MANDELBROT.computeEscape(x0, y0, this.max);
          this.iterationsPixels[y][x] = iterations;
        }
      });
  }

  @Benchmark
  public void doubleVectorBenchmark() {
    IntStream.range(0, this.height)
      .parallel()
      .forEach(y -> {

        double im0 = this.viewport.screenToIm(y, this.height);
        MandelbrotVector.computeLineDouble(this.width, this.viewport, im0, this.max);
      });
  }

  @Benchmark
  public void floatVectorBenchmark() {
    IntStream.range(0, this.height)
      .parallel()
      .forEach(y -> {
        float im0 = (float) this.viewport.screenToIm(y, this.height);
        MandelbrotVector.computeLineFloat(this.width, this.viewport, im0, this.max);
      });
  }

  @Benchmark
  public void avxFFMBenchmark() {

    IntStream.range(0, this.height)
      .parallel()
      .forEach(y -> {

        double size = this.viewport.getSize();

        float xRes = (float) (size / this.width);
        float yRes = (float) (size / this.height);

        float xl = (float) (this.viewport.getCenterRe() - (this.width * 0.5) * xRes);
        float yl = (float) ((this.height * 0.5) * yRes - this.viewport.getCenterIm());

        MandelbrotFFMAVX.computeLine(
          y,
          this.width,
          this.max,
          xl,
          yl,
          xRes,
          yRes,
          this.iterationsPixels[y]
        );
      });
  }

  @Benchmark
  public void cudaFFMBenchmark() {
    float cx0 = (float) this.viewport.getCenterRe();
    float cy0 = (float) this.viewport.getCenterIm();
    float scale = (float) (this.viewport.getSize() / this.width);

    MandelbrotFFMCuda.computeFloat(
      cx0,
      cy0,
      scale,
      this.width,
      this.height,
      this.max,
      this.iterationsPixels
    );
  }
}
