package com.codesimcoe.demo;

import jdk.internal.value.ValueClass;

// jcmd _08_Flattening GC.class_histogram | head
// --add-exports=java.base/jdk.internal.value=mandelbrotfx
public class _08_Flattening {

  record Orbit(float re, float im) {}
  value record ValueOrbit(float re, float im) {}
  value record NonNullValueOrbit(float re, float im) {}

  void main() throws InterruptedException {

    int size = 1_000_000;

    //   num     #instances         #bytes  class name (module)
    // -------------------------------------------------------
    //    1:       1000000       24000000  com.codesimcoe.demo._08_Flattening$Orbit (mandelbrotfx)
    //    2:       1000000       24000000  com.codesimcoe.demo._08_Flattening$ValueOrbit (mandelbrotfx)
    //    3:             1        8000016  [Lcom.codesimcoe.demo._08_Flattening$NonNullValueOrbit; (mandelbrotfx)
    // Orbit[]
    var data1 = new Orbit[size];

    // value Orbit[]
    var data2 = (ValueOrbit[]) ValueClass.newNullableAtomicArray(ValueOrbit.class, size);

    // Orbit![]
    var data3 = (NonNullValueOrbit[]) ValueClass.newNullRestrictedAtomicArray(NonNullValueOrbit.class, size, new NonNullValueOrbit(0, 0));

    for (int i = 0; i < size; i++) {
      data1[i] = new Orbit(i, i);
      data2[i] = new ValueOrbit(i, i);
      data3[i] = new NonNullValueOrbit(i, i);
    }

    Thread.sleep(10_000);

    IO.println(data1);
    IO.println(data2);
    IO.println(data3);
  }
}
