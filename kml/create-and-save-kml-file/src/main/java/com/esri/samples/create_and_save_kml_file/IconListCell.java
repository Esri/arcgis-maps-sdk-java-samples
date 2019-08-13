/*
 * Copyright 2019 Esri.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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