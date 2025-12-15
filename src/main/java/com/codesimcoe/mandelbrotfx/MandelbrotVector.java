package com.codesimcoe.mandelbrotfx;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

public final class MandelbrotVector {

  // Double
  private static final VectorSpecies<Double> DS = DoubleVector.SPECIES_256;
  private static final VectorSpecies<Integer> IS128 = IntVector.SPECIES_128;
  private static final DoubleVector D_FOUR = DoubleVector.broadcast(DS, 4.0);
  private static final int D_LENGTH = DS.length();

  // Float
  private static final VectorSpecies<Float> FS = FloatVector.SPECIES_256;
  private static final VectorSpecies<Integer> IS256 = IntVector.SPECIES_256;
  private static final FloatVector F_FOUR = FloatVector.broadcast(FS, 4.0f);
  private static final int F_LENGTH = FS.length();

  private MandelbrotVector() {}

  public static int[] computeLineDouble(
    int width,
    Viewport viewport,
    double im0,
    int maxIter) {

    int[] iterations = new int[width];

    // Hoist constants outside loop

    IntVector one = IntVector.broadcast(IS128, 1);
    IntVector maxV = IntVector.broadcast(IS128, maxIter);

    double scale = viewport.getSize() / width;

    double[] xIndices = new double[width];
    for (int i = 0; i < width; i++) {
      xIndices[i] = i;
    }

    for (int i = 0; i < width; i += D_LENGTH) {

      DoubleVector x0 = DoubleVector.fromArray(DS, xIndices, i)
        .sub(width / 2.0)
        .mul(scale)
        .add(viewport.getCenterRe());

      DoubleVector y0 = DoubleVector.broadcast(DS, im0);
      DoubleVector x = x0;
      DoubleVector y = y0;

      IntVector iter = IntVector.zero(IS128);
      VectorMask<Integer> active = VectorMask.fromLong(IS128, -1L);

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
        VectorMask<Double> inside = magSq.compare(VectorOperators.LT, D_FOUR);

        // Update active mask: inside && iter < maxIter
        VectorMask<Integer> insideInt = inside.cast(IS128);
        VectorMask<Integer> belowMax = iter.compare(VectorOperators.LT, maxV);
        active = insideInt.and(belowMax);

        // Increment counter for active lanes
        iter = iter.add(one, active);
      }

      // Store results
      iter.intoArray(iterations, i);
    }

    return iterations;
  }

  public static int[] computeLineFloat(
    int width,
    Viewport viewport,
    float im0,
    int maxIter) {

    int[] iterations = new int[width];

    // Hoist constants outside loop
    IntVector one = IntVector.broadcast(IS256, 1);
    IntVector maxV = IntVector.broadcast(IS256, maxIter);

    float scale = (float) (viewport.getSize() / width);
    float center = (float) viewport.getCenterRe();

    float[] xIndices = new float[width];
    for (int i = 0; i < width; i++) {
      xIndices[i] = i;
    }

    for (int i = 0; i < width; i += F_LENGTH) {

      FloatVector x0 = FloatVector.fromArray(FS, xIndices, i)
        .sub(width / 2.0f)
        .mul(scale)
        .add(center);

      FloatVector y0 = FloatVector.broadcast(FS, im0);
      FloatVector x = x0;
      FloatVector y = y0;

      IntVector iter = IntVector.zero(IS256);
      VectorMask<Integer> active = VectorMask.fromLong(IS256, -1L);

      while (active.anyTrue()) {

        // z² = (x + iy)² = x² - y² + 2ixy
        FloatVector xx = x.mul(x);
        FloatVector yy = y.mul(y);
        FloatVector xy = x.mul(y);

        // New z = z² + c
        x = xx.sub(yy).add(x0);
        y = xy.add(xy).add(y0);  // Fused: 2xy + y0

        // Magnitude check: |z|² < 4
        FloatVector magSq = xx.add(yy);
        VectorMask<Float> inside = magSq.compare(VectorOperators.LT, F_FOUR);

        // Update active mask: inside && iter < maxIter
        VectorMask<Integer> insideInt = inside.cast(IS256);
        VectorMask<Integer> belowMax = iter.compare(VectorOperators.LT, maxV);
        active = insideInt.and(belowMax);

        // Increment counter for active lanes
        iter = iter.add(one, active);
      }

      // Store results
      iter.intoArray(iterations, i);
    }

    return iterations;
  }
}
