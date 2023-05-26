/*
 * Copyright 2017 Esri.
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

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import com.esri.arcgisruntime.symbology.DictionarySymbolStyle;
import com.esri.arcgisruntime.symbology.SymbolStyleSearchParameters;
import com.esri.arcgisruntime.symbology.SymbolStyleSearchResult;

public class SymbolDictionaryController {

  @FXML private TextField nameField;
  @FXML private TextField tagField;
  @FXML private TextField symbolClassField;
  @FXML private TextField categoryField;
  @FXML private TextField keyField;
  @FXML private Text searchResultsFound;
  @FXML private Pagination resultPages;

  private ObservableList<SymbolStyleSearchResult> results;
  private DictionarySymbolStyle dictionarySymbol;
  private static final int MAX_RESULTS_PER_PAGE = 20;

  public void initialize() {
    // loads a specification for the symbol dictionary
    var stylxFile = new File(System.getProperty("data.dir"), "./samples-data/stylx/mil2525d.stylx");
    dictionarySymbol = DictionarySymbolStyle.createFromFile(stylxFile.getAbsolutePath());
    dictionarySymbol.loadAsync();

    // initialize result list
    results = FXCollections.observableArrayList();

    // add listener to update pagination control when results change
    results.addListener((ListChangeListener<SymbolStyleSearchResult>) e -> {
      int resultSize = results.size();
      resultPages.setPageCount(resultSize / MAX_RESULTS_PER_PAGE + 1);
      resultPages.setCurrentPageIndex(0);
      resultPages.setPageFactory(pageIndex -> {
        ListView<SymbolView> resultsList = new ListView<>();
        int start = pageIndex * MAX_RESULTS_PER_PAGE;
        List<SymbolView> resultViews = results.subList(start, Math.min(start + MAX_RESULTS_PER_PAGE, results.size()))
          .stream()
          .map(SymbolView::new)
          .collect(Collectors.toList());
        resultsList.getItems().addAll(resultViews);
        return resultsList;
      });
    });
  }

  /**
   * Searches through the symbol dictionary using the text from the search fields.
   */
  @FXML
  private void handleSearchAction() {
    // get parameters from input fields
    var searchParameters = new SymbolStyleSearchParameters();
    searchParameters.getNames().add(nameField.getText());
    searchParameters.getTags().add(tagField.getText());
    searchParameters.getSymbolClasses().add(symbolClassField.getText());
    searchParameters.getCategories().add(categoryField.getText());
    searchParameters.getKeys().add(keyField.getText());

    // search for any matching symbols
    dictionarySymbol.searchSymbolsAsync(searchParameters).toCompletableFuture().whenComplete(
      (searchResults, ex) -> {
      if (ex == null) {
        // update the result list
        searchResultsFound.setText(String.valueOf(searchResults.size()));
        results.clear();
        results.addAll(searchResults);
      } else {
        // display an error if the symbol search completed with an exception
        new Alert(AlertType.ERROR, "Error searching symbol dictionary.").show();
      }
    });
  }

  /**
   * Clears search results and any text in the search fields.
   */
  @FXML
  private void handleClearAction() {
    nameField.clear();
    tagField.clear();
    symbolClassField.clear();
    categoryField.clear();
    keyField.clear();
    results.clear();
    searchResultsFound.setText("");
  }
}
