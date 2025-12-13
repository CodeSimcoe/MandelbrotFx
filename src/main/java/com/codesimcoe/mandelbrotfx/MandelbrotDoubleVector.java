package com.codesimcoe.mandelbrotfx;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

public final class MandelbrotDoubleVector {

  private static final VectorSpecies<Double> DS = DoubleVector.SPECIES_256;
  private static final VectorSpecies<Integer> IS = IntVector.SPECIES_128;

  static void mandelbrotVector(
    double[] x0Arr,
    double[] y0Arr,
    int[] iterations,
    int maxIter) {

    int length = DS.length();

    // Hoist constants outside loop
    DoubleVector four = DoubleVector.broadcast(DS, 4.0);
    IntVector one = IntVector.broadcast(IS, 1);
    IntVector maxV = IntVector.broadcast(IS, maxIter);

    for (int base = 0; base < x0Arr.length; base += length) {

      DoubleVector x0 = DoubleVector.fromArray(DS, x0Arr, base);
      DoubleVector y0 = DoubleVector.fromArray(DS, y0Arr, base);
      DoubleVector x = x0;
      DoubleVector y = y0;

      IntVector iter = IntVector.zero(IS);
      VectorMask<Integer> active = VectorMask.fromLong(IS, -1L);

      while (active.anyTrue()) {

        // z² = (x + iy)² = x² - y² + 2ixy
        DoubleVector xx = x.mul(x);
        DoubleVector yy = y.mul(y);
        DoubleVector xy = x.mul(y);

        // New z = z² + c
        x = xx.sub(yy).add(x0);
        y = xy.add(xy).add(y0);  // Fused: 2xy + y0

        // Magnitude check: |z|² < 4
        DoubleVector magSq = xx.add(yy);
        VectorMask<Double> inside = magSq.compare(VectorOperators.LT, four);

        // Update active mask: inside && iter < maxIter
        VectorMask<Integer> insideInt = inside.cast(IS);
        VectorMask<Integer> belowMax = iter.compare(VectorOperators.LT, maxV);
        active = insideInt.and(belowMax);

        // Increment counter for active lanes
        iter = iter.add(one, active);
      }

      // Store results
      iter.intoArray(iterations, base);
    }
  }
}
