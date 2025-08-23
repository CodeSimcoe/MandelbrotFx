package com.codesimcoe.mandelbrotfx.component;

import com.codesimcoe.mandelbrotfx.palette.ColorPalette;
import com.codesimcoe.mandelbrotfx.palette.GradientColorPalette;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.Arrays;

public class PaletteButtonCell extends ListCell<ColorPalette> {

  private final HBox hbox = new HBox(5);
  private final Text nameText = new Text();

  public PaletteButtonCell() {
    this.hbox.getChildren().add(this.nameText);
  }

  @Override
  protected void updateItem(final ColorPalette palette, final boolean empty) {
    super.updateItem(palette, empty);

    if (empty || palette == null) {
      this.setGraphic(null);
    } else {
      this.nameText.setText(palette.getName());

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
}
