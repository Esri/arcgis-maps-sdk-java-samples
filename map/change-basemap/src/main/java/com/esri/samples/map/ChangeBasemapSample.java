/*
 * Copyright 2015 Esri. Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.esri.samples.map;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;

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

      // create a control panel
      VBox vBoxControl = new VBox(8);
      vBoxControl.setMaxSize(690, 80);

      FlowPane flowPane = new FlowPane();
      flowPane.setVgap(4);
      flowPane.setHgap(4);

      // setup all buttons to switch basemaps
      for (Basemap.Type type : Basemap.Type.values()) {
        if (type != Basemap.Type.UNKNOWN) {
          String basemapString = type.toString();

          Button button = new Button();
          button.setTooltip(new Tooltip(basemapString));
          button.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/" + basemapString.toLowerCase() +
              ".png"))));

          // listener to switch ArcGISMap types when button clicked
          button.setOnAction(e -> {
            if (map != null) {
              mapView.setMap(null);
            }
            map = new ArcGISMap(Basemap.Type.valueOf(basemapString), LATITUDE, LONGITUDE, LOD);
            mapView.setMap(map);
          });

          flowPane.getChildren().add(button);
        }
      }

      vBoxControl.getChildren().add(flowPane);

      // create ArcGISMap with topographic basemap
      map = new ArcGISMap(Basemap.Type.TOPOGRAPHIC, LATITUDE, LONGITUDE, LOD);

      // creates a map view and set map to it
      mapView = new MapView();
      mapView.setMap(map);

      // add the map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView, vBoxControl);
      StackPane.setAlignment(vBoxControl, Pos.TOP_CENTER);
      StackPane.setMargin(vBoxControl, new Insets(10, 0, 0, 10));

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
