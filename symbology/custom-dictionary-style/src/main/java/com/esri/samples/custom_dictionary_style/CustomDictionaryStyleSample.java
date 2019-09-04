/*
 * Copyright 2019 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.esri.samples.custom_dictionary_style;

import java.util.HashMap;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.DictionaryRenderer;
import com.esri.arcgisruntime.symbology.DictionarySymbolStyle;
import com.esri.arcgisruntime.symbology.DictionarySymbolStyleConfiguration;

public class CustomDictionaryStyleSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {
    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // create a map view
      mapView = new MapView();

      // set title, size, and add scene to stage
      stage.setTitle("Custom Dictionary Style Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // open the custom style file
      DictionarySymbolStyle restaurantStyle = DictionarySymbolStyle.createFromFile("./samples-data/stylx/Restaurant.stylx");

      // create a new map with a streets basemap and show it
      ArcGISMap map = new ArcGISMap(Basemap.createStreetsVector());
      mapView.setMap(map);

      // create the restaurants feature table from the feature service
      ServiceFeatureTable restaurantFeatureTable = new ServiceFeatureTable("https://services2.arcgis.com/ZQgQTuoyBrtmoGdP/arcgis/rest/services/Redlands_Restaurants/FeatureServer/0");

      // create the restaurants layer and add it to the map
      FeatureLayer restaurantLayer = new FeatureLayer(restaurantFeatureTable);
      map.getOperationalLayers().add(restaurantLayer);

      // set the map's initial extent to that of the restaurants layer
      restaurantLayer.loadAsync();
      restaurantLayer.addDoneLoadingListener(() ->
              map.setInitialViewpoint(new Viewpoint((restaurantLayer.getFullExtent()))));

      // create overrides for expected field names that are different in this dataset
      HashMap<String, String> styleToFieldMappingOverrides = new HashMap<>();
      styleToFieldMappingOverrides.put("style", "Style");
      styleToFieldMappingOverrides.put("price", "Price");
      styleToFieldMappingOverrides.put("healthgrade", "Inspection");
      styleToFieldMappingOverrides.put("rating", "Rating");

      // create overrides for expected text field names (if any)
      HashMap<String, String> textFieldOverrides = new HashMap<>();
      textFieldOverrides.put("name", "Name");

      // set the text visibility configuration setting
      List<DictionarySymbolStyleConfiguration> configurations = restaurantStyle.getConfigurations();
      for (DictionarySymbolStyleConfiguration configuration : configurations) {
        if (configuration.getName().equals("text")) {
          configuration.setValue("ON");
        }
      }

      // create the dictionary renderer with the style file and the field overrides
      DictionaryRenderer dictionaryRenderer = new DictionaryRenderer(restaurantStyle, styleToFieldMappingOverrides, textFieldOverrides);

      // apply the dictionary renderer to the layer
      restaurantLayer.setRenderer(dictionaryRenderer);

      // add the map view to the stack pane
      stackPane.getChildren().add(mapView);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

    // release resources when the application closes
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
