package com.codesimcoe.demo;

import jdk.internal.value.ValueClass;
import jdk.internal.vm.annotation.LooselyConsistentValue;

// jcmd _09_FlatteningNonAtomic GC.class_histogram | head
// --add-exports=java.base/jdk.internal.value=mandelbrotfx
public class _09_FlatteningNonAtomic {

  record Complex(double re, double im) {}
  value record ValueComplex(double re, double im) {}
  value record NonNullValueComplex(double re, double im) {}
  @LooselyConsistentValue value record NonNullNonAtomicValueComplex(double re, double im) {}

  void main() throws InterruptedException {

    int size = 1_000_000;

    //   num     #instances         #bytes  class name (module)
    // -------------------------------------------------------
    //    1:       1000000       32000000  com.codesimcoe.demo._09_FlatteningNonAtomic$Complex (mandelbrotfx)
    //    2:       1000000       32000000  com.codesimcoe.demo._09_FlatteningNonAtomic$NonNullValueComplex (mandelbrotfx)
    //    3:       1000000       32000000  com.codesimcoe.demo._09_FlatteningNonAtomic$ValueComplex (mandelbrotfx)
    //    4:             1       16000016  [Lcom.codesimcoe.demo._09_FlatteningNonAtomic$NonNullNonAtomicValueComplex; (mandelbrotfx)
    //    5:             1        4000016  [Lcom.codesimcoe.demo._09_FlatteningNonAtomic$Complex; (mandelbrotfx)
    //    6:             1        4000016  [Lcom.codesimcoe.demo._09_FlatteningNonAtomic$NonNullValueComplex; (mandelbrotfx)
    //    7:             1        4000016  [Lcom.codesimcoe.demo._09_FlatteningNonAtomic$ValueComplex; (mandelbrotfx)
    var data1 = new Complex[size];
    var data2 = (ValueComplex[]) ValueClass.newNullableAtomicArray(ValueComplex.class, size);
    var data3 = (NonNullValueComplex[]) ValueClass.newNullRestrictedAtomicArray(NonNullValueComplex.class, size, new NonNullValueComplex(0, 0));
    var data4 = (NonNullNonAtomicValueComplex[]) ValueClass.newNullRestrictedNonAtomicArray(NonNullNonAtomicValueComplex.class, size, new NonNullNonAtomicValueComplex(0, 0));

    for (int i = 0; i < size; i++) {
      data1[i] = new Complex(i, i);
      data2[i] = new ValueComplex(i, i);
      data3[i] = new NonNullValueComplex(i, i);
      data4[i] = new NonNullNonAtomicValueComplex(i, i);
    }

    Thread.sleep(10_000);

    IO.println(data1);
    IO.println(data2);
    IO.println(data3);
    IO.println(data4);
  }
}
