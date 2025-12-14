package com.codesimcoe.mandelbrotfx;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.nio.file.Path;

public final class MandelbrotFFMCuda {

  private static final MethodHandle MH;

  static {
    Arena arena = Arena.ofShared();
    Path path = Path.of("cuda/mandelbrot_cuda.dll");
    SymbolLookup lookup = SymbolLookup.libraryLookup(path, arena);
    MemorySegment function = lookup.find("mandelbrot").orElseThrow();

    FunctionDescriptor descriptor = FunctionDescriptor.ofVoid(
      ValueLayout.JAVA_FLOAT, // cx0
      ValueLayout.JAVA_FLOAT, // cy0
      ValueLayout.JAVA_FLOAT, // scale
      ValueLayout.JAVA_INT,   // width
      ValueLayout.JAVA_INT,   // height
      ValueLayout.JAVA_INT,   // maxIter
      ValueLayout.ADDRESS     // int* hostOutput
    );

    Linker linker = Linker.nativeLinker();
    MH = linker.downcallHandle(function, descriptor);
  }

  public static void computeFloat(
    float cx0,
    float cy0,
    float scale,
    int width,
    int height,
    int max,
    int[][] iterationsPixels) {

    try (Arena arena = Arena.ofConfined()) {

      MemorySegment buffer = arena.allocate(ValueLayout.JAVA_INT, (long) width * height);

      MH.invokeExact(
        cx0, cy0, scale,
        width, height,
        max,
        buffer
      );

      int[] flat = buffer.toArray(ValueLayout.JAVA_INT);

      for (int y = 0; y < height; y++) {
        System.arraycopy(
          flat,
          y * width,
          iterationsPixels[y],
          0,
          width
        );
      }
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  public static void computeDouble(
    double cx0,
    double cy0,
    double scale,
    int width,
    int height,
    int max,
    int[][] iterationsPixels) {

    try (Arena arena = Arena.ofConfined()) {

      MemorySegment buffer = arena.allocate(ValueLayout.JAVA_INT, (long) width * height);

      MH.invokeExact(
        cx0, cy0, scale,
        width, height,
        max,
        buffer
      );

      int[] flat = buffer.toArray(ValueLayout.JAVA_INT);

      for (int y = 0; y < height; y++) {
        System.arraycopy(
          flat,
          y * width,
          iterationsPixels[y],
          0,
          width
        );
      }
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }
}
