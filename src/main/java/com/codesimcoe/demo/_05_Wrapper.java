package com.codesimcoe.demo;

import java.time.LocalDate;
import java.time.Month;

public class _05_Wrapper {

  void main() {
    Integer i1 = Integer.valueOf(1024);
    Integer i2 = Integer.valueOf(1024);

    IO.println("i1 : " + i1);
    IO.println("i2 : " + i2);

    IO.println("== : " + (i1 == i2));
    IO.println("equals : " + i1.equals(i2));

    IO.println("==============");

    // Long, Double, Character, ...
    IO.println(Long.valueOf(2026) == Long.valueOf(2026));
    IO.println(Double.valueOf(2026d) == Double.valueOf(2026d));
    IO.println(Character.valueOf('S') == Character.valueOf('S'));
    // ...

    IO.println("==============");

    LocalDate t1 = LocalDate.of(2026, Month.JANUARY, 23);
    LocalDate t2 = LocalDate.of(2026, Month.JANUARY, 23);

    IO.println(t1 == t2);
  }
}