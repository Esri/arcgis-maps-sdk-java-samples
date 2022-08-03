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
  private Geodatabase geodatabase;   // keep loadable in scope to avoid garbage collection

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

    // create a map with the topographic basemap style and set it to the map view
    ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC);
    mapView.setMap(map);

    // define and load a dictionary symbol style from a local style file
    File stylxFile = new File(System.getProperty("data.dir"), "./samples-data/stylx/mil2525d.stylx");
    var dictionarySymbolStyle = DictionarySymbolStyle.createFromFile(stylxFile.getAbsolutePath());
    dictionarySymbolStyle.loadAsync();

    // create a new geodatabase instance from the geodatabase stored at the local location
    File geodatabaseFile = new File(System.getProperty("data.dir"), "./samples-data/dictionary/militaryoverlay" +
      ".geodatabase");
    geodatabase = new Geodatabase(geodatabaseFile.getAbsolutePath());

    // check the geodatabase has loaded successfully
    geodatabase.addDoneLoadingListener(() -> {
      if (geodatabase.getLoadStatus() == LoadStatus.LOADED) {
        var mapOperationalLayersList = map.getOperationalLayers();
        var geodatabaseFeatureTablesList = geodatabase.getGeodatabaseFeatureTables();

        geodatabaseFeatureTablesList.forEach(table -> {
          // create a new feature layer from each geodatabase feature table and
          // add it to the map's list of operational layers
          mapOperationalLayersList.add(new FeatureLayer(table));
        });

        // check the map operational layers size matches that of the geodatabase's feature tables size
        if (!mapOperationalLayersList.isEmpty() && mapOperationalLayersList.size() == geodatabaseFeatureTablesList.size()) {

          // check that each layer has loaded correctly and if not display an error message
          mapOperationalLayersList.forEach(layer ->
            layer.addDoneLoadingListener(() -> {
              if (layer.getLoadStatus() == LoadStatus.LOADED) {
                
                // set the feature layer's minimum scale so that features no longer show after this scale
                layer.setMinScale(1000000);
                
                // set the dictionary renderer as the feature layer's renderer
                if (layer instanceof FeatureLayer) {
                  ((FeatureLayer) layer).setRenderer(new DictionaryRenderer(dictionarySymbolStyle));
                }
                
                // set the map view viewpoint
                mapView.setViewpointGeometryAsync(layer.getFullExtent());

              } else {
                new Alert(Alert.AlertType.ERROR,
                  "Feature layer failed to load: " + layer.getLoadError().getCause().getMessage()).show();
              }
            })
          );
          
        } else {
          new Alert(Alert.AlertType.ERROR, "Error: Map operational list size does not match geodatabase feature table list size").show();
        }
        
      } else {
        new Alert(Alert.AlertType.ERROR, "Geodatabase Failed to Load!").show();
      }
    });
    
    // load the geodatabase
    geodatabase.loadAsync();
    
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
