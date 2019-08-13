package com.esri.samples.create_and_save_kml_file;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Displays a preview of the available icons in the icon selection combobox
 */
public class IconListCell extends ListCell<String> {
  private final ImageView iconImageView;

  IconListCell() {
    // set the cell to display only a graphic
    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    iconImageView = new ImageView();
  }

  @Override
  protected void updateItem(String item, boolean empty) {
    super.updateItem(item, empty);

    if (item == null || empty) {
      // if the item in the combobox is an empty item, show nothing
      setGraphic(null);
    } else {
      // get the symbol from the combobox entry, and create an image from it
      iconImageView.setImage(new Image(item, 0, 15, true, true));
      setGraphic(iconImageView);
    }
  }
}