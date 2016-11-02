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

package com.esri.samples.symbology.symbol_dictionary;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.symbology.Symbol;
import com.esri.arcgisruntime.symbology.SymbolStyleSearchResult;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

class SymbolView extends HBox implements Initializable {

  private @FXML ImageView imageView;
  private @FXML Label name;
  private @FXML Label tags;
  private @FXML Label symbolClass;
  private @FXML Label category;
  private @FXML Label key;

  SymbolStyleSearchResult styleSymbolSearchResult;

  /**
   * Creates a view of a symbol with a picture and description.
   *
   * @param symbolResult symbol result from a symbol dictionary search
   */
  SymbolView(SymbolStyleSearchResult symbolResult) {
    styleSymbolSearchResult = symbolResult;

    // Set the view of this component to the fxml file
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/symbol_dictionary/symbol_dictionary_view.fxml"));
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
      Symbol symbol = styleSymbolSearchResult.getSymbol();
      ListenableFuture<Image> imageResult = symbol.createSwatchAsync(40, 40, 0x00FFFFFF, new Point(0, 0, 0));
      imageResult.addDoneListener(() -> {
        try {
          imageView.setImage(imageResult.get());
        } catch (ExecutionException | InterruptedException e) {
          e.printStackTrace();
        }
      });
    }
  }
}
