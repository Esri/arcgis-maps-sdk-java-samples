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

package com.esri.sampleviewer.samples.imagelayers;

import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.MapView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * The ArcGIS Map Image Layer from URL app is the most basic Map application for
 * the <a href="https://developers.arcgis.com/java/"> ArcGIS Runtime SDK for
 * Java </a> using an <@ArcGISMapImageLayer> operational layer from an ArcGIS
 * Online service URL. It shows how to inflate a <@MapView>, create a Map Image
 * Layer from an ArcGIS Online service URL and bind that to a <@Basemap>. The
 * <@Basemap> is used to create a <@Map> which is used inside of the <@MapView>.
 * By default, this map supports basic zooming and panning operations.
 */
public class MapImageLayer extends Application {

  private Map map;
  private MapView mapView;

  private final String WORLD_ELEVATION_SERVICE =
      "http://sampleserver5.arcgisonline.com/arcgis/rest/services/Elevation/WorldElevations/MapServer";

  @Override
  public void start(Stage stage) throws Exception {

    // create a border pane and application scene
    BorderPane borderPane = new BorderPane();
    Scene scene = new Scene(borderPane);

    // set size of stage, add title, and set scene to stage
    stage.setTitle("Map Image Layer From URL Example");
    stage.setWidth(700);
    stage.setHeight(800);
    stage.setScene(scene);
    stage.show();

    try {
      // create a view for our map
      mapView = new MapView();
      // create new Map Image Layer from service URL
      ArcGISMapImageLayer imageLayer = new ArcGISMapImageLayer(
          WORLD_ELEVATION_SERVICE);
      // set Map Image Layer as basemap
      Basemap basemap = new Basemap(imageLayer);
      // add basemap to map
      map = new Map(basemap);
      // set map to be displayed in view
      mapView.setMap(map);
      // place the map in the center of the border pane
      borderPane.setCenter(mapView);

    } catch (Exception e) {
      // on any error, display stack trace
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   * 
   * @throws Exception if security manager doesn't allow JVM to exit with
   *           current status
   */
  @Override
  public void stop() throws Exception {

    mapView.dispose();
    map.dispose();
    Platform.exit();
    System.exit(0);
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
