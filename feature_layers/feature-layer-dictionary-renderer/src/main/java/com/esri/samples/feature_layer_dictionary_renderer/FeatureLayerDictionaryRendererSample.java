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

package com.esri.samples.feature_layer_dictionary_renderer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.data.Geodatabase;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.DictionaryRenderer;
import com.esri.arcgisruntime.symbology.DictionarySymbolStyle;

import java.io.File;

public class FeatureLayerDictionaryRendererSample extends Application {

  private MapView mapView;
  // keep loadables in scope to avoid garbage collection
  private Geodatabase geodatabase;
  private FeatureLayer featureLayer;

  @Override
  public void start(Stage stage) {

    mapView = new MapView();
    StackPane appWindow = new StackPane(mapView);
    Scene scene = new Scene(appWindow);

    // set title, size, and add scene to stage
    stage.setTitle("Feature Layer Dictionary Renderer Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    // authentication with an API key or named user is required to access basemaps and other location services
    String yourAPIKey = System.getProperty("apiKey");
    ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

    // create a map with a basemap style and set it to the map view
    ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC);
    mapView.setMap(map);

    // load geo-database from local location
    File geodatabaseFile = new File(System.getProperty("data.dir"), "./samples-data/dictionary/militaryoverlay" +
            ".geodatabase");
    geodatabase = new Geodatabase(geodatabaseFile.getAbsolutePath());
    geodatabase.loadAsync();

    // render tells layer what symbols to apply to what features
    File stylxFile = new File(System.getProperty("data.dir"), "./samples-data/stylx/mil2525d.stylx");
    DictionarySymbolStyle symbolDictionary = DictionarySymbolStyle.createFromFile(stylxFile.getAbsolutePath());
    symbolDictionary.loadAsync();

    geodatabase.addDoneLoadingListener(() -> {
      if (geodatabase.getLoadStatus() == LoadStatus.LOADED) {
        geodatabase.getGeodatabaseFeatureTables().forEach(table -> {
          // add each layer to map
          featureLayer = new FeatureLayer(table);
          featureLayer.loadAsync();
          // Features no longer show after this scale
          featureLayer.setMinScale(1000000);
          map.getOperationalLayers().add(featureLayer);

          // displays features from layer using mil2525d symbols
          DictionaryRenderer dictionaryRenderer = new DictionaryRenderer(symbolDictionary);
          featureLayer.setRenderer(dictionaryRenderer);

          featureLayer.addDoneLoadingListener(() -> {
            if (featureLayer.getLoadStatus() == LoadStatus.LOADED) {
              // initial viewpoint to encompass all graphics displayed on the map view 
              mapView.setViewpointGeometryAsync(featureLayer.getFullExtent());
            } else {
              Alert alert = new Alert(Alert.AlertType.ERROR, "Feature Layer Failed to Load!");
              alert.show();
            }
          });
        });
      } else {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Geodatabase Failed to Load!");
        alert.show();
      }
    });
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

    if (mapView != null) {
      mapView.dispose();
    }
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
