package com.codesimcoe.mandelbrotfx;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.nio.file.Path;

public final class MandelbrotFFMAVX {

  private static final Arena ARENA;
  private static final MethodHandle MH;

  static {
    ARENA = Arena.ofShared();
    Path path = Path.of("quickbrot/quickbrot.dll");
    SymbolLookup lookup = SymbolLookup.libraryLookup(path, ARENA);
    MemorySegment function = lookup.find("mandelbrotLineAVX").orElseThrow();
    FunctionDescriptor descriptor = FunctionDescriptor.ofVoid(
      ValueLayout.JAVA_INT,
      ValueLayout.JAVA_INT,
      ValueLayout.JAVA_INT,
      ValueLayout.JAVA_FLOAT,
      ValueLayout.JAVA_FLOAT,
      ValueLayout.JAVA_FLOAT,
      ValueLayout.JAVA_FLOAT,
      ValueLayout.ADDRESS
    );

    Linker linker = Linker.nativeLinker();
    MH = linker.downcallHandle(function, descriptor);
  }

  public static void computeLine(
    int y,
    int width,
    int max,
    float xl,
    float yl,
    float xRes,
    float yRes,
    int[] out) {

    try (Arena arena = Arena.ofConfined()) {

      MemorySegment buf = arena.allocate(
        out.length * ValueLayout.JAVA_INT.byteSize(),
        ValueLayout.JAVA_INT.byteAlignment()
      );

      MH.invokeExact(y, width, max, xl, yl, xRes, yRes, buf);

      MemorySegment.ofArray(out).copyFrom(buf);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }
}
