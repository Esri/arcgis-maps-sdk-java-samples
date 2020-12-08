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

package com.esri.samples.change_basemap;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;

public class ChangeBasemapSample extends Application {

  private MapView mapView;
  private ArcGISMap map;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Change Basemap Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // creates a map view
      mapView = new MapView();

      // setup a list view of basemap styles
      ListView<BasemapStyle> basemapStyleListView = new ListView<>(FXCollections.observableArrayList(BasemapStyle.values()));
      basemapStyleListView.setMaxSize(250, 150);

      // change the basemap when a list option is selected
      basemapStyleListView.getSelectionModel().selectedItemProperty().addListener(o -> {
        BasemapStyle selectedBasemapStyle = basemapStyleListView.getSelectionModel().getSelectedItem();
        map = new ArcGISMap(selectedBasemapStyle);
        mapView.setMap(map);
        mapView.setViewpoint(new Viewpoint(57.5000, -5.0000, 10000000.0));
      });

      // select the first basemap style
      basemapStyleListView.getSelectionModel().selectFirst();

      // add the map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView, basemapStyleListView);
      StackPane.setAlignment(basemapStyleListView, Pos.TOP_LEFT);
      StackPane.setMargin(basemapStyleListView, new Insets(10, 0, 0, 10));

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
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
