package com.codesimcoe.demo;

import jdk.internal.vm.annotation.NullRestricted;

// -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlineLayout --add-exports=java.base/jdk.internal.value=mandelbrotfx
public class _07_NullRestricted {

  record Data(
    @NullRestricted // Boolean!
    Boolean bool1,

    @NullRestricted // Boolean!
    Boolean bool2,

//    @NullRestricted // Double!
    Double d) {}

  void main() {
    Data data = new Data(true, false, 3.14);
    IO.println(data);
  }
}
