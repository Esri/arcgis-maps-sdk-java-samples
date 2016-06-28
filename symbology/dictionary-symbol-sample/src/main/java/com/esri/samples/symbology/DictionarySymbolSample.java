/*
 * Copyright 2015 Esri. Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.esri.samples.symbology;

import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.StyleSymbolSearchParameters;
import com.esri.arcgisruntime.symbology.StyleSymbolSearchResult;
import com.esri.arcgisruntime.symbology.SymbolDictionary;

public class DictionarySymbolSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Dictionary Symbol Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a control panel
      VBox vBoxControl = new VBox(6);
      vBoxControl.setMaxSize(180, 200);
      vBoxControl.getStyleClass().add("panel-region");

      //DELETE
      //      List<String> dictionarySymbols = SymbolDictionary.getSpecificationTypes();
      //      System.out.println("Size: " + dictionarySymbols.size());
      //      for (String symbol : dictionarySymbols) {
      //        System.out.println("Type: " + symbol);
      //      }
      //DELETE

      //
      SymbolDictionary dictionarySymbol = new SymbolDictionary("mil2525d");
      dictionarySymbol.loadAsync();
      //      ListenableFuture<StyleSymbolSearchParameters> searchParameters =
      //          dictionarySymbol.getAllStyleSymbolSearchParametersAsync();

      //      List<String> symbolNames = dictionarySymbol.getSymbologyFieldNames();
      //      System.out.println("Size: " + symbolNames.size());
      //      for (String fieldName : symbolNames) {
      //        System.out.println("Symbol Name: " + fieldName);
      //      }

      //      List<String> textNames = dictionarySymbol.getTextFieldNames();
      //      System.out.println("Size: " + textNames.size());
      //      for (String fieldName : textNames) {
      //        System.out.println("Text Name: " + fieldName);
      //      }

      //      searchParameters.addDoneListener(() -> {
      //        try {
      //          List<String> searchNames = searchParameters.get().getNames();
      //          System.out.println("Size: " + searchNames.size());
      //          //                        for (int i = 50; i < 100; i++) {
      //          for (String name : searchNames) {
      //            //                          System.out.println("Search Name: " + searchNames.get(i));
      //            System.out.println("Search Name: " + name);
      //          }
      //        } catch (Exception ex) {
      //
      //        }
      //      });

      //FINDING A SYMBOL
      StyleSymbolSearchParameters params = new StyleSymbolSearchParameters();
      //      params.getKeys().add("110000");
      //      params.setKeysStrictMatch(false);
      //      params.getCategories().add("Sea Surface : Main Icon");
      //      params.setCategoriesStrictMatch(false);
      params.getTags().add("Missile");
      params.setTagsStrictMatch(false);
      //      params.getNames().add("Maritime Points : Acoustic Fix : Friend");
      //      params.getNames().add("Missile Range : Long Range (Air Missile)");
      //      params.setNamesStrictMatch(false);
      ListenableFuture<List<StyleSymbolSearchResult>> styleSymbols =
          dictionarySymbol.searchSymbolsAsync(params);
      List<StyleSymbolSearchResult> symbolsResult = styleSymbols.get();
      //      double x = 0;
      for (StyleSymbolSearchResult symbolResult : symbolsResult) {
        //        System.out.println("Name: " + symbolResult.getName());
        System.out.println("Tags: " + symbolResult.getTags());
        //        System.out.println("Symbol Class: " + symbolResult.getSymbolClass());
        //        System.out.println("Category: " + symbolResult.getCategory());
        //        System.out.println("Key: " + symbolResult.getKey());
        //        CimSymbol symbol = symbolResult.getSymbol();
        //        Point geometry = new Point(x, x, SpatialReference.create(4326));
        //        x += 5;
        //        Graphic gr = new Graphic(geometry, symbol);
        //        mapView.getGraphicsOverlays().get(0).getGraphics().add(gr);
      }
      //FINDING A SYMBOL

      // add the map view and control panel to stack pane
      stackPane.getChildren().addAll();
      //      StackPane.setAlignment(vBoxControl, Pos.TOP_LEFT);
      //      StackPane.setMargin(vBoxControl, new Insets(10, 0, 0, 10));
    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() throws Exception {
  }

  /**
   * Opens and runs application.
   *
   * @param args arguments passed to this application
   */
  public static void main(String[] args) {

    Application.launch(args);
  }

}
