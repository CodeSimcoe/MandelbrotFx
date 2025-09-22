package com.codesimcoe.mandelbrotfx;

import javafx.scene.media.Media;

import java.io.File;

record NamedMusic(String name, Media media) implements Named {
  NamedMusic(final String name, final String path) {
    String file = new File(path).toURI().toString();
    this(name, new Media(file));
  }

  @Override
  public String getName() {
    return this.name;
  }
}