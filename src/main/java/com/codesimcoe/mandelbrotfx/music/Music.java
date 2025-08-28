package com.codesimcoe.mandelbrotfx.music;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Music {

  public static final Map<String, String> MUSICS;
  static {
    Map<String, String> map = new LinkedHashMap<>();
    map.put("Ambient 1", "music/ambient-1.mp3");
    map.put("Ambient 2", "music/ambient-2.mp3");
    map.put("Ambient 3", "music/ambient-3.mp3");
    map.put("Ambient 4", "music/ambient-4.mp3");
    MUSICS = Collections.unmodifiableMap(map);
  }

  private Music() {
    // Non-instantiable
  }
}
