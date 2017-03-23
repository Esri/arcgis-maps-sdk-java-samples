/*
 * Copyright 2016 Esri.
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

package com.esri.samples.map.change_basemap;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChangeBasemapSample extends Application {

  private MapView mapView;
  private ArcGISMap map;

  private static final double LATITUDE = 57.5000;
  private static final double LONGITUDE = -5.0000;
  private static final int LOD = 6;

  @Override
  public void start(Stage stage) throws Exception {

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

      // creates a map view
      mapView = new MapView();

      // setup listview of basemaps
      ListView<Basemap.Type> basemapList = new ListView<>();
      Arrays.stream(Basemap.Type.values()).collect(Collectors.toCollection(basemapList::getItems));
      basemapList.setMaxSize(250, 150);

      // change the basemap when list option is selected
      basemapList.getSelectionModel().selectedItemProperty().addListener(o -> {
        String basemapString = basemapList.getSelectionModel().getSelectedItem().toString();
        map = new ArcGISMap(Basemap.Type.valueOf(basemapString), LATITUDE, LONGITUDE, LOD);
        mapView.setMap(map);
      });

      // select the first basemap
      basemapList.getSelectionModel().selectFirst();

      // add the map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView, basemapList);
      StackPane.setAlignment(basemapList, Pos.TOP_LEFT);
      StackPane.setMargin(basemapList, new Insets(10, 0, 0, 10));

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() throws Exception {

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
