package com.codesimcoe.mandelbrotfx.benchmark;

import com.codesimcoe.mandelbrotfx.MandelbrotStrategy;
import com.codesimcoe.mandelbrotfx.MandelbrotStrategy.PrimitiveStrategy;
import com.codesimcoe.mandelbrotfx.MandelbrotStrategy.RecordStrategy;
import com.codesimcoe.mandelbrotfx.MandelbrotStrategy.ValueRecordStrategy;
import com.codesimcoe.mandelbrotfx.Viewport;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

//Benchmark                                  Mode  Cnt    Score   Error  Units
//PrimitiveRecordValueBenchmark.primitive    avgt   25   19,758 ± 0,119  ms/op
//PrimitiveRecordValueBenchmark.record       avgt   25  207,828 ± 1,938  ms/op
//PrimitiveRecordValueBenchmark.valueRecord  avgt   25   20,887 ± 0,079  ms/op
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class PrimitiveRecordValueBenchmark {

  private static void compute(MandelbrotStrategy strategy) {

    int max = 255;

    int width = 1024;
    int height = 1024;
    Viewport viewport = new Viewport(-0.5, 0, 2, width, height);

    IntStream.range(0, height)
      .parallel()
      .forEach(y -> {
        double y0 = viewport.screenToIm(y, height);
        for (int x = 0; x < width; x++) {
          double x0 = viewport.screenToRe(x, width);
          strategy.computeEscape(x0, y0, max);
        }
      });
  }

  @Benchmark
  public void primitive() {
    compute(PrimitiveStrategy.INSTANCE);
  }

  @Benchmark
  public void record() {
    compute(RecordStrategy.INSTANCE);
  }

  @Benchmark
  public void valueRecord() {
    compute(ValueRecordStrategy.INSTANCE);
  }
}
