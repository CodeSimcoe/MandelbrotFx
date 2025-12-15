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

  private static final MethodHandle FLOAT_MH;
  private static final MethodHandle DOUBLE_MH;

  static {
    Arena arena = Arena.ofShared();
    Path floatPath = Path.of("cuda/mandelbrot-cuda-float.dll");
    Path doublePath = Path.of("cuda/mandelbrot-cuda-double.dll");
    SymbolLookup floatLookup = SymbolLookup.libraryLookup(floatPath, arena);
    SymbolLookup doubleLookup = SymbolLookup.libraryLookup(doublePath, arena);
    MemorySegment floatFunction = floatLookup.find("mandelbrot").orElseThrow();
    MemorySegment doubleFunction = doubleLookup.find("mandelbrot").orElseThrow();

    FunctionDescriptor floatDescriptor = FunctionDescriptor.ofVoid(
      ValueLayout.JAVA_FLOAT, // cx0
      ValueLayout.JAVA_FLOAT, // cy0
      ValueLayout.JAVA_FLOAT, // scale
      ValueLayout.JAVA_INT,   // width
      ValueLayout.JAVA_INT,   // height
      ValueLayout.JAVA_INT,   // maxIter
      ValueLayout.ADDRESS     // int* hostOutput
    );

    FunctionDescriptor doubleDescriptor = FunctionDescriptor.ofVoid(
      ValueLayout.JAVA_DOUBLE, // cx0
      ValueLayout.JAVA_DOUBLE, // cy0
      ValueLayout.JAVA_DOUBLE, // scale
      ValueLayout.JAVA_INT,    // width
      ValueLayout.JAVA_INT,    // height
      ValueLayout.JAVA_INT,    // maxIter
      ValueLayout.ADDRESS      // int* hostOutput
    );

    Linker linker = Linker.nativeLinker();
    FLOAT_MH = linker.downcallHandle(floatFunction, floatDescriptor);
    DOUBLE_MH = linker.downcallHandle(doubleFunction, doubleDescriptor);
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

      FLOAT_MH.invokeExact(
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

      DOUBLE_MH.invokeExact(
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
