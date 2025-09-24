package com.codesimcoe.mandelbrotfx.component;

import com.codesimcoe.mandelbrotfx.palette.ColorPalette;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.Arrays;

public final class PaletteCellFactory {

  public static void apply(ComboBox<ColorPalette> comboBox) {
    comboBox.setCellFactory(_ -> createCell());
    comboBox.setButtonCell(createCell());
  }

  private static void setupKeyColors(ColorPalette palette, Pane pane) {

    pane.getChildren().removeIf(node -> node instanceof Rectangle);

    Arrays.stream(palette.getKeyColors())
      .map(ColorPreviewSquare::newColorPreviewSquare)
      .forEach(pane.getChildren()::add);
  }

  private static ListCell<ColorPalette> createCell() {
    return new ListCell<>() {
      private final HBox hbox = new HBox(5);
      private final Text nameText = new Text();

      {
        this.hbox.getChildren().add(this.nameText);
      }

      @Override
      protected void updateItem(ColorPalette palette, boolean empty) {
        super.updateItem(palette, empty);

        if (empty || palette == null) {
          this.setGraphic(null);
        } else {
          this.nameText.setText(palette.getName());
          setupKeyColors(palette, this.hbox);
          this.setGraphic(this.hbox);
        }
      }
    };
  }

}
