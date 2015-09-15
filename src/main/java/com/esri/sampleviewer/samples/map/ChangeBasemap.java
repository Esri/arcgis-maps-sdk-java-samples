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

import com.esri.arcgisruntime.mapping.BasemapType;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.MapView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

/**
 * This application shows how to create a ({@link Map}) using a
 * {@link BasemapType} instance, giving you the option to easily set a map type
 * (base layer), latitude and longitude around which to center the map, and zoom
 * level for the map. The BasemapType class is then used to switch the type of
 * basemap in the map on-the-fly.
 */
public class ChangeBasemap extends Application {

  private MapView mapView;
  private Map map;

  private static final double LATITUDE = 57.5000;
  private static final double LONGITUDE = -5.0000;
  private static final int LOD = 6;

  @Override
  public void start(Stage stage) throws Exception {

    // creates a border pane and application scene
    BorderPane borderPane = new BorderPane();
    Scene scene = new Scene(borderPane);

    // size the stage and add a title
    stage.setTitle("Change Basemap");
    stage.setWidth(700);
    stage.setHeight(800);
    stage.setScene(scene);
    stage.show();

    TilePane tilePane = new TilePane();
    tilePane.setHgap(8);
    tilePane.setAlignment(Pos.CENTER);
    borderPane.setTop(tilePane);

    for (BasemapType type : BasemapType.values()) {
      if (type != BasemapType.UNKNOWN) {
        String basemapString = type.toString();
        Button button = new Button();
        button.setTooltip(new Tooltip(basemapString));
        button.setGraphic(new ImageView(new Image(getClass()
            .getResourceAsStream("resources/" + basemapString + ".png"))));
        // listener to switch map types when button clicked
        button.setOnAction(e -> {
          if (map != null) {
            mapView.setMap(null);
            map.dispose();
          }
          map = new Map(BasemapType.valueOf(basemapString), LATITUDE,
              LONGITUDE, LOD);
          mapView.setMap(map);
        });

        tilePane.getChildren().add(button);
      }
    }

    // creates a Map which defines the layers of data to view
    try {
      // creates a new map: topographic map, centered at lat-lon 57.5N 5W
      // (Scotland), zoom level 6
      map = new Map(BasemapType.TOPOGRAPHIC, LATITUDE, LONGITUDE, LOD);

      // creates the MapView JavaFX control and assign its map
      mapView = new MapView();
      mapView.setMap(map);

      // adds the MapView
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
