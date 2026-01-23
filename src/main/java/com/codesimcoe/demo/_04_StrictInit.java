package com.codesimcoe.demo;

public class _04_StrictInit {

    /* value */ class SafeComplex {

    private final double re;
    private final double im;

    // JEP 513 : Flexible Constructor Bodies
    public SafeComplex(double re, double im) {

      // Prologue
      // Can initialize field or perform checks
//      this.re = re;
//      this.im = im;

      // Cannot reference 'this' before superclass constructor is called
//      this.prettyPrint();

      super();

      // Validation
      if (Double.isNaN(re)) {
        throw new IllegalArgumentException("re is NaN");
      }
      if (Double.isNaN(im)) {
        throw new IllegalArgumentException("im is NaN");
      }

      // Epilogue
      // "this" becomes available

      // strict field re is not initialized before the supertype constructor has been called
      this.re = re;
      this.im = im;

      this.prettyPrint();
    }

    void prettyPrint() {
      IO.println("re = " + this.re + ", im = " + this.im);
    }
  }

  void main() {
    var c = new SafeComplex(0, 1);
    IO.println(c);
  }
}
