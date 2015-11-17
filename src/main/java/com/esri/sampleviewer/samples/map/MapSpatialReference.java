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

import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
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
 * The Set Map Spatial Reference sample application demonstrates how to create a
 * Map with the <b>WORLD_BONNE<\b> equal-area projection that has a true scale
 * along the central meridian and all parallels. The sample creates an
 * {@link ArcGISMapImageLayer} that can reproject itself to the {@link Map}'s
 * spatial reference.
 * <p>
 * Note: Not all layer types can be reprojected, like {@link ArcGISTiledLayer},
 * and will fail to draw if their spatial reference is not the same as the Map's
 * spatial reference.
 */
public class MapSpatialReference extends Application {

  private MapView mapView;
  private Map map;

  private static final String WORLD_CITIES_SERVICE =
      "http://sampleserver6.arcgisonline.com/arcgis/rest/services/SampleWorldCities/MapServer";

  @Override
  public void start(Stage stage) throws Exception {

    // creates a border pane and application scene
    BorderPane borderPane = new BorderPane();
    Scene scene = new Scene(borderPane);

    // size the stage and add a title
    stage.setTitle("Set initial Spatial Reference");
    stage.setWidth(700);
    stage.setHeight(800);
    stage.setScene(scene);
    stage.show();

    // creates a Map which defines the layers of data to view
    try {
      // create MapView
      mapView = new MapView();
      // create a map with World_Bonne projection
      map = new Map(SpatialReference.create(54024));
      // Adding a map image layer which can reproject itself to the map's
      // spatial reference
      ArcGISMapImageLayer mapImageLayer =
          new ArcGISMapImageLayer(WORLD_CITIES_SERVICE);
      // set the map image layer as basemap
      Basemap basemap = new Basemap(mapImageLayer);
      // add the basemap to the map
      map.setBasemap(basemap);
      // set the map to be displayed in this view
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
