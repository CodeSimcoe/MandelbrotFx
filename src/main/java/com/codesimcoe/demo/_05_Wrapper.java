package com.codesimcoe.demo;

public class _05_Wrapper {

  void main() {
    Integer i1 = Integer.valueOf(1024);
    Integer i2 = Integer.valueOf(1024);

    IO.println("i1 : " + i1);
    IO.println("i2 : " + i2);

    IO.println("== : " + (i1 == i2));
    IO.println("equals : " + i1.equals(i2));
  }
}