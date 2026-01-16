package com.codesimcoe.demo;

// -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlineLayout
public class _06_FieldLayout {

  record Data(Boolean bool1, Boolean bool2, Double d) {}

  void main() {
    Data data = new Data(true, false, 3.14);
    IO.println(data);
  }
}
