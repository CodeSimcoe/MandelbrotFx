package com.codesimcoe.demo;

public class _05_Wrapper {

  void main() {
    var i1 = Integer.valueOf(1024);
    IO.println(i1);
    var i2 = Integer.valueOf(1024);
    IO.println(i2);

    IO.println(i1 == i2);
    IO.println(i1.equals(i2));
  }
}