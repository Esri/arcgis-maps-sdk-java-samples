/* Copyright 2015 Esri.
 
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
    http://www.apache.org/licenses/LICENSE-2.0
 
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
limitations under the License.  */

package com.esri.sampleviewer.samples.map;

import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.MapView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * The Display Map app is the most basic Map app for the ArcGIS Runtime SDK for
 * Java. It shows how to inflate a MapView in the scene, create a Map with a
 * static Basemap.createImagery() and bind the Map to the MapView. By default,
 * this map supports basic zooming and panning operations.
 */
public class DisplayMap extends Application {

  private MapView mapView;
  private Map map;

  @Override
  public void start(Stage stage) throws Exception {

    // creates a border pane and application scene
    BorderPane borderPane = new BorderPane();
    Scene scene = new Scene(borderPane);

    // size the stage and add a title
    stage.setTitle("Display A Map");
    stage.setWidth(700);
    stage.setHeight(800);
    stage.setScene(scene);
    stage.show();

    try {
      // create a map with the a Basemap instance with an Imagery base layer
      map = new Map(Basemap.createImagery());

      // set the map to be displayed in this view
      mapView = new MapView();
      mapView.setMap(map);

      // add the MapView
      borderPane.setCenter(mapView);

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  @Override
  public void stop() throws Exception {

    // releases resources when the application closes
    mapView.dispose();
    map.dispose();
    Platform.exit();
    System.exit(0);
  }

  /**
   * Starting point of this application.
   * 
   * @args arguments to this application.
   */
  public static void main(String[] args) {

    Application.launch(args);
  }
}
