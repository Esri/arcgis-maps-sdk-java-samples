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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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

//  private Text resultsFoundText;
//  private ScrollPane resultsPane;
//  private TextField[] searchFields;
  private VBox searchResultsBox;
  
  // injected elements from fxml
  @FXML private ScrollPane scrollPane;
  @FXML private TextField nameField;
  @FXML private TextField tagField;
  @FXML private TextField symbolClassField;
  @FXML private TextField categoryField;
  @FXML private TextField keyField;
  @FXML private Text resultsText;

  private SymbolDictionary dictionarySymbol;
  private StyleSymbolSearchParameters searchParameters;

  public void initialize() {
    Button button = new Button();
    // loads a specification for the symbol dictionary
    dictionarySymbol = new SymbolDictionary("mil2525d");
    dictionarySymbol.loadAsync();

    // parameters used to search for symbol dictionary
    searchParameters = new StyleSymbolSearchParameters();

    // creates container to hold all components
//    VBox windowContainer = new VBox();
//    windowContainer.getChildren().addAll(createSearchPane(), createResultsControl());
//    Scene scene = new Scene(windowContainer);
//    scene.getStylesheets().add(getClass().getResource("/SamplesTheme.css").toExternalForm());
  }

  /**
   * Creates a Pane that searches through the symbol dictionary and displays the number of results found.
   * <p>
   * A symbol dictionary can be searched using a name, tag, symbol class, category, or key.
   * 
   * @return a pane used for searching dictionary symbols
   */
  //  private Pane createSearchPane() {
  // container for all components in top pane
  //    GridPane searchPane = new GridPane();
  //    searchPane.setHgap(10);
  //    searchPane.setVgap(10);
  //    searchPane.setPadding(new Insets(25, 0, 25, 25));

  // creates five different ways to search through a symbol dictionary
  //    int row = 0;
  //    searchFields = new TextField[5];
  //    String[] labelNames = new String[] {
  //        "Name: ", "Tag: ", "Symbol Class: ", "Category: ", "Key: "
  //    };
  //    for (String labelName : labelNames) {
  //      Label nameLabel = new Label(labelName);
  //      searchFields[row] = new TextField();
  //      searchPane.add(nameLabel, 0, row);
  //      searchPane.add(searchFields[row], 1, row++);
  //    }

  // searches for any dictionary symbols that match text from text fields
  //    Button searchButton = new Button("Search for Symbols");
  //    searchButton.setMaxWidth(Double.MAX_VALUE);
  //    searchButton.setOnAction(this::handleSearchAction);
  //    searchPane.add(searchButton, 1, row);
  //
  //    // clears all of the search results
  //    Button clearButton = new Button("Clear");
  //    clearButton.setOnAction(this::handleClearAction);
  //    searchPane.add(clearButton, 2, row++);

  // displays number of search results
  //    Label resultsLabel = new Label("Result(s) Found: ");
  //    resultsFoundText = new Text();
  //    searchPane.add(resultsLabel, 0, row);
  //    searchPane.add(resultsFoundText, 1, row);

  //    return searchPane;
  //  }

  /**
   * Control that displays all search results in a scroll area.
   * 
   * @return control for displaying symbol dictionary search results
   */
  //  private Control createResultsControl() {
  //    resultsPane = new ScrollPane();
  //    resultsPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
  //    VBox.setVgrow(resultsPane, Priority.ALWAYS);
  //
  //    // displays search results
  //    searchResultsBox = new VBox(10);
  //
  //    return resultsPane;
  //  }

  /**
   * Searches through the symbol dictionary using the text from the search fields.
   * <p>
   * An image of the symbol(s) found is shown, along with its full name, tags, symbol class, category, and key.
   * 
   * @param event action from search button
   */
  @FXML
  private void handleSearchAction(ActionEvent event) {
    searchParameters.getNames().add(nameField.getText());
    searchParameters.getTags().add(tagField.getText());
    searchParameters.getSymbolClasses().add(symbolClassField.getText());
    searchParameters.getCategories().add(categoryField.getText());
    searchParameters.getKeys().add(keyField.getText());

    try {
      // searching for any matches
      ListenableFuture<List<StyleSymbolSearchResult>> searchResult =
          dictionarySymbol.searchSymbolsAsync(searchParameters);
      List<StyleSymbolSearchResult> symbolResults = searchResult.get();
      resultsText.setText("" + symbolResults.size());

      // reset result so duplicates are not added
      searchResultsBox = new VBox(10);
      // display each result found
//      symbolResults.forEach(symbolResult -> {
//        Text description = new Text("Name: " + symbolResult.getName()
//            + "\nTags: " + symbolResult.getTags()
//            + "\nSymbol Classe: " + symbolResult.getSymbolClass()
//            + "\nCategory: " + symbolResult.getCategory()
//            + "\nKey: " + symbolResult.getKey());

        ImageView symbolImage = new ImageView();
        symbolImage.setFitWidth(40);
        // Text symbol are not Cim Symbol
        if (true){//!symbolResult.getCategory().startsWith("Text")) {
          try {
            CimSymbol symbol = symbolResults.get(0).getSymbol();
            ListenableFuture<Image> imageResult = symbol.createSwatchAsync(40, 40, 100, 0x00FFFFFF, new Point(0, 0, 0));
            Image image = imageResult.get();
            symbolImage.setImage(image);
          } catch (ExecutionException ee) {
            System.out.println("Creating CimSymbol image was aborted!\n" + ee.getMessage());
          } catch (InterruptedException ie) {
            System.out.println("Thread interrupted while creating CimSymbol Image search!\n" + ie.getMessage());
          }
        }
        searchResultsBox.getChildren().add(new HBox(symbolImage));
//      });
        scrollPane.setContent(searchResultsBox);
    } catch (ExecutionException ee) {
      System.out.println("Dictionary Symbol search was aborted!\n" + ee.getMessage());
    } catch (InterruptedException ie) {
      System.out.println("Thread interrupted during Dictionary Symbol search!\n" + ie.getMessage());
    }

  }

  /**
   * Clears all search results from screen.
   * 
   * @param event action for clear button
   */
  @FXML
  private void handleClearAction(ActionEvent event) {
    // reset search parameters to empty
    searchParameters = new StyleSymbolSearchParameters();

    // clear search results
    searchResultsBox = new VBox(10);
//    resultsPane.setContent(searchResultsBox);

    resultsText.setText("");

    // clear all text in search fields
    nameField.clear();
    tagField.clear();
    symbolClassField.clear();
    categoryField.clear();
    keyField.clear();
  }
}
