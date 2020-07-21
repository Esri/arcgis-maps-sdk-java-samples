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

package com.esri.samples.buffer_list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.LinearUnit;
import com.esri.arcgisruntime.geometry.LinearUnitId;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

public class BufferListSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Buffer List Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      SpatialReference statePlaneNorthCentralTexas = SpatialReference.create(32038);

      // show a box where the spatial reference used is valid for planar buffers
      List<Point> boundaryPoints = Arrays.asList(
          new Point(-103.070, 31.720, SpatialReferences.getWgs84()),
          new Point(-103.070, 34.580, SpatialReferences.getWgs84()),
          new Point(-94.000, 34.580, SpatialReferences.getWgs84()),
          new Point(-94.00, 31.720, SpatialReferences.getWgs84())
      );
      Polygon boundaryPolygon = (Polygon) GeometryEngine.project(new Polygon(new PointCollection(boundaryPoints)), statePlaneNorthCentralTexas);

      // create a map view
      mapView = new MapView();

      // create a map with a basemap and add it to the map view
      ArcGISMap map = new ArcGISMap(statePlaneNorthCentralTexas);
      mapView.setMap(map);

      // set an initial viewpoint
      map.setInitialViewpoint(new Viewpoint(boundaryPolygon.getExtent()));

      // create an image layer from a service URL (counties, cities, and highways)
      ArcGISMapImageLayer mapImageLayer = new ArcGISMapImageLayer("https://sampleserver6.arcgisonline.com/arcgis/rest/services/USA/MapServer");
      // add the image layer to the map's base layers
      map.getBasemap().getBaseLayers().add(mapImageLayer);

      // show alert if layer fails to load
      mapImageLayer.addDoneLoadingListener(() -> {
        if (mapImageLayer.getLoadStatus() != LoadStatus.LOADED) {
          new Alert(Alert.AlertType.ERROR, "Error loading ArcGIS Map Image Layer.").show();
        }
      });

      // create a graphics overlay to show the spatial reference's valid area
      GraphicsOverlay boundaryGraphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(boundaryGraphicsOverlay);
      SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH, 0xFFFF0000, 5);
      Graphic boundaryGraphic = new Graphic(boundaryPolygon, lineSymbol);
      boundaryGraphicsOverlay.getGraphics().add(boundaryGraphic);

      GraphicsOverlay bufferGraphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(bufferGraphicsOverlay);

      // create a white cross marker symbol to show where the user clicked
      final SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CROSS, 0xFFFFFFFF, 14);
      // create a semi-transparent
      final SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0x88FF00FF, new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFFFF0000, 3));

      // create a box to hold the input controls
      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY,
          Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(200, 150);
      controlsVBox.getStyleClass().add("panel-region");

      // create a spinner to set the buffer size (in miles)
      Spinner<Integer> distanceSpinner = new Spinner<>(0, 300, 100);
      distanceSpinner.setEditable(true);
      controlsVBox.getChildren().add(distanceSpinner);

      // set up units to convert from miles to meters
      final LinearUnit miles = new LinearUnit(LinearUnitId.MILES);
      final LinearUnit meters = new LinearUnit(LinearUnitId.METERS);

      // create a checkbox to choose whether to union the buffers into one geometry
      CheckBox unionCheckBox = new CheckBox("Union the buffers");
      controlsVBox.getChildren().add(unionCheckBox);

      // create a button to create the buffer(s)
      Button createButton = new Button("Create Buffer(s)");
      controlsVBox.getChildren().add(createButton);

      // create a button to clear the buffer(s)
      Button clearButton = new Button("Clear");
      controlsVBox.getChildren().add(clearButton);

      // when the user clicks the map, save the clicked location, along with the current distance value
      List<Geometry> geometries = new ArrayList<>();
      List<Double> distances = new ArrayList<>();
      mapView.setOnMouseClicked(e -> {
        if (e.isStillSincePress() && e.getButton() == MouseButton.PRIMARY) {
          Point2D point2D = new Point2D(e.getX(), e.getY());
          Point point = mapView.screenToLocation(point2D);
          if (GeometryEngine.contains(boundaryPolygon, point)) {
            geometries.add(point);
            double distance = miles.convertTo(meters, distanceSpinner.getValue());
            distances.add(distance);
            // add a marker where the user clicked
            Graphic clickedMarker = new Graphic(point, markerSymbol);
            bufferGraphicsOverlay.getGraphics().add(clickedMarker);
          } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Location is not valid to buffer using the defined spatial reference.");
            alert.initOwner(mapView.getScene().getWindow());
            alert.show();
          }
        }
      });

      // draw the buffer(s) when the button is clicked
      createButton.setOnAction(e -> {
        // if the buffers are unioned, only one polygon is returned
        if (!geometries.isEmpty() && !distances.isEmpty()) {
          List<Polygon> buffers = GeometryEngine.buffer(geometries, distances, unionCheckBox.isSelected());
          buffers.forEach(bufferGeometry -> {
            Graphic bufferGraphic = new Graphic(bufferGeometry, fillSymbol);
            bufferGraphicsOverlay.getGraphics().add(bufferGraphic);
          });
        }
      });

      clearButton.setOnAction(e -> {
        bufferGraphicsOverlay.getGraphics().clear();
        geometries.clear();
        distances.clear();
      });

      // add the map view to the stack pane
      stackPane.getChildren().addAll(mapView, controlsVBox);
      StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
      StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));
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
