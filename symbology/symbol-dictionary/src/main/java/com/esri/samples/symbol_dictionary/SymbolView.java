/*
 * Copyright 2016 Esri.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.esri.samples.symbol_dictionary;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.symbology.SymbolStyleSearchResult;

class SymbolView extends HBox implements Initializable {

  @FXML private ImageView imageView;
  @FXML private Label name;
  @FXML private Label tags;
  @FXML private Label symbolClass;
  @FXML private Label category;
  @FXML private Label key;

  private final SymbolStyleSearchResult styleSymbolSearchResult;

  /**
   * Creates a view of a symbol with a picture and description.
   *
   * @param symbolResult symbol result from a symbol dictionary search
   */
  SymbolView(SymbolStyleSearchResult symbolResult) {
    styleSymbolSearchResult = symbolResult;

    // Set the view of this component to the fxml file
    var loader = new FXMLLoader(getClass().getResource("/symbol_dictionary/symbol_view.fxml"));
    loader.setRoot(this);
    loader.setController(this);

    try {
      loader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // initialize the component values
    name.setText(styleSymbolSearchResult.getName());
    tags.setText(styleSymbolSearchResult.getTags().toString());
    symbolClass.setText(styleSymbolSearchResult.getSymbolClass());
    category.setText(styleSymbolSearchResult.getCategory());
    key.setText(styleSymbolSearchResult.getKey());

    // set image for non-text symbols
    if (!category.getText().startsWith("Text")) {
      // get the symbol and create a swatch from it
      styleSymbolSearchResult.getSymbolAsync().toCompletableFuture()
        .thenCompose(symbol ->
          symbol.createSwatchAsync(40, 40, Color.color(1.0, 1.0, 1.0, 0.0), new Point(0, 0, 0)).toCompletableFuture())
        .whenComplete((image, ex) -> {
          if (ex == null) {
            // if the symbol fetch and swatch creation complete successfully, add the resulting image to the ImageView
            imageView.setImage(image);
          } else {
            // display an error if the symbol search or swatch creation completed with an exception
            new Alert(Alert.AlertType.ERROR, "Error creating swatch from symbol" + ex.getMessage()).show();
          }
        });
    }
  }
}
