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
 * Shows an image based on the URL String value.
 */
public class ImageURLListCell extends ListCell<String> {

  @Override
  protected void updateItem(String item, boolean empty) {
    super.updateItem(item, empty);

    ImageView imageView = new ImageView();

    if (item == null || empty) {
      // show 'Default' text instead of an image when the String is null
      setGraphic(null);
      setText("Default");
    } else {
      // display image from URL
      imageView.setImage(new Image(item, 0, 15, true, true));
      setGraphic(imageView);
      setText(null);
    }
  }
}