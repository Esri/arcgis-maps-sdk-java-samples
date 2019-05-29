/*
 * Copyright 2019 Esri.
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

package com.esri.samples.symbology.read_symbols_from_mobile_style_file;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.MultilayerPointSymbol;
import com.esri.arcgisruntime.symbology.MultilayerSymbol;
import com.esri.arcgisruntime.symbology.Symbol;
import com.esri.arcgisruntime.symbology.SymbolStyle;
import com.esri.arcgisruntime.symbology.SymbolStyleSearchParameters;
import com.esri.arcgisruntime.symbology.SymbolStyleSearchResult;

public class ReadSymbolsFromMobileStyleFileController {

  @FXML
  private MapView mapView = new MapView();
  private GraphicsOverlay graphicsOverlay;
  private MultilayerSymbol currentMultilayerSymbol;
  private SymbolStyle emojiStyle;
  private ArrayList<String> symbolKeys = new ArrayList<>();
  private String faceSymbolKey;
  private String hatSymbolKey;
  private String eyesSymbolKey;
  private String mouthSymbolKey;

  @FXML
  public void initialize() {

    // create a map
    ArcGISMap map = new ArcGISMap(Basemap.createTopographic());
    // add the map to the map view
    mapView.setMap(map);

    // create a graphics overlay and add it to the map
    graphicsOverlay = new GraphicsOverlay();
    mapView.getGraphicsOverlays().add(graphicsOverlay);

    symbolKeys = new ArrayList<>();


    loadSymbolsFromStyleFile();

    // listen to mouse clicks to add the desired multi layer symbol
    mapView.setOnMouseClicked( e -> {
      // convert clicked point to a map point
      Point mapPoint = mapView.screenToLocation(new Point2D(e.getX(), e.getY()));

      // create a new graphic with the point and symbol
      Graphic graphic = new Graphic(mapPoint, currentMultilayerSymbol);
      graphicsOverlay.getGraphics().add(graphic);

    });

  }

  /**
   * Loads the stylx file and searches for all symbols contained within. Put the resulting symbols into the GUI
   * based on their category (eyes, mouth, hat, face).
   */
  private void loadSymbolsFromStyleFile(){
    // create a SymbolStyle by passing the location of the .stylx file in the constructor
    emojiStyle = new SymbolStyle("./samples-data/stylx/emoji-mobile.stylx");
    emojiStyle.loadAsync();

    // add a listener to run when the SymbolStyle has loaded
    emojiStyle.addDoneLoadingListener(() -> {
      if (emojiStyle.getLoadStatus() == LoadStatus.FAILED_TO_LOAD){
        new Alert(Alert.AlertType.ERROR, "Error: could not load .stylx file. Details: "+ emojiStyle.getLoadError().getMessage()).show();
        return;
      }

      // load the default search parameters
      ListenableFuture<SymbolStyleSearchParameters> defaultSearchParametersFuture = emojiStyle.getDefaultSearchParametersAsync();
      defaultSearchParametersFuture.addDoneListener(()->{
        try {
          SymbolStyleSearchParameters defaultSearchParameters = defaultSearchParametersFuture.get();

          // use the default parameters to perform the search
          ListenableFuture<List<SymbolStyleSearchResult>> symbolStyleSearchResultFuture = emojiStyle.searchSymbolsAsync(defaultSearchParameters);
          symbolStyleSearchResultFuture.addDoneListener(()->{
            try {
              List<SymbolStyleSearchResult> symbolStyleSearchResults = symbolStyleSearchResultFuture.get();
              for (SymbolStyleSearchResult symbolStyleSearchResult : symbolStyleSearchResults) {
                switch (symbolStyleSearchResult.getCategory().toLowerCase()) {
                  case "eyes":
                    eyesSymbolKey = symbolStyleSearchResult.getKey();
                    break;
                  case "mouth":
                    mouthSymbolKey = symbolStyleSearchResult.getKey();
                    break;
                  case "hat":
                    hatSymbolKey = symbolStyleSearchResult.getKey();
                    break;
                  case "face":
                    faceSymbolKey = symbolStyleSearchResult.getKey();
                    break;
                }

              }
              symbolKeys.add(faceSymbolKey);
              symbolKeys.add(hatSymbolKey);
              symbolKeys.add(eyesSymbolKey);
              symbolKeys.add(mouthSymbolKey);
              createSwatchAsync();

            } catch (InterruptedException | ExecutionException e) {
              new Alert(Alert.AlertType.ERROR, "Error performing the symbol search"+ e.getMessage()).show();
            }
          });
        } catch (InterruptedException | ExecutionException e) {
          new Alert(Alert.AlertType.ERROR, "Error retrieving default search parameters for symbol search"+ e.getMessage()).show();
        }
      });
    });
  }


  /**
   * Create a new multilayer point symbol based on selected symbol keys, size, and color.
   */
  private void createSwatchAsync() {
    // create a future to preform the generation fo the multi layer symbol using the chosen keys
    ListenableFuture<Symbol> symbolFuture = emojiStyle.getSymbolAsync(symbolKeys);
    symbolFuture.addDoneListener(()->{
      try {
        // wait for the future to complete and get the result
        MultilayerPointSymbol faceSymbol = (MultilayerPointSymbol) symbolFuture.get();
        if (faceSymbol == null) {
          return;
        }

        currentMultilayerSymbol = faceSymbol;
      }
      catch (InterruptedException | ExecutionException e)  {

      }
    });
  }


  /**
   * Stops and releases all resources used in application.
   */
  public void terminate() {

    if (mapView != null) {
      mapView.dispose();
    }
  }
}
