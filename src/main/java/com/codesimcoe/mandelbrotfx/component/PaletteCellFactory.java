package com.codesimcoe.mandelbrotfx.component;

import com.codesimcoe.mandelbrotfx.palette.ColorPalette;
import com.codesimcoe.mandelbrotfx.palette.GradientColorPalette;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Arrays;

public final class PaletteCellFactory {

  public static void apply(final ComboBox<ColorPalette> comboBox) {
    comboBox.setCellFactory(cb -> new ListCell<>() {
      private final HBox hbox = new HBox(5);
      private final Label nameLabel = new Label();

      {
        this.hbox.getChildren().add(this.nameLabel);
      }

      @Override
      protected void updateItem(final ColorPalette palette, final boolean empty) {
        super.updateItem(palette, empty);
        if (empty || palette == null) {
          this.setGraphic(null);
        } else {
          this.nameLabel.setText(palette.getName());

          // Remove old rectangles
          this.hbox.getChildren().removeIf(node -> node instanceof Rectangle);

          if (palette instanceof GradientColorPalette(_, Color[] keyColors)) {
            Arrays.stream(keyColors)
              .map(ColorPreviewSquare::newColorPreviewSquare)
              .forEach(this.hbox.getChildren()::add);
          }

          this.setGraphic(this.hbox);
        }
      }
    });

    // Button cell to show name only
    comboBox.setButtonCell(new ListCell<>() {
      @Override
      protected void updateItem(final ColorPalette palette, final boolean empty) {
        super.updateItem(palette, empty);
        if (empty || palette == null) {
          this.setGraphic(null);
        } else {
          this.setText(palette.getName());
        }
      }
    });
  }
}
