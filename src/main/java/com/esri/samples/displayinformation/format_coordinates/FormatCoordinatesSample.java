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

package com.esri.samples.displayinformation.format_coordinates;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.esri.arcgisruntime.ArcGISRuntimeException;
import com.esri.arcgisruntime.geometry.CoordinateFormatter;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.MapView;

public class FormatCoordinatesSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Format Coordinates Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a map with a wgs84 basemap and set it to the map view
      mapView = new MapView();
      ArcGISTiledLayer basemapLayer = new ArcGISTiledLayer("https://wi.maptiles.arcgis" +
          ".com/arcgis/rest/services/World_Imagery/MapServer");
      Basemap basemap = new Basemap(basemapLayer);
      ArcGISMap map = new ArcGISMap(basemap);
      mapView.setMap(map);

      // click event to display the callout with the formatted coordinates of the clicked location
      mapView.setOnMouseClicked(e -> {
        // check that the primary mouse button was clicked and user is not panning
        if (e.isStillSincePress() && e.getButton() == MouseButton.PRIMARY) {
          // get the map point where the user clicked
          Point2D point = new Point2D(e.getX(), e.getY());
          Point mapPoint = mapView.screenToLocation(point);
          // show the callout at the point with the different coordinate format strings
          showCalloutWithLocationCoordinates(mapPoint);
        }
      });

      // create text field to input user's own coordinate string
      TextField input = new TextField();
      input.setMaxWidth(300);
      input.setPromptText("Input a coordinate string (LatLon, UTM, or USNG)");
      input.setOnAction(e -> {
        String inputText = input.getText();
        if (!"".equals(inputText)) {
          // try each coordinate format converter, use the first one with the correct format
          try {
            Point point = CoordinateFormatter.fromLatitudeLongitude(inputText, map.getSpatialReference());
            showCalloutWithLocationCoordinates(point);
            return;
          } catch (ArcGISRuntimeException ex) {
            //ignore
          }
          try {
            Point point = CoordinateFormatter.fromUtm(inputText, map.getSpatialReference(), CoordinateFormatter.UtmConversionMode.LATITUDE_BAND_INDICATORS);
            showCalloutWithLocationCoordinates(point);
            return;
          } catch (ArcGISRuntimeException ex) {
            //ignore
          }
          try {
            Point point = CoordinateFormatter.fromUsng(inputText, map.getSpatialReference());
            showCalloutWithLocationCoordinates(point);
          } catch (ArcGISRuntimeException ex) {
            //ignore
          }
        }
      });

      // add map view and text field to stack pane
      stackPane.getChildren().addAll(mapView, input);
      StackPane.setAlignment(input, Pos.TOP_LEFT);
      StackPane.setMargin(input, new Insets(10, 0, 0, 10));
    } catch (Exception e) {
      // on any error, print the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Shows a callout at the specified location with different coordinate formats in the callout.
   *
   * @param location coordinate to show coordinate formats for
   */
  private void showCalloutWithLocationCoordinates(Point location) {
    Callout callout = mapView.getCallout();
    callout.setTitle("Location:");
    String latLonDecimalDegrees = CoordinateFormatter.toLatitudeLongitude(location, CoordinateFormatter
        .LatitudeLongitudeFormat.DECIMAL_DEGREES, 4);
    String latLonDegMinSec = CoordinateFormatter.toLatitudeLongitude(location, CoordinateFormatter
        .LatitudeLongitudeFormat.DEGREES_MINUTES_SECONDS, 1);
    String utm = CoordinateFormatter.toUtm(location, CoordinateFormatter.UtmConversionMode.LATITUDE_BAND_INDICATORS,
        true);
    String usng = CoordinateFormatter.toUsng(location, 4, true);
    callout.setDetail(
        "Decimal Degrees: " + latLonDecimalDegrees + "\n" +
            "Degrees, Minutes, Seconds: " + latLonDegMinSec + "\n" +
            "UTM: " + utm + "\n" +
            "USNG: " + usng + "\n"
    );
    mapView.getCallout().showCalloutAt(location, new Duration(500));
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
