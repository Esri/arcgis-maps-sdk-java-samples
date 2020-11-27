/*
 * Copyright 2018 Esri.
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

package com.esri.samples.map_rotation;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.toolkit.Compass;

public class MapRotationSample extends Application {

  private ArcGISMap map; // keep loadable in scope to avoid garbage collection
  private MapView mapView;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Map Rotation Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a slider with a range of 360 units and start it half way
      Slider slider = new Slider(-180.0, 180.0, 0.0);
      slider.setMaxWidth(240.0);
      slider.setShowTickLabels(true);
      slider.setShowTickMarks(true);
      slider.setMajorTickUnit(90);
      slider.setDisable(true);

      // listen for the value in the slider to change
      slider.valueProperty().addListener(e -> {
        // rotate map view based on new value in slider
        mapView.setViewpointRotationAsync(slider.getValue());
      });

      // create a ArcGISMap with a basemap style
      map = new ArcGISMap(BasemapStyle.ARCGIS_STREETS);

      // enable slider when map view is done loading
      map.addDoneLoadingListener(() -> {
        if (map.getLoadStatus() == LoadStatus.LOADED) {
          slider.setDisable(false);
        } else {
          Alert alert = new Alert(Alert.AlertType.ERROR, "Map Failed to Load!");
          alert.show();
        }
      });

      // create a view and add an ArcGISMap to it
      mapView = new MapView();
      mapView.setMap(map);

      // create a compass to show the current heading when rotated
      Compass compass = new Compass(mapView);

      // clicking the compass sets the map's heading to 0.0
      // add a listener to reset the slider when this happens
      compass.setOnMouseClicked(e -> slider.setValue(0.0));

      // create a starting viewpoint for the map view
      SpatialReference spatialReference = SpatialReferences.getWebMercator();
      Point pointBottomLeft = new Point(-13639984.0, 4537387.0, spatialReference);
      Point pointTopRight = new Point(-13606734.0, 4558866, spatialReference);
      Envelope envelope = new Envelope(pointBottomLeft, pointTopRight);
      Viewpoint viewpoint = new Viewpoint(envelope, 5.0f);

      // set viewpoint to the map view
      mapView.setViewpointAsync(viewpoint);

      // add the map view, slider, and compass to the stack pane
      stackPane.getChildren().addAll(mapView, slider, compass);
      StackPane.setAlignment(slider, Pos.TOP_LEFT);
      StackPane.setAlignment(compass, Pos.TOP_RIGHT);
      StackPane.setMargin(slider, new Insets(10, 0, 0, 10));
      StackPane.setMargin(compass, new Insets(10, 10, 0, 0));
    } catch (Exception e) {
      // on any error, display the stack trace
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
