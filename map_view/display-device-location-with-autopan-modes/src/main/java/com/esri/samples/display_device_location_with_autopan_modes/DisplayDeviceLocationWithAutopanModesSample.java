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

package com.esri.samples.display_device_location_with_autopan_modes;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.location.SimulatedLocationDataSource;
import com.esri.arcgisruntime.location.SimulationParameters;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;

import org.apache.commons.io.IOUtils;

public class DisplayDeviceLocationWithAutopanModesSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {

    try {
      // create the stack pane and the application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set a title, size, and add the scene to stage
      stage.setTitle("Display Device Location With Autopan Modes Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();
      scene.getStylesheets().add(getClass().getResource("/display_device_location_with_autopan_modes/style.css").toExternalForm());

      // create an ArcGISMap with the imagery basemap
      ArcGISMap map = new ArcGISMap(Basemap.createImagery());

      // create a map view and add the map to it
      mapView = new MapView();
      mapView.setMap(map);

      // create a combo box
      ComboBox<String> comboBox = new ComboBox<>();
      comboBox.setMaxWidth(Double.MAX_VALUE);
      comboBox.setDisable(true);
      // add the autopan modes to the combo box
      comboBox.getItems().addAll("Re-Center", "Navigation", "Compass", "Off");
      comboBox.setValue("Re-Center");

      // add a label
      Label autopanModeLabel = new Label("Choose an autopan mode:");
      // add a checkbox that toggles the visibility of the location display
      CheckBox checkBox = new CheckBox("Show device location");
      checkBox.setDisable(true);

      // access the json of the location points
      String polylineData = IOUtils.toString(getClass().getResourceAsStream(
        "/display_device_location_with_autopan_modes/polyline_data.json"), StandardCharsets.UTF_8);
      // create a polyline from the location points
      Polyline locations = (Polyline) Geometry.fromJson(polylineData, SpatialReferences.getWgs84());

      // create a simulated location data source
      SimulatedLocationDataSource simulatedLocationDataSource = new SimulatedLocationDataSource();
      // set the location of the simulated location data source with simulation parameters to set a consistent velocity
      simulatedLocationDataSource.setLocations(
        locations, new SimulationParameters(Calendar.getInstance(), 5.0, 0.0, 0.0));

      // configure the map view's location display to follow the simulated location data source
      LocationDisplay locationDisplay = mapView.getLocationDisplay();
      locationDisplay.setLocationDataSource(simulatedLocationDataSource);
      locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
      locationDisplay.setInitialZoomScale(1000);

      // enable the checkbox and combo box interactions when the map is loaded
      map.addDoneLoadingListener(() -> {
        if (map.getLoadStatus() == LoadStatus.LOADED) {
          checkBox.setDisable(false);
          checkBox.setSelected(true);
          comboBox.setDisable(false);

          // start the location display
          locationDisplay.startAsync();
        } else {
          new Alert(Alert.AlertType.ERROR, "Map failed to load: " + map.getLoadError().getCause().getMessage()).show();
        }
      });

      // control location display updates and visibility
      checkBox.setOnAction(event -> {
        if (checkBox.isSelected()) {
          // start receiving location updates and display the current location with a default round blue symbol
          locationDisplay.startAsync();

        } else {
          // stop receiving location updates and displaying location symbol
          locationDisplay.stop();
        }
        // toggle the combo box interactions
        comboBox.setDisable(!checkBox.isSelected());
      });

      // set the autopan mode of the location display based on the mode chosen from the combo box
      comboBox.getSelectionModel().selectedItemProperty().addListener(e -> {

        // set the scale that the map view will zoom to when the autopan mode is changed
        locationDisplay.setInitialZoomScale(1000);

        switch (comboBox.getSelectionModel().getSelectedItem()) {
          case "Off":
            locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.OFF);
            break;
          case "Re-Center":
            locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
            break;
          case "Navigation":
            locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.NAVIGATION);
            break;
          case "Compass":
            locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.COMPASS_NAVIGATION);
            break;
        }
      });

      mapView.setOnMouseClicked(event -> {
        if (event.getButton() == MouseButton.PRIMARY) {
          // if the user has panned away from the location display
          if (locationDisplay.getAutoPanMode() == LocationDisplay.AutoPanMode.OFF) {
            // set the combo box
            comboBox.setValue("Off");
          }
        }
      });

      // create a control panel
      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY,
        Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(180, 50);
      controlsVBox.getStyleClass().add("panel-region");
      // add the checkbox, label and combo box to the control panel
      controlsVBox.getChildren().addAll(checkBox, autopanModeLabel, comboBox);

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
