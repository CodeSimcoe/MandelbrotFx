package com.codesimcoe.demo;

class _03_Properties {

  value record ValueComplex(double re, double im) {
  }

  // JEP 512 : Compact Source Files and Instance Main Methods
  void main() {
    IO.println("JDK version : " + Runtime.version());

    var c1 = new ValueComplex(2, -1);
    var c2 = new ValueComplex(2, -1);
    IO.println(c1);

    IO.println("isValue : " + c1.getClass().isValue());
    IO.println("isIdentity : " + c1.getClass().isIdentity());

    IO.println("c1 == c2 : " + (c1 == c2));

    IO.println("==============");

    IO.println(System.identityHashCode(c1));
    IO.println(System.identityHashCode(c2));

    IO.println("==============");

    IO.println(System.identityHashCode(new Object()));
    IO.println(System.identityHashCode(new Object()));

//  synchronized (c1) {}

//  Object o1 = c1;
//  synchronized (o1) {}

    // Objects::requireIdentity
//    new java.lang.ref.WeakReference<>(c1);
  }
}
