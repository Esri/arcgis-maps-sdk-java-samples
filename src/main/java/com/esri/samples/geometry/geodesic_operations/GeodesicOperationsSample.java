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

package com.esri.samples.geometry.geodesic_operations;

import java.util.Arrays;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.GeodeticCurveType;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.LinearUnit;
import com.esri.arcgisruntime.geometry.LinearUnitId;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

public class GeodesicOperationsSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene fxScene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Geodesic Operations Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(fxScene);
      stage.show();

      // create a map
      ArcGISMap map = new ArcGISMap(Basemap.createImagery());

      // set the map to a map view
      mapView = new MapView();
      mapView.setMap(map);

      // create a graphics overlay
      GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // add a graphic at JFK airport to represent the flight start location
      Point start = new Point(-73.7781, 40.6413, SpatialReferences.getWgs84());
      Graphic startLocation = new Graphic(start, new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0xFF0000FF,
          10));
      graphicsOverlay.getGraphics().add(startLocation);

      // create a graphic for the destination
      Graphic endLocation = new Graphic();
      endLocation.setSymbol(new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0xFF0000FF, 10));
      graphicsOverlay.getGraphics().add(endLocation);

      // create a graphic representing the geodesic path between the two locations
      Graphic path = new Graphic();
      path.setSymbol(new SimpleLineSymbol(SimpleLineSymbol.Style.DASH, 0xFF0000FF, 5));
      graphicsOverlay.getGraphics().add(path);

      // and a mouse click listener to get the user's input for the destination
      mapView.setOnMouseClicked(e -> {
        if (e.isStillSincePress() && e.getButton() == MouseButton.PRIMARY) {
          // change the end location's geometry to the clicked location
          Point2D point2D = new Point2D(e.getX(), e.getY());
          Point destination = (Point) GeometryEngine.project(mapView.screenToLocation(point2D), SpatialReferences
              .getWgs84());
          endLocation.setGeometry(destination);
          // create a straight line path between the start and end locations
          PointCollection points = new PointCollection(Arrays.asList(start, destination), SpatialReferences.getWgs84());
          Polyline line = new Polyline(points, SpatialReferences.getWgs84());
          // densify the path as a geodesic curve and show it with the path graphic
          Geometry pathGeometry = GeometryEngine.densifyGeodetic(line, 1, new LinearUnit(LinearUnitId.KILOMETERS),
              GeodeticCurveType.GEODESIC);
          path.setGeometry(pathGeometry);
        }
      });

      // add the scene view to the stack pane
      stackPane.getChildren().add(mapView);
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
