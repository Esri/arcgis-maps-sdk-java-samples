package com.esri.samples.read_symbols_from_mobile_style_file;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.shape.Rectangle;

import com.esri.arcgisruntime.symbology.ColorUtil;

/**
 * Shows the colors in the color selection list view.
 */
class ColorListCell extends ListCell<Integer> {
  private final Rectangle rectangle;

  ColorListCell() {
    // set the cell to display only a graphic
    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    // create a rectangle to display in the cell
    rectangle = new Rectangle(10, 10);
  }

  @Override
  protected void updateItem(Integer item, boolean empty) {
    super.updateItem(item, empty);

    if (item == null || empty) {
      // if the item in the list view is an empty item, show nothing
      setGraphic(null);
    } else {
      // convert the 0xAARRGGBB format to a Color object and apply it to the rectangle fill
      rectangle.setFill(ColorUtil.argbToColor(item));
      // set the rectangle to be displayed in the cell
      setGraphic(rectangle);
    }
  }
}
