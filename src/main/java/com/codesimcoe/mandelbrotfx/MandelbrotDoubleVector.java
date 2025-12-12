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

    for (int base = 0; base < x0Arr.length; base += length) {

      DoubleVector x = DoubleVector.fromArray(DS, x0Arr, base);
      DoubleVector y = DoubleVector.fromArray(DS, y0Arr, base);
      DoubleVector x0 = x;
      DoubleVector y0 = y;

      IntVector iter = IntVector.zero(IS);
      VectorMask<Double> active = VectorMask.fromLong(DS, -1L);

      DoubleVector four = DoubleVector.broadcast(DS, 4.0);
      IntVector maxV = IntVector.broadcast(IS, maxIter);

      while (active.anyTrue()) {

        // xx = x^2, yy = y^2
        DoubleVector xx = x.mul(x);
        DoubleVector yy = y.mul(y);

        // ab = xx + yy
        DoubleVector ab = xx.add(yy);

        // xy = 2*x*y
        DoubleVector xy = x.mul(y).add(x.mul(y));

        // x = xx - yy + x0
        x = xx.sub(yy).add(x0);

        // y = xy + y0
        y = xy.add(y0);

        // inside = ab < 4
        VectorMask<Double> inside =
          ab.compare(VectorOperators.LT, four);

        // reachedMax = iter == max
        VectorMask<Integer> reachedMax =
          iter.compare(VectorOperators.EQ, maxV);

        // nextActive = inside && !reachedMax
        VectorMask<Integer> nextActive =
          inside.cast(IS).and(reachedMax.not());

        // iter += 1 where active
        iter = iter.add(IntVector.broadcast(IS, 1), nextActive);

        // convert integer mask → double mask for loop
        active = nextActive.cast(DS);
      }

      // Store results
      iter.intoArray(iterations, base);
    }
  }
}
