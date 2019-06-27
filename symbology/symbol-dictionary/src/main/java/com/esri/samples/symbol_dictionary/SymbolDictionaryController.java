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

package com.esri.samples.symbology.symbol_dictionary;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
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
    dictionarySymbol = new DictionarySymbolStyle("mil2525d");
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
    SymbolStyleSearchParameters searchParameters = new SymbolStyleSearchParameters();
    searchParameters.getNames().add(nameField.getText());
    searchParameters.getTags().add(tagField.getText());
    searchParameters.getSymbolClasses().add(symbolClassField.getText());
    searchParameters.getCategories().add(categoryField.getText());
    searchParameters.getKeys().add(keyField.getText());

    // search for any matching symbols
    ListenableFuture<List<SymbolStyleSearchResult>> search = dictionarySymbol.searchSymbolsAsync(searchParameters);
    search.addDoneListener(() -> {
      try {
        // update the result list (triggering the listener)
        List<SymbolStyleSearchResult> searchResults = search.get();
        searchResultsFound.setText(String.valueOf(searchResults.size()));
        results.clear();
        results.addAll(searchResults);
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
    nameField.clear();
    tagField.clear();
    symbolClassField.clear();
    categoryField.clear();
    keyField.clear();
    results.clear();
    searchResultsFound.setText("");
  }
}
