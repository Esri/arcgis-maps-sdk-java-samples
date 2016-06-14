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

package com.esri.samples.displayinformation;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.MapView;

public class ShowCallout extends Application {

  private MapView mapView;
  private SpatialReference spartialReference;

  // callout show and hide animation duration
  private static final Duration DURATION = new Duration(500);

  @Override
  public void start(Stage stage) throws Exception {

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
      ArcGISMap map = new ArcGISMap(Basemap.createImageryWithLabels());

      // create a view and set map to it
      mapView = new MapView();
      mapView.setMap(map);

      // create spatial reference for all points
      spartialReference = SpatialReferences.getWebMercator();
      // create point for starting location
      Point startPoint = new Point(-14093, 6711377, spartialReference);

      // set viewpoint of map view to starting point
      mapView.setViewpointCenterAsync(startPoint);

      // click event to display the callout
      mapView.setOnMouseClicked(e -> {
        // check that the primary mouse button was clicked and user is not
        // panning
        if (e.isStillSincePress() && e.getButton() == MouseButton.PRIMARY) {
          // create a point from where the user clicked
          Point2D point = new Point2D(e.getX(), e.getY());

          // create a map point from a point
          Point mapPoint = mapView.screenToLocation(point);

          // get the map view's callout
          Callout callout = mapView.getCallout();

          if (!callout.isVisible()) {
            // set the callout's details
            callout.setTitle("Location");
            callout.setDetail(String.format("x: %.2f, y: %.2f", mapPoint.getX(), mapPoint.getY()));

            // show the callout where the user clicked
            callout.showCalloutAt(mapPoint, DURATION);
          } else {
            // hide the callout
            callout.dismiss();
          }
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
  public void stop() throws Exception {

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
