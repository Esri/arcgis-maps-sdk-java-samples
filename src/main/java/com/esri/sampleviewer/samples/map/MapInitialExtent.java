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

import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.Viewpoint;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * The Set Initial Map Area sample demonstrates how to start a {@link Map}
 * application with a defined initial area using a {@link Viewpoint} created
 * with an {@link Envelope} defining the initial area.
 */
public class MapInitialExtent extends Application {

  private MapView mapView;
  private Map map;

  private final String WORLD_TOPO_SERVICE =
      "http://services.arcgisonline.com/arcgis/rest/services/World_Topo_Map/MapServer";

  @Override
  public void start(Stage stage) throws Exception {

    // creates a border pane and application scene
    BorderPane borderPane = new BorderPane();
    Scene scene = new Scene(borderPane);

    // size the stage and add a title
    stage.setTitle("Set Initial Map Area");
    stage.setWidth(700);
    stage.setHeight(800);
    stage.setScene(scene);
    stage.show();

    try {
      // create a map view
      mapView = new MapView();
      // create new Tiled Layer from service url
      ArcGISTiledLayer topoBasemap = new ArcGISTiledLayer(WORLD_TOPO_SERVICE);
      // set tiled layer as basemap
      Basemap basemap = new Basemap(topoBasemap);
      // create a map with the basemap
      map = new Map(basemap);

      // create an initial extent envelope
      Envelope initialExtent = new Envelope(-12211308.778729, 4645116.003309,
          -12208257.879667, 4650542.535773, 0.0, 0.0, 0.0, 0.0,
          SpatialReferences.getWebMercator());

      // create a viewpoint from envelope
      Viewpoint viewPoint = new Viewpoint(initialExtent);
      // set initial map extent
      map.setInitialViewpoint(viewPoint);

      // set the map to be displayed in this view
      mapView.setMap(map);

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
