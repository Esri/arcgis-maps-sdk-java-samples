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

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.symbology.DictionarySymbolStyle;
import com.esri.arcgisruntime.symbology.SymbolStyleSearchParameters;
import com.esri.arcgisruntime.symbology.SymbolStyleSearchResult;

public class SymbolDictionaryController {

  // injected elements from fxml
  @FXML private TextField nameField;
  @FXML private TextField tagField;
  @FXML private TextField symbolClassField;
  @FXML private TextField categoryField;
  @FXML private TextField keyField;
  @FXML private Text searchResultsFound;
  @FXML private VBox displayPanel;

  private List<SymbolStyleSearchResult> symbolResults;
  private DictionarySymbolStyle dictionarySymbol;
  private SymbolStyleSearchParameters searchParameters;
  private static final double MAX_RESULTS_PER_PAGE = 100.0;
  private static final int SEARCH_RESULTS = 1;

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
    // if searched multiple times delete old search results
    if (displayPanel.getChildren().size() > 1) {
      displayPanel.getChildren().remove(SEARCH_RESULTS);
    }

    // accessing text from all search fields
    searchParameters = new SymbolStyleSearchParameters();
    searchParameters.getNames().add(nameField.getText());
    searchParameters.getTags().add(tagField.getText());
    searchParameters.getSymbolClasses().add(symbolClassField.getText());
    searchParameters.getCategories().add(categoryField.getText());
    searchParameters.getKeys().add(keyField.getText());

    // search for any matches in dictionary
    ListenableFuture<List<SymbolStyleSearchResult>> searchResult = dictionarySymbol.searchSymbolsAsync(searchParameters);
    searchResult.addDoneListener(() -> {
      try {
        symbolResults = searchResult.get();
        int searchResultSize = symbolResults.size();

        // only display search result if one or more items were found
        if (searchResultSize > 0) {
          // makes sure that at least one page will be created
          Pagination pagination = new Pagination((int) Math.ceil(symbolResults.size() / MAX_RESULTS_PER_PAGE), 0);
          pagination.setPageFactory(new Callback<Integer, Node>() {

            public ListView<SymbolView> call(Integer pageIndex) {
              ListView<SymbolView> listView = new ListView<>();
              double results = MAX_RESULTS_PER_PAGE;
              // if last page only show remaining results
              if (pagination.getPageCount() == (pageIndex + 1)) {
                results = searchResultSize % MAX_RESULTS_PER_PAGE;
              }

              // cycle through results and display to panel
              for (int i = 0; i < results; i++) {
                SymbolView box = new SymbolView(symbolResults.get((pageIndex + 1) * i));
                listView.getItems().add(box);
              }
              return listView;
            }
          });
          displayPanel.getChildren().add(pagination);
        }
        searchResultsFound.setText(String.valueOf(searchResultSize));

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
    displayPanel.getChildren().remove(SEARCH_RESULTS);
    searchResultsFound.setText(String.valueOf(0));

    // clear all text from search fields
    nameField.clear();
    tagField.clear();
    symbolClassField.clear();
    categoryField.clear();
    keyField.clear();
  }
}
