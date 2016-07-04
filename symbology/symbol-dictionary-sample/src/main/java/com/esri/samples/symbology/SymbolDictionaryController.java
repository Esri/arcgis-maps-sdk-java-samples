/*
 * Copyright 2015 Esri.
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
package com.esri.samples.symbology;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.symbology.CimSymbol;
import com.esri.arcgisruntime.symbology.StyleSymbolSearchParameters;
import com.esri.arcgisruntime.symbology.StyleSymbolSearchResult;
import com.esri.arcgisruntime.symbology.SymbolDictionary;

public class SymbolDictionaryController {

  // injected elements from fxml
  @FXML private ScrollPane scrollPane;
  @FXML private TextField nameField;
  @FXML private TextField tagField;
  @FXML private TextField symbolClassField;
  @FXML private TextField categoryField;
  @FXML private TextField keyField;
  @FXML private Text searchResultsFound;
  @FXML private ProgressIndicator progress;

  private VBox searchResultsBox;

  private SymbolDictionary dictionarySymbol;
  private StyleSymbolSearchParameters searchParameters;

  /**
   * 
   */
  public void initialize() {
    // loads a specification for the symbol dictionary
    dictionarySymbol = new SymbolDictionary("mil2525d");
    dictionarySymbol.loadAsync();

    // parameters used to search for symbol dictionary
    searchParameters = new StyleSymbolSearchParameters();
  }

  /**
   * Searches through the symbol dictionary using the text from the search fields.
   * 
   * @param event action from search button
   */
  @FXML
  private void handleSearchAction(ActionEvent event) {
    // accessing text from all search fields 
    searchParameters.getNames().add(nameField.getText());
    searchParameters.getTags().add(tagField.getText());
    searchParameters.getSymbolClasses().add(symbolClassField.getText());
    searchParameters.getCategories().add(categoryField.getText());
    searchParameters.getKeys().add(keyField.getText());

    new Thread(() -> {
      try {
        progress.setVisible(true);
        // searching for any matches in dictionary
        ListenableFuture<List<StyleSymbolSearchResult>> searchResult =
            dictionarySymbol.searchSymbolsAsync(searchParameters);
        List<StyleSymbolSearchResult> symbolResults = searchResult.get();

        // displays number of results
        Platform.runLater(() -> searchResultsFound.setText("" + symbolResults.size()));
        //        searchResultsFound.setText("" + symbolResults.size());

        // reset result so duplicates are not added
        searchResultsBox = new VBox(10);
        if (symbolResults.size() > 0) {
          symbolResults.forEach(e -> {
            new Thread(() -> {
              displaySymbol(e);
            }).start();
          });
        } else {
          Text description = new Text("No Results Found");
          searchResultsBox.getChildren().add(description);
        }
        Platform.runLater(() -> scrollPane.setContent(searchResultsBox));
      } catch (ExecutionException ee) {
        System.out.println("Dictionary Symbol search was aborted!\n" + ee.getMessage());
      } catch (InterruptedException ie) {
        System.out.println("Thread interrupted during Dictionary Symbol search!\n" + ie.getMessage());
      } finally {
        progress.setVisible(false);
      }
    }).start();
  }

  /**
   * Displays an image of the symbol passed along with its full name, tags, symbol class, category, and key.
   * 
   * @param symbolResult symbol to be displayed
   */
  private void displaySymbol(StyleSymbolSearchResult symbolResult) {
    Text description = new Text("Name: " + symbolResult.getName()
        + "\nTags: " + symbolResult.getTags()
        + "\nSymbol Classe: " + symbolResult.getSymbolClass()
        + "\nCategory: " + symbolResult.getCategory()
        + "\nKey: " + symbolResult.getKey());

    ImageView symbolImage = new ImageView();
    symbolImage.setFitWidth(40);
    // Text symbols are not Cim Symbols
    if (!symbolResult.getCategory().startsWith("Text")) {
      try {
        CimSymbol symbol = symbolResult.getSymbol();
        ListenableFuture<Image> imageResult = symbol.createSwatchAsync(40, 40, 100, 0x00FFFFFF, new Point(0, 0, 0));
        Image image = imageResult.get();
        symbolImage.setImage(image);
      } catch (ExecutionException ee) {
        System.out.println("Creating CimSymbol image was aborted!\n" + ee.getMessage());
      } catch (InterruptedException ie) {
        System.out.println("Thread interrupted while creating CimSymbol Image search!\n" + ie.getMessage());
      }
    }
    Platform.runLater(() -> searchResultsBox.getChildren().add(new HBox(symbolImage, description)));
    //    searchResultsBox.getChildren().add(new HBox(symbolImage, description));
  }

  /**
   * Clears search results and any text in the search fields.
   * 
   * @param event action for clear button
   */
  @FXML
  private void handleClearAction(ActionEvent event) {
    // reset search parameters to empty
    searchParameters = new StyleSymbolSearchParameters();

    // clear search results
    searchResultsBox = new VBox(10);
    scrollPane.setContent(searchResultsBox);

    // clear number of results found
    searchResultsFound.setText("");

    // clear all text from search fields
    nameField.clear();
    tagField.clear();
    symbolClassField.clear();
    categoryField.clear();
    keyField.clear();
  }
}
