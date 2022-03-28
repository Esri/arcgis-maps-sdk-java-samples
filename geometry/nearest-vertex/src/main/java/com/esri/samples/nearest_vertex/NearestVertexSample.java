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
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.ProximityResult;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import com.esri.arcgisruntime.symbology.ColorUtil;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

public class NearestVertexSample extends Application {

  private MapView mapView;
  // California zone 5 (ftUS) state plane coordinate system
  private final SpatialReference statePlaneCaliforniaZone5SpatialReference = SpatialReference.create(2229);

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/nearest_vertex/style.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Nearest Vertex Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a new feature layer from a new portal item 
      var portalItem = new PortalItem(
        new Portal("https://arcgisruntime.maps.arcgis.com", false), "99fd67933e754a1181cc755146be21ca");
      FeatureLayer usStatesGeneralizedLayer = new FeatureLayer(portalItem, 0);
      // create a new map using the California zone 5 spatial reference
      ArcGISMap map = new ArcGISMap(statePlaneCaliforniaZone5SpatialReference);
      // add the feature layer to the map's list of base layers
      map.getBasemap().getBaseLayers().add(usStatesGeneralizedLayer);

      // create a map view and set the map to it
      mapView = new MapView();
      mapView.setMap(map);

      // create a graphics overlay to show the polygon, clicked location, and nearest vertex/coordinate
      GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // construct a polygon from a point collection that uses the California zone 5 (ftUS) state plane coordinate system
      PointCollection polygonPoints = new PointCollection(statePlaneCaliforniaZone5SpatialReference);
      polygonPoints.addAll(Arrays.asList(
          new Point(6627416.41469281, 1804532.53233782),
          new Point(6669147.89779046, 2479145.16609522),
          new Point(7265673.02678292, 2484254.50442408),
          new Point(7676192.55880379, 2001458.66365744),
          new Point(7175695.94143837, 1840722.34474458)));

      // create a graphic for the polygon
      Polygon polygon = new Polygon(polygonPoints);
      SimpleLineSymbol polygonOutlineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, ColorUtil.colorToArgb(Color.LIMEGREEN), 2);
      SimpleFillSymbol polygonFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.FORWARD_DIAGONAL, ColorUtil.colorToArgb(Color.LIMEGREEN), polygonOutlineSymbol);
      Graphic polygonGraphic = new Graphic(polygon, polygonFillSymbol);
      graphicsOverlay.getGraphics().add(polygonGraphic);

      // create graphics for the clicked location, nearest coordinate, and nearest vertex markers
      SimpleMarkerSymbol clickedLocationSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.X, ColorUtil.colorToArgb(Color.DARKORANGE), 15);
      SimpleMarkerSymbol nearestCoordinateSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, ColorUtil.colorToArgb(Color.RED), 10);
      SimpleMarkerSymbol nearestVertexSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, ColorUtil.colorToArgb(Color.BLUE), 15);
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
          // create a point from where the user clicked
          Point2D point = new Point2D(e.getX(), e.getY());

          // create a map point from a point
          Point mapPoint = mapView.screenToLocation(point);
          
          // show where the user clicked
          clickedLocationGraphic.setGeometry(mapPoint);

          // show the nearest coordinate and vertex
          ProximityResult nearestCoordinateResult = GeometryEngine.nearestCoordinate(polygon, mapPoint);
          ProximityResult nearestVertexResult = GeometryEngine.nearestVertex(polygon, mapPoint);
          nearestVertexGraphic.setGeometry(nearestVertexResult.getCoordinate());
          nearestCoordinateGraphic.setGeometry(nearestCoordinateResult.getCoordinate());

          // show the distances to the nearest vertex and nearest coordinate, converted from feet to miles
          int vertexDistance = (int) (nearestVertexResult.getDistance() / 5280.0);
          int coordinateDistance = (int) (nearestCoordinateResult.getDistance() / 5280.0);
          distancesLabel.setText("Vertex distance: " + vertexDistance + " mi\nCoordinate distance: " + coordinateDistance + " mi");
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
