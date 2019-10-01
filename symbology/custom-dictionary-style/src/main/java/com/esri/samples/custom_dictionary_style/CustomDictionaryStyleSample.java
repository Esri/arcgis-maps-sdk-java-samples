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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
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
      scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

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

      // create a renderer without overrides
      DictionaryRenderer dictionaryRendererWithoutOverrides = new DictionaryRenderer(restaurantStyle);

      // create overrides for expected field names that are different in this dataset
      HashMap<String, String> styleToFieldMappingOverrides = new HashMap<>();
      styleToFieldMappingOverrides.put("healthgrade", "Inspection");
      styleToFieldMappingOverrides.put("rating", "MyRating");

      // create overrides for expected text field names (if any)
      HashMap<String, String> textFieldOverrides = new HashMap<>();
      textFieldOverrides.put("name", "Address");

      // set the text visibility configuration setting
      List<DictionarySymbolStyleConfiguration> configurations = restaurantStyle.getConfigurations();
      for (DictionarySymbolStyleConfiguration configuration : configurations) {
        if (configuration.getName().equals("text")) {
          configuration.setValue("ON");
        }
      }

      // create the dictionary renderer with the style file and the field overrides
      DictionaryRenderer dictionaryRendererWithOverrides = new DictionaryRenderer(restaurantStyle, styleToFieldMappingOverrides, textFieldOverrides);

      // set the map's initial extent to that of the restaurants layer
      restaurantLayer.loadAsync();
      restaurantLayer.addDoneLoadingListener(() -> {
        if (restaurantLayer.getLoadStatus() == LoadStatus.LOADED) {
          map.setInitialViewpoint(new Viewpoint((restaurantLayer.getFullExtent())));
          // apply the dictionary renderer without overrides to the layer
          restaurantLayer.setRenderer(dictionaryRendererWithoutOverrides);
        }
      });

      // create a checkbox for toggling the applied renderer
      CheckBox overridesCheckBox = new CheckBox("Use overrides");

      // set the checkbox to toggle the renderer that is applied to the restaurants layer
      overridesCheckBox.setOnAction(event -> {
        if (overridesCheckBox.isSelected()) {
          // apply the dictionary renderer with overrides to the layer
          restaurantLayer.setRenderer(dictionaryRendererWithOverrides);
        } else {
          // apply the dictionary renderer without overrides to the layer
          restaurantLayer.setRenderer(dictionaryRendererWithoutOverrides);
        }
      });

      // create a control panel
      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY,
              Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(130, 30);
      controlsVBox.getStyleClass().add("panel-region");

      // add the checkbox to the control panel
      controlsVBox.getChildren().add(overridesCheckBox);

      // add the map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView, controlsVBox);
      StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
      StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));

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
