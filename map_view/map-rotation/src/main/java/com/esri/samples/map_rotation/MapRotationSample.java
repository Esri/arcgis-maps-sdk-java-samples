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
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.toolkit.Compass;

public class MapRotationSample extends Application {

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

      // create a map with the streets basemap style
      ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_STREETS);

      // create a map view and set the map to it
      mapView = new MapView();
      mapView.setMap(map);

      // create a compass to show the direction of north
      var compass = new Compass(mapView);

      // create labels for instructions and map rotation
      Label instructionsLabel = new Label("Press the A and D keys to rotate the map.");
      instructionsLabel.setTextFill(Color.WHITE);
      Label rotationLabel = new Label("Current rotation: ");
      rotationLabel.setTextFill(Color.WHITE);

      // update the rotation label when the map rotation property changes
      rotationLabel.textProperty().bind(Bindings.createStringBinding(()->
        "Current map rotation: " + Math.round(mapView.mapRotationProperty().get()) + "º", mapView.mapRotationProperty()));

      // create a starting viewpoint for the map view
      SpatialReference spatialReference = SpatialReferences.getWebMercator();
      Point pointBottomLeft = new Point(-13639984.0, 4537387.0, spatialReference);
      Point pointTopRight = new Point(-13606734.0, 4558866, spatialReference);
      Envelope envelope = new Envelope(pointBottomLeft, pointTopRight);
      // set the viewpoint with a rotation of 5 degrees (so that the sample loads with the compass visible)
      Viewpoint viewpoint = new Viewpoint(envelope, 5.0f);

      // set viewpoint to the map view
      mapView.setViewpointAsync(viewpoint);

      // create a vbox to add the labels
      VBox controlsVBox = new VBox();
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.5)"), CornerRadii.EMPTY,
        Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(250, 50);
      controlsVBox.getChildren().addAll(instructionsLabel, rotationLabel);

      // add the map view, slider, and compass to the stack pane
      stackPane.getChildren().addAll(mapView, controlsVBox, compass);
      StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
      StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));
      StackPane.setAlignment(compass, Pos.TOP_RIGHT);
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
