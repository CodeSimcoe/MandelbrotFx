package com.codesimcoe.mandelbrotfx;

public enum SnapshotMode {
  _1K("1k", 1024),
  _2K("2k", 2 * 1024),
  _4K("4k", 4 * 1024),
  _8K("8k", 8 * 1024),
  _16K("16k", 16 * 1024),
  ;

  private final String name;
  private final int resolution;

  SnapshotMode(final String name, final int resolution) {
    this.name = name;
    this.resolution = resolution;
  }

  public String getName() {
    return this.name;
  }

  public int getResolution() {
    return this.resolution;
  }
}
