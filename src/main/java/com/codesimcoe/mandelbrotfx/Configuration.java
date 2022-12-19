package com.codesimcoe.mandelbrotfx;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Configuration {

    public static final int SETTINGS_WIDTH = 300;

    private final IntegerProperty colorOffsetProperty = new SimpleIntegerProperty();

    // Eagerly instantiated singleton
    private static final Configuration instance = new Configuration();
    public static Configuration getInstance() {
        return instance;
    }

    public IntegerProperty getColorOffsetProperty() {
        return this.colorOffsetProperty;
    }
}