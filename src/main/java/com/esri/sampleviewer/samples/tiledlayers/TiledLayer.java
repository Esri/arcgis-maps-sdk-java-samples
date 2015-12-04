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

package com.esri.sampleviewer.samples.tiledlayers;

import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.MapView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * The ArcGIS Tile Layer URL app is the most basic Map application for the
 * <a href="https://developers.arcgis.com/java/"> ArcGIS Runtime SDK for
 * Java </a> using Tiled Layer <@Basemap> from an ArcGIS Online service URL. It
 * shows how to inflate a <@MapView>, create a Tiled Layer from an ArcGIS Online
 * service URL and bind that to a Basemap. The Basemap is used to create a
 * <@Map> which is used inside of the MapView. By default, this map supports
 * basic zooming and panning operations.
 */
public class TiledLayer extends Application {

  private Map map;
  private MapView mapView;

  private final String WORLD_TOPO_SERVICE =
      "http://services.arcgisonline.com/arcgis/rest/services/World_Topo_Map/MapServer";

  @Override
  public void start(Stage stage) throws Exception {

    // create a border pane and application scene
    BorderPane borderPane = new BorderPane();
    Scene scene = new Scene(borderPane);

    // size the stage, add a title, and set scene to stage
    stage.setTitle("Tiled Layer From URL Example");
    stage.setHeight(700);
    stage.setWidth(800);
    stage.setScene(scene);
    stage.show();

    try {
      // create a view for this map
      mapView = new MapView();
      // create new Tiled Layer from service URL
      ArcGISTiledLayer tiledLayer = new ArcGISTiledLayer(WORLD_TOPO_SERVICE);
      // set Tiled Layer as basemap
      Basemap basemap = new Basemap(tiledLayer);
      // add basemap to map
      map = new Map(basemap);
      // set the map to be displayed in the view
      mapView.setMap(map);
      // place map in the center of the border pane
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

    // release resources when the application closes
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
