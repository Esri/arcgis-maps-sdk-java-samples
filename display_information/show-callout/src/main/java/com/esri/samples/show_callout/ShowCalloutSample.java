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

package com.esri.samples.show_callout;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.MapView;

public class ShowCalloutSample extends Application {

  private MapView mapView;

  // callout show and hide animation duration
  private static final Duration DURATION = new Duration(500);

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Show Callout Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create ArcGISMap with imagery basemap
      ArcGISMap map = new ArcGISMap(Basemap.createStreets());

      // create a view and set map to it
      mapView = new MapView();
      mapView.setMap(map);

      // get the map view's callout
      Callout callout = mapView.getCallout();

      // click event to display the callout
      mapView.setOnMouseClicked(e -> {

        // check that the primary mouse button was clicked and user is not panning
        if (e.getButton() == MouseButton.PRIMARY && e.isStillSincePress()) {

          // create a point from where the user clicked
          Point2D point = new Point2D(e.getX(), e.getY());

          // create a map point from a point
          Point mapPoint = mapView.screenToLocation(point);

          // set the callout's details
          callout.setTitle("Location");
          callout.setDetail(String.format("x: %.2f, y: %.2f", mapPoint.getX(), mapPoint.getY()));

          // show the callout where the user clicked
          callout.showCalloutAt(mapPoint, DURATION);

          // dismiss the callout on secondary click
        } else if (e.getButton() == MouseButton.SECONDARY && e.isStillSincePress()) {
          callout.dismiss();
        }
      });

      // add map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView);

    } catch (Exception e) {
      // on any error, print the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

    // release resources when the application closes
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
