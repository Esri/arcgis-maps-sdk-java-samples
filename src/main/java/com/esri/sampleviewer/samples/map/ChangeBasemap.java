/*
 * Copyright 2015 Esri.
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

package com.esri.sampleviewer.samples.map;

import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.BasemapType;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.MapView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This sample demonstrates how to change the {@link Basemap} of a {@link Map}.
 * How it works: the values method allows one to get a collection of different
 * Basemaps. This collection is then cycled through to add functionality. When a
 * Basemap is selected, it is set to a new Map and that Map is set to the
 * {@link MapView}.
 */
public class ChangeBasemap extends Application {

  private MapView mapView;
  private Map map;

  private static final double LATITUDE = 57.5000;
  private static final double LONGITUDE = -5.0000;
  private static final int LOD = 6;

  @Override
  public void start(Stage stage) throws Exception {

    // create stack pane and application scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    scene.getStylesheets().add(getClass()
        .getResource("../resources/SamplesTheme.css").toExternalForm());

    // set title, size, and add scene to stage
    stage.setTitle("Change Basemap Sample");
    stage.setWidth(700);
    stage.setHeight(800);
    stage.setScene(scene);
    stage.show();

    // create a control panel
    VBox vBoxControl = new VBox(6);
    vBoxControl.setMaxSize(184, 420);
    vBoxControl.getStyleClass().add("panel-region");

    // create sample description
    Label descriptionLabel = new Label("Sample Description:");
    descriptionLabel.getStyleClass().add("panel-label");
    TextArea description =
        new TextArea("This sample shows how to change the Basemap of a Map.");
    description.setWrapText(true);
    description.autosize();
    description.setEditable(false);

    // add label and sample description to the control panel
    vBoxControl.getChildren().addAll(descriptionLabel, description);

    FlowPane flowPane = new FlowPane();
    flowPane.setVgap(4);
    flowPane.setHgap(4);

    // setup all buttons to switch basemaps
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
          map = new Map(BasemapType.valueOf(basemapString), LATITUDE, LONGITUDE,
              LOD);
          mapView.setMap(map);
        });

        flowPane.getChildren().add(button);
      }
    }

    vBoxControl.getChildren().add(flowPane);

    try {
      // create map with topograohic basemap
      map = new Map(BasemapType.TOPOGRAPHIC, LATITUDE, LONGITUDE, LOD);

      // creates a map view and set map to it
      mapView = new MapView();
      mapView.setMap(map);

      // add the map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView, vBoxControl);
      StackPane.setAlignment(vBoxControl, Pos.TOP_LEFT);
      StackPane.setMargin(vBoxControl, new Insets(10, 0, 0, 10));

    } catch (Exception e) {
      // on any error, display the stack trace.
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

    if (mapView != null) {
      mapView.dispose();
    }
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
