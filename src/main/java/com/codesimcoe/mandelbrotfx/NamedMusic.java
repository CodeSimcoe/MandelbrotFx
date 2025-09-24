package com.codesimcoe.mandelbrotfx;

import javafx.scene.media.Media;

import java.io.File;

record NamedMusic(String name, Media media) implements Named {
  NamedMusic(String name, String path) {
    this(name, new Media(new File(path).toURI().toString()));
  }

  @Override
  public String getName() {
    return this.name;
  }
}