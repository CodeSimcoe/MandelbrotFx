package com.codesimcoe.mandelbrotfx;

public enum SnapshotMode {
  _1K("1k", 1024),
  _2K("2k", 2048),
  _3K("4k", 4096),
  _4K("8k", 8192);

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
