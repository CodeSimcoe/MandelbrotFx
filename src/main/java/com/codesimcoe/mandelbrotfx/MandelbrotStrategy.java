package com.codesimcoe.mandelbrotfx;

public sealed interface MandelbrotStrategy {

  int compute(double x, double y, int max);

  enum MandelbrotStrategyType implements Named {
    PRIMITIVE("Primitive", PrimitiveStrategy.INSTANCE),
    RECORD("Record", RecordStrategy.INSTANCE),
    VALUE_RECORD("Value record", ValueRecordStrategy.INSTANCE);

    private final String name;
    private final MandelbrotStrategy strategy;

    MandelbrotStrategyType(final String name, final MandelbrotStrategy strategy) {
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
    public int compute(final double x0, final double y0, final int max) {

      double x = 0;
      double y = 0;

      // Squared values
      double x2 = 0;
      double y2 = 0;

      // Iteration
      int i = 0;

      double modulusSquared = x2 + y2;
      while (modulusSquared <= 4 && i < max) {
        y = 2 * x * y + y0;
        x = x2 - y2 + x0;
        x2 = x * x;
        y2 = y * y;
        modulusSquared = x2 + y2;
        i++;
      }

      return i;
    }
  }

  enum RecordStrategy implements MandelbrotStrategy {

    INSTANCE;

    @Override
    public int compute(final double x, final double y, final int max) {
      Complex c = new Complex(x, y);
      Complex z = new Complex(0, 0);
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
    public int compute(final double x, final double y, final int max) {
      ValueComplex c = new ValueComplex(x, y);
      ValueComplex z = new ValueComplex(0, 0);
      int i = 0;

      while (z.magnitudeSquared() < 4 && i < max) {
        z = z.square().add(c);
        i++;
      }

      return i;
    }
  }
}
