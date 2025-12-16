package com.codesimcoe.mandelbrotfx;

public sealed interface MandelbrotStrategy {

  int computeEscape(double re0, double im0, int max);

  enum MandelbrotStrategyType implements Named {
    PRIMITIVE("Primitive", PrimitiveStrategy.INSTANCE),
    RECORD("Record", RecordStrategy.INSTANCE),
    VALUE_RECORD("Value record", ValueRecordStrategy.INSTANCE);

    private final String name;
    private final MandelbrotStrategy strategy;

    MandelbrotStrategyType(String name, MandelbrotStrategy strategy) {
      this.name = name;
      this.strategy = strategy;
    }

    public MandelbrotStrategy getStrategy() {
      return this.strategy;
    }

    @Override
    public String getName() {
      return this.name;
    }
  }

  enum PrimitiveStrategy implements MandelbrotStrategy {

    INSTANCE;

    @Override
    public int computeEscape(double re0, double im0, int max) {

      double re = 0;
      double im = 0;

      // Squared values
      double x2 = 0;
      double y2 = 0;

      // Iteration
      int i = 0;

      double modulusSquared = 0;
      while (modulusSquared <= 4 && i < max) {
        // fma : 2 * re * im + im0
        im = Math.fma(2 * re, im, im0);
        re = x2 - y2 + re0;
        x2 = re * re;
        y2 = im * im;
        modulusSquared = x2 + y2;
        i++;
      }

      return i;
    }
  }

  enum RecordStrategy implements MandelbrotStrategy {

    INSTANCE;

    @Override
    public int computeEscape(double re0, double im0, int max) {
      Complex c = new Complex(re0, im0);
      Complex z = Complex.ZERO;
      int i = 0;

      while (z.magnitudeSquared() < 4 && i < max) {
        z = z.square().add(c);
        i++;
      }

      return i;
    }
  }

  enum ValueRecordStrategy implements MandelbrotStrategy {

    INSTANCE;

    @Override
    public int computeEscape(double re0, double im0, int max) {
      ValueComplex c = new ValueComplex(re0, im0);
      ValueComplex z = ValueComplex.ZERO;
      int i = 0;

      while (z.magnitudeSquared() < 4 && i < max) {
        z = z.square().add(c);
        i++;
      }

      return i;
    }
  }
}
