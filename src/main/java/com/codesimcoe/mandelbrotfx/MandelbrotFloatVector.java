package com.codesimcoe.mandelbrotfx;

import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

public final class MandelbrotFloatVector {

  private static final VectorSpecies<Float> FS = FloatVector.SPECIES_256;
  private static final VectorSpecies<Integer> IS = IntVector.SPECIES_256;

  static void mandelbrotVector(
    float[] x0Arr,
    float[] y0Arr,
    int[] iterations,
    int maxIter) {

    int length = FS.length();

    for (int base = 0; base < x0Arr.length; base += length) {

      FloatVector x = FloatVector.fromArray(FS, x0Arr, base);
      FloatVector y = FloatVector.fromArray(FS, y0Arr, base);
      FloatVector x0 = x;
      FloatVector y0 = y;

      IntVector iter = IntVector.zero(IS);
      VectorMask<Float> active = VectorMask.fromLong(FS, -1L);

      // Constant vector 4.0
      FloatVector four = FloatVector.broadcast(FS, 4.0f);
      IntVector maxV = IntVector.broadcast(IS, maxIter);

      while (active.anyTrue()) {

        // Compute xx = x*x, yy = y*y
        FloatVector xx = x.mul(x);
        FloatVector yy = y.mul(y);

        // ab = xx + yy
        FloatVector ab = xx.add(yy);

        // xy = 2*x*y
        FloatVector xy = x.mul(y).add(x.mul(y));

        // Mandelbrot: x = xx-yy + x0, y = xy + y0
        x = xx.sub(yy).add(x0);
        y = xy.add(y0);

        // Compare ab < 4
        VectorMask<Float> inside = ab.compare(VectorOperators.LT, four);

        // Compare iter < max
        VectorMask<Integer> reachedMax =
          iter.compare(VectorOperators.EQ, maxV);

        // Active pixels = inside & !reachedMax
        VectorMask<Integer> nextActive =
          reachedMax.not().and(inside.cast(IS));

        // Increase iteration counters where active
        IntVector inc = IntVector.broadcast(IS, 1);
        iter = iter.add(inc, nextActive);

        // Update float mask for loop continuation
        active = nextActive.cast(FS);
      }

      // Store final iteration counts
      iter.intoArray(iterations, base);
    }
  }
}
