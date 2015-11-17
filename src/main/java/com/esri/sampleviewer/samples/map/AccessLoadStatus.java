/*
 * Copyright 2015 Esri.
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

package com.esri.sampleviewer.samples.map;

import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.loadable.Loadable;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.Viewpoint;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This sample demonstrates how to access the Maps' LoadStatus. How it works:
 * The load status is obtained from the {@link LoadStatus} property, which is
 * inherited from the {@link Loadable} interface.
 * 
 * <h4>Implementation Requirements</h4>
 * <p>
 * The loadStatus will be considered loaded when any of the following are true:
 * the map has a valid {@link SpatialReference}; the map has an an initial
 * {@link Viewpoint}; one of the map's predefined layers has been created.
 * 
 */
public class AccessLoadStatus extends Application {

  private MapView mapView;
  private Map map;

  @Override
  public void start(Stage stage) throws Exception {

    // create stack pane and application scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    scene.getStylesheets().add(getClass()
        .getResource("../resources/SamplesTheme.css").toExternalForm());

    // set title, size, and add scene to stage
    stage.setTitle("Load Status Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    // create a control panel
    VBox vBoxControl = new VBox(6);
    vBoxControl.setMaxSize(250, 290);
    vBoxControl.getStyleClass().add("panel-region");

    // create sample description
    Label descriptionLabel = new Label("Sample Description:");
    descriptionLabel.getStyleClass().add("panel-label");
    TextArea description = new TextArea(
        "This sample shows how to access the Map's load status. Click the reload button to begin.");
    description.setWrapText(true);
    description.autosize();
    description.setEditable(false);

    // create area to display load status text
    Label loadStatusLabel = new Label("Load Status");
    loadStatusLabel.getStyleClass().add("panel-label");
    TextArea loadStatusText = new TextArea();
    loadStatusText.setMaxHeight(200);
    description.setWrapText(true);
    description.autosize();
    description.setEditable(false);

    Button reloadMapButton = new Button("Reload Map");
    reloadMapButton.setMaxWidth(Double.MAX_VALUE);

    // reload map when button clicked
    reloadMapButton.setOnAction(event -> {
      loadStatusText.clear();

      // reload the same map
      map = new Map(Basemap.createImagery());

      map.addLoadStatusChangedListener(e -> {
        String loadingStatus;

        // check the loading status
        switch (e.getNewLoadStatus()) {
          case LOADING:
            loadingStatus = "Load Status: LOADING!";
            break;
          case FAILED_TO_LOAD:
            loadingStatus = "Load Status: FAILED TO LOAD!";
            break;
          case NOT_LOADED:
            loadingStatus = "Load Status: NOT LOADED!";
            break;
          case LOADED:
            loadingStatus = "Load Status: LOADED!";
            break;
          default:
            loadingStatus = "Load Status: ERROR!";
        }

        // update the load status text to the loading status that was fired
        Platform
            .runLater(() -> loadStatusText.appendText(loadingStatus + "\n"));
      });

      mapView.setMap(map);
    });

    // add labels, sample description, and button to the control panel
    vBoxControl.getChildren().addAll(descriptionLabel, description,
        loadStatusLabel, loadStatusText, reloadMapButton);

    try {
      // create map with the imagery basemap
      map = new Map(Basemap.createImagery());

      // create a view for this map and set map to it
      mapView = new MapView();
      mapView.setMap(map);

      // add the map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView, vBoxControl);
      StackPane.setAlignment(vBoxControl, Pos.TOP_LEFT);
      StackPane.setMargin(vBoxControl, new Insets(10, 0, 0, 10));

    } catch (Exception e) {
      // on any error, display the stack trace
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
