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

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.DictionaryRenderer;
import com.esri.arcgisruntime.symbology.DictionarySymbolStyle;

import java.io.File;

public class CustomDictionaryStyleSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {
    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/custom_dictionary_style/style.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Custom Dictionary Style Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a map with a streets basemap style
      ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_STREETS);

      // create a map view and set its map
      mapView = new MapView();
      mapView.setMap(map);

      // set the initial viewpoint to the Esri Redlands campus
      map.setInitialViewpoint(new Viewpoint(new Point(-1.304630524635E7, 4036698.1412000023, map.getSpatialReference()), 5000));

      // create the restaurants feature table from the feature service
      ServiceFeatureTable restaurantFeatureTable = new ServiceFeatureTable("https://services2.arcgis.com/ZQgQTuoyBrtmoGdP/arcgis/rest/services/Redlands_Restaurants/FeatureServer/0");

      // create the restaurants layer and add it to the map
      FeatureLayer restaurantLayer = new FeatureLayer(restaurantFeatureTable);
      map.getOperationalLayers().add(restaurantLayer);

      // open the custom style file
      String styleFilePath = new File(System.getProperty("data.dir"), "./samples-data/stylx/Restaurant.stylx").getAbsolutePath();
      DictionarySymbolStyle restaurantStyle = DictionarySymbolStyle.createFromFile(styleFilePath);

      // create a dictionary renderer
      DictionaryRenderer dictionaryRenderer = new DictionaryRenderer(restaurantStyle);

      // set the dictionary renderer to the restaurant layer
      restaurantLayer.setRenderer(dictionaryRenderer);

      // add the map view and control panel to stack pane
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
