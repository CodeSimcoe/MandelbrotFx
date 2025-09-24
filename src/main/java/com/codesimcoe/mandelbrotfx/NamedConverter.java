package com.codesimcoe.mandelbrotfx;

import javafx.util.StringConverter;

public class NamedConverter<T extends Named> extends StringConverter<T> {

  @Override
  public String toString(T object) {
    if (object == null) return "";
    return object.getName();
  }

  @Override
  public T fromString(String string) {
    return null;
  }
}
