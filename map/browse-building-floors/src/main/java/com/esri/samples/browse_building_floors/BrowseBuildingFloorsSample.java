/*
 * Copyright 2022 Esri.
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

package com.esri.samples.browse_building_floors;

import java.util.Objects;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.floor.FloorLevel;
import com.esri.arcgisruntime.mapping.floor.FloorManager;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class BrowseBuildingFloorsSample extends Application {

  private MapView mapView;
  private FloorManager floorManager;
  private Viewpoint initialViewpoint;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(
        "/browse_building_floors/style.css")).toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Browse Building Floors Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // set up the combobox UI for choosing which floor level to display
      ComboBox<FloorLevel> comboBox = new ComboBox<>();
      comboBox.setMaxWidth(150);
      // disable combobox interaction until the floor levels have loaded
      comboBox.setDisable(true);

      // create a portal item with a floor-aware web map
      var portalItem = new PortalItem(new Portal("https://www.arcgis.com/", false), "f133a698536f44c8884ad81f80b6cfc7");
      // create a map with the portal item
      ArcGISMap map = new ArcGISMap(portalItem);
      // create a map view and set the map to it
      mapView = new MapView();
      mapView.setMap(map);
      // add a done loading listener to the map
      map.addDoneLoadingListener(() -> {
        // check the map has loaded successfully and that the map is floor-aware)
        if (map.getLoadStatus() == LoadStatus.LOADED && map.getFloorDefinition() != null) {
          // get the initial view point of the map
          initialViewpoint = map.getInitialViewpoint();
          // get the floor manager from the map, and load it
          floorManager = map.getFloorManager();
          floorManager.addDoneLoadingListener(() -> {
            // check the floor manager has loaded and that it contains levels
            if (floorManager.getLoadStatus() == LoadStatus.LOADED && !floorManager.getLevels().isEmpty()) {
              // add each floor level to the combo box
              floorManager.getLevels().forEach(floorLevel -> comboBox.getItems().add(floorLevel));
              // select the first floor level in the building (Level 1)
              comboBox.getSelectionModel().select(0);
              comboBox.setDisable(false);
            }
          });
          floorManager.loadAsync();
        }
      });

      // set a string converter to the combobox to display the floor level's name
      comboBox.setConverter(new FloorLevelStringConverter());

      // when a floor level from the combobox is selected, set its visibility property to true and set the map view's
      // viewpoint back to the extent of Building L
      comboBox.getSelectionModel().selectedItemProperty().addListener(e -> {
        comboBox.getItems().forEach(item -> item.setVisible(false));
        comboBox.getSelectionModel().getSelectedItem().setVisible(true);
        mapView.setViewpoint(initialViewpoint);
      });

      // set up the UI
      var label = new Label("Choose floor level to display");
      var vBox = new VBox(6);
      vBox.getChildren().addAll(label, comboBox);
      vBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.5)"), CornerRadii.EMPTY,
        Insets.EMPTY)));
      vBox.setPadding(new Insets(10.0));
      vBox.setMaxSize(200, 50);
      vBox.getStyleClass().add("panel-region");

      // add the map view and UI to the stack pane
      stackPane.getChildren().addAll(mapView, vBox);
      StackPane.setMargin(vBox, new Insets(10, 0, 0, 10));
      StackPane.setAlignment(vBox, Pos.TOP_LEFT);

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Converts FloorLevel objects to strings to display floor level name on the combobox.
   */
  private static class FloorLevelStringConverter extends StringConverter<FloorLevel> {

    @Override
    public String toString(FloorLevel floorLevel) {
      return floorLevel != null ? floorLevel.getLongName() : "";
    }

    @Override
    public FloorLevel fromString(String fileName) {
      return null;
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
