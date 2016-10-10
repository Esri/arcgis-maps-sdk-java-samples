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
package com.esri.samples.symbology.symbol_dictionary;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.symbology.DictionarySymbolStyle;
import com.esri.arcgisruntime.symbology.StyleSymbolSearchParameters;
import com.esri.arcgisruntime.symbology.StyleSymbolSearchResult;

public class SymbolDictionaryController {

  // injected elements from fxml
  @FXML private ListView<SymbolView> resultList;
  @FXML private TextField nameField;
  @FXML private TextField tagField;
  @FXML private TextField symbolClassField;
  @FXML private TextField categoryField;
  @FXML private TextField keyField;
  @FXML private Text searchResultsFound;

  private DictionarySymbolStyle dictionarySymbol;
  private StyleSymbolSearchParameters searchParameters;

  /**
   * Initialize fields after FXML is loaded.
   */
  public void initialize() {
    // loads a specification for the symbol dictionary
    dictionarySymbol = new DictionarySymbolStyle("mil2525d");
    dictionarySymbol.loadAsync();
  }

  /**
   * Searches through the symbol dictionary using the text from the search fields.
   */
  @FXML
  private void handleSearchAction() {
    // clear previous results
    resultList.getItems().clear();

    // accessing text from all search fields
    searchParameters = new StyleSymbolSearchParameters();
    searchParameters.getNames().add(nameField.getText());
    searchParameters.getTags().add(tagField.getText());
    searchParameters.getSymbolClasses().add(symbolClassField.getText());
    searchParameters.getCategories().add(categoryField.getText());
    searchParameters.getKeys().add(keyField.getText());

    // search for any matches in dictionary
    ListenableFuture<List<StyleSymbolSearchResult>> searchResult = dictionarySymbol.searchSymbolsAsync(searchParameters);
    searchResult.addDoneListener(() -> {
      try {
        List<StyleSymbolSearchResult> symbolResults = searchResult.get();

        Platform.runLater(() -> {
          // create and add results to listview
          symbolResults.stream().map(SymbolView::new).collect(Collectors.toCollection(() -> resultList.getItems()));

          // show result count
          searchResultsFound.setText(String.valueOf(resultList.getItems().size()));
        });

      } catch (ExecutionException | InterruptedException e) {
        e.printStackTrace();
      }
    });
  }

  /**
   * Clears search results and any text in the search fields.
   */
  @FXML
  private void handleClearAction() {
    // clear search results
    resultList.getItems().clear();
    searchResultsFound.setText(String.valueOf(0));

    // clear all text from search fields
    nameField.clear();
    tagField.clear();
    symbolClassField.clear();
    categoryField.clear();
    keyField.clear();
  }
}
