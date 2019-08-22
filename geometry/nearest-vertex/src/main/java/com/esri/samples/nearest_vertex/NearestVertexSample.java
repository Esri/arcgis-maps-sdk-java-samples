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

package com.esri.samples.nearest_vertex;

import java.util.Arrays;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.ProximityResult;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

public class NearestVertexSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Nearest Vertex Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a map with a basemap and add it to the map view
      ArcGISMap map = new ArcGISMap(Basemap.createTopographic());
      mapView = new MapView();
      mapView.setMap(map);

      // create a graphics overlay to show the polygon, clicked location, and nearest vertex/coordinate
      GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // create a graphic for the polygon
      PointCollection polygonPoints = new PointCollection(SpatialReferences.getWebMercator());
      polygonPoints.addAll(Arrays.asList(
          new Point(-5991501.677830, 5599295.131468),
          new Point(-6928550.398185, 2087936.739807),
          new Point(-3149463.800709, 1840803.011362),
          new Point(-1563689.043184, 3714900.452072),
          new Point(-3180355.516764, 5619889.608838)));
      Polygon polygon = new Polygon(polygonPoints);
      SimpleLineSymbol polygonOutlineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF00FF00, 2);
      SimpleFillSymbol polygonFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.FORWARD_DIAGONAL, 0xFF00FF00, polygonOutlineSymbol);
      Graphic polygonGraphic = new Graphic(polygon, polygonFillSymbol);
      graphicsOverlay.getGraphics().add(polygonGraphic);

      // create graphics for the clicked location, nearest coordinate, and nearest vertex markers
      SimpleMarkerSymbol clickedLocationSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.X, 0xFFFFA500, 15);
      SimpleMarkerSymbol nearestCoordinateSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, 0xFFFF0000, 10);
      SimpleMarkerSymbol nearestVertexSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0xFF0000FF, 15);
      Graphic clickedLocationGraphic = new Graphic();
      clickedLocationGraphic.setSymbol(clickedLocationSymbol);
      Graphic nearestCoordinateGraphic = new Graphic();
      nearestCoordinateGraphic.setSymbol(nearestCoordinateSymbol);
      Graphic nearestVertexGraphic = new Graphic();
      nearestVertexGraphic.setSymbol(nearestVertexSymbol);
      graphicsOverlay.getGraphics().addAll(Arrays.asList(clickedLocationGraphic, nearestCoordinateGraphic, nearestVertexGraphic));

      // create a label to show the distances between the nearest vertex and nearest coordinate to the clicked location
      Label distancesLabel = new Label("");
      distancesLabel.getStyleClass().add("panel-label");

      // create a black background for the label
      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY,
          Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(300, 50);
      controlsVBox.getStyleClass().add("panel-region");
      controlsVBox.getChildren().add(distancesLabel);
      // hide it until the label text is set
      controlsVBox.visibleProperty().bind(Bindings.createBooleanBinding(() -> !distancesLabel.getText().equals(""), distancesLabel.textProperty()));
      controlsVBox.managedProperty().bind(controlsVBox.visibleProperty());

      // get the nearest vertex and coordinate where the user clicks
      mapView.setOnMouseClicked(e -> {
        if (e.isStillSincePress() && e.getButton() == MouseButton.PRIMARY) {
          // show where the user clicked
          Point2D point2D = new Point2D(e.getX(), e.getY());
          Point point = mapView.screenToLocation(point2D);
          clickedLocationGraphic.setGeometry(point);

          // show the nearest coordinate and vertex
          ProximityResult nearestCoordinateResult = GeometryEngine.nearestCoordinate(polygon, point);
          ProximityResult nearestVertexResult = GeometryEngine.nearestVertex(polygon, point);
          nearestVertexGraphic.setGeometry(nearestVertexResult.getCoordinate());
          nearestCoordinateGraphic.setGeometry(nearestCoordinateResult.getCoordinate());

          // show the distances to the nearest vertex and nearest coordinate rounded to the nearest kilometer
          int vertexDistance = (int) (nearestVertexResult.getDistance() / 1000.0);
          int coordinateDistance = (int) (nearestCoordinateResult.getDistance() / 1000.0);
          distancesLabel.setText("Vertex distance: " + vertexDistance + " km\nCoordinate distance: " + coordinateDistance + " km");
        }
      });

      // zoom to the polygon's extent
      mapView.setViewpointGeometryAsync(polygon.getExtent(), 100);

      // add the map view and label to the stack pane
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
