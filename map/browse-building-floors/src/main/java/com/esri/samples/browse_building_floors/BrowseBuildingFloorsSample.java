/*
 * Copyright 2017 Esri.
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

import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.floor.FloorLevel;
import com.esri.arcgisruntime.mapping.floor.FloorManager;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.MapView;
import javafx.util.StringConverter;

import java.util.List;
import java.util.Objects;

public class BrowseBuildingFloorsSample extends Application {

  private MapView mapView;
  private FloorManager floorManager;

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

      ComboBox<FloorLevel> comboBox = new ComboBox<>();
      comboBox.setDisable(true);

      // create a portal item with a floor-aware web map
      var portalItem = new PortalItem(new Portal("https://www.arcgis.com/", false), "f133a698536f44c8884ad81f80b6cfc7");
      // create a map with the portal item
      ArcGISMap map = new ArcGISMap(portalItem);

      // create a map view and set the map to it
      mapView = new MapView();
      mapView.setMap(map);

      map.addDoneLoadingListener(() -> {
        if (map.getLoadStatus() == LoadStatus.LOADED && map.getFloorDefinition() != null) {
          // get the floor manager from the map, and load it
          floorManager = map.getFloorManager();
          floorManager.addDoneLoadingListener(() -> {
            // check the floor manager has loaded and that it contains levels
            if (floorManager.getLoadStatus() == LoadStatus.LOADED && !floorManager.getLevels().isEmpty()) {
              // add each floor level to the combo box
              floorManager.getLevels().forEach(floorLevel -> comboBox.getItems().add(floorLevel));
              // check the floor levels have been added to the combobox before selecting the first floor and enabling
              // interaction
              if (comboBox.getItems().size() == floorManager.getLevels().size()) {
                // select the first floor level in the building (Level 1)
                comboBox.getSelectionModel().select(0);
                comboBox.setDisable(false);
              }
            }
          });
          floorManager.loadAsync();
        }
      });

      // set a string converter to the combobox to display the floor level's name
      comboBox.setConverter(new FloorLevelStringConverter());

      // when a floor level from the combobox is selected, set its visibility to true
      comboBox.getSelectionModel().selectedItemProperty().addListener(e -> {
        comboBox.getItems().forEach(item -> item.setVisible(false));
        comboBox.getSelectionModel().getSelectedItem().setVisible(true);
      });

      // add the map view to the stack pane
      var label = new Label("Choose Floor Level to Display");

      var vBox = new VBox(6);
      vBox.getChildren().addAll(label, comboBox);
      vBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY,
        Insets.EMPTY)));
      vBox.setPadding(new Insets(10.0));
      vBox.setMaxSize(260, 160);
      vBox.getStyleClass().add("panel-region");

      stackPane.getChildren().addAll(mapView, vBox);
      StackPane.setMargin(vBox, new Insets(10, 0, 0, 10));
      StackPane.setAlignment(vBox, Pos.TOP_LEFT);
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

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
