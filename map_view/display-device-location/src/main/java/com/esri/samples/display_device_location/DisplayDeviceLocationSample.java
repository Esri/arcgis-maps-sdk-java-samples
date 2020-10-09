/*
 * Copyright 2020 Esri.
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

package com.esri.samples.display_device_location;

import com.esri.arcgisruntime.loadable.LoadStatus;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.location.SimulatedLocationDataSource;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;

public class DisplayDeviceLocationSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Display Device Location Sample");
      stage.setWidth(800);
      stage.setHeight(600);
      stage.setScene(scene);
      stage.show();
      scene.getStylesheets().add(getClass().getResource("/display_device_location/style.css").toExternalForm());

      // create ArcGISMap with the imagery basemap
      ArcGISMap map = new ArcGISMap(Basemap.createImagery());

      // create a map view and set its map
      mapView = new MapView();
      mapView.setMap(map);

      // create combo box
      ComboBox<String> comboBox = new ComboBox<>();
      comboBox.setMaxWidth(Double.MAX_VALUE);
      comboBox.setDisable(true);
      // add the autopan modes to the combo box
      comboBox.getItems().addAll("Off", "Recenter", "Navigation", "Compass");
      comboBox.setValue("Off");

      // add a label
      Label autopanModeLabel = new Label("Choose an autopan mode:");
      // add a checkbox that toggles the visibility of the location symbol
      CheckBox checkbox = new CheckBox("Show device location");
      checkbox.setDisable(true);

      // show a background behind the label, checkbox and combo box
      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY,
        Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(180, 50);
      controlsVBox.getStyleClass().add("panel-region");
      controlsVBox.getChildren().addAll(checkbox, autopanModeLabel, comboBox);

      // create a simulated location data source
      SimulatedLocationDataSource simulatedLocationDataSource = new SimulatedLocationDataSource();

      // set the location of the simulated location data source
      simulatedLocationDataSource.setLocations(position);

      // configure the map view's location display to follow the simulated location data source
      LocationDisplay locationDisplay = mapView.getLocationDisplay();
      locationDisplay.setLocationDataSource(simulatedLocationDataSource);
      // toggle location display visibility on check
      checkbox.setOnAction(event -> {
        if (checkbox.isSelected()) {
          // enable to combo box
          comboBox.setDisable(false);

          // start the location display
          locationDisplay.startAsync();

          // set the autopan mode of the location display based on the mode chosen from the combo box
          comboBox.getSelectionModel().selectedItemProperty().addListener(e -> {
            switch (comboBox.getSelectionModel().getSelectedItem()) {
              case "Off":
                locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.OFF);
                break;
              case "Recenter":
                locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
                break;
              case "Navigation":
                locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.NAVIGATION);
                break;
              case "Compass":
                locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.COMPASS_NAVIGATION);
                break;
            }
            // set the map scale that the map view will zoom to when the autopan mode is changed
            locationDisplay.setInitialZoomScale(1000);
          });
        } else {
          // turn off the location display and disable to combo box when the checkbox is unchecked
          locationDisplay.stop();
          comboBox.setDisable(true);
        }
      });

      // enable the checkbox interactions when the map is loaded
      map.addDoneLoadingListener(() -> {
        if (map.getLoadStatus() == LoadStatus.LOADED) {
          checkbox.setDisable(false);
        } else {
          new Alert(Alert.AlertType.ERROR, "Map failed to load: " + map.getLoadError().getCause().getMessage()).show();
        }
      });

      // add the map view and control panel to the stack pane
      stackPane.getChildren().addAll(mapView, controlsVBox);
      StackPane.setAlignment(controlsVBox, Pos.TOP_RIGHT);
      StackPane.setMargin(controlsVBox, new Insets(10, 10, 0, 0));
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

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
