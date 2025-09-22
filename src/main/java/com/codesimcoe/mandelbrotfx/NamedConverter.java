package com.codesimcoe.mandelbrotfx;

import javafx.util.StringConverter;

public class NamedConverter<T extends Named> extends StringConverter<T> {

  @Override
  public String toString(final T object) {
    return object == null ? "" : object.getName();
  }

  @Override
  public T fromString(final String string) {
    throw new UnsupportedOperationException();
  }
}
