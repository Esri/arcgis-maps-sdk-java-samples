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

package com.esri.samples.geometry.buffer;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Spinner;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.LinearUnit;
import com.esri.arcgisruntime.geometry.LinearUnitId;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

public class BufferSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Buffer Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a map with a basemap and add it to the map view
      ArcGISMap map = new ArcGISMap(SpatialReferences.getWebMercator());
      map.setBasemap(Basemap.createTopographic());
      mapView = new MapView();
      mapView.setMap(map);

      Geometry startingEnvelope = new Envelope(-10863035.97, 3838021.34, -10744801.344, 3887145.299,
          SpatialReferences.getWebMercator());
      mapView.setViewpointGeometryAsync(startingEnvelope);

      // create a graphics overlay to contain the buffered geometry graphics
      GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // create a spinner to set the buffer size (in miles)
      Spinner<Integer> bufferSpinner = new Spinner<>(0, 100, 5);
      bufferSpinner.setEditable(true);

      // set up units to convert from miles to meters
      final LinearUnit miles = new LinearUnit(LinearUnitId.MILES);
      final LinearUnit meters = new LinearUnit(LinearUnitId.METERS);

      // create a semi-transparent green fill symbol for the buffer regions
      final SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0x8800FF00, new
          SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF057e15, 5));

      // create a buffer around the clicked location
      mapView.setOnMouseClicked(e -> {
        if (e.isStillSincePress() && e.getButton() == MouseButton.PRIMARY) {
          Point2D point2D = new Point2D(e.getX(), e.getY());
          // buffer around the clicked point
          Point point = mapView.screenToLocation(point2D);
          Polygon bufferGeometry = GeometryEngine.buffer(point, miles.convertTo(meters, bufferSpinner.getValue()));
          // show the buffered region as a green graphic
          Graphic bufferGraphic = new Graphic(bufferGeometry, fillSymbol);
          graphicsOverlay.getGraphics().add(bufferGraphic);
          // show a red marker where clicked
          Graphic markerGraphic = new Graphic(point, new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE,
              0xFFFF0000, 5));
          graphicsOverlay.getGraphics().add(markerGraphic);
        }
      });

      // add the map view to the stack pane
      stackPane.getChildren().addAll(mapView, bufferSpinner);
      StackPane.setAlignment(bufferSpinner, Pos.TOP_LEFT);
      StackPane.setMargin(bufferSpinner, new Insets(10, 0, 0, 10));
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
