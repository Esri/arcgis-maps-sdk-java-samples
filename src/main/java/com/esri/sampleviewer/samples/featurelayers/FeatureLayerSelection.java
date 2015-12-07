/*
 * Copyright 2015 Esri.
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

package com.esri.sampleviewer.samples.featurelayers;

import java.util.concurrent.atomic.AtomicInteger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.datasource.FeatureQueryResult;
import com.esri.arcgisruntime.datasource.QueryParameters;
import com.esri.arcgisruntime.datasource.QueryParameters.SpatialRelationship;
import com.esri.arcgisruntime.datasource.arcgis.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.FeatureLayer.SelectionMode;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.Viewpoint;
import com.esri.arcgisruntime.symbology.RgbColor;

/**
 * This sample demonstrates how to select Features from a FeatureLayer.
 * <p>
 * A {@link FeatureLayer} allows us to work with Features from a table, like
 * displaying Features to Map.
 * <h4>How it Works</h4>
 * 
 * A {@link ServiceFeatureTable} is created from a URL and then a FeatureLayer
 * is created by supplying that table. The {@link FeatureLayer#selectFeatures}
 * method will select Features filtered by the {@link QueryParameters} that was
 * passed to it.
 * 
 * <p>
 * A ListenableFuture needs to be a class level field because it could get
 * garbage collected right after being set.
 */
public class FeatureLayerSelection extends Application {

  private MapView mapView;
  private ServiceFeatureTable featureTable;
  private FeatureLayer featureLayer;

  private ListenableFuture<FeatureQueryResult> queryResult;

  private static final String SERVICE_FEATURE_URL =
      "http://sampleserver6.arcgisonline.com/arcgis/rest/services/DamageAssessment/FeatureServer/0";
  private static final String SAMPLES_THEME_PATH =
      "../resources/SamplesTheme.css";

  @Override
  public void start(Stage stage) throws Exception {

    // create stack pane and application scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    scene.getStylesheets().add(getClass().getResource(SAMPLES_THEME_PATH)
        .toExternalForm());

    // set title, size, and add scene to stage
    stage.setTitle("Feature Layer Selection Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    // create a control panel
    VBox vBoxControl = new VBox(6);
    vBoxControl.setMaxSize(250, 190);
    vBoxControl.getStyleClass().add("panel-region");

    // create sample label and description
    Label descriptionLabel = new Label("Sample Description");
    descriptionLabel.getStyleClass().add("panel-label");
    TextArea description = new TextArea(
        "This sample demonstrates how to select Features from a Feature Layer. "
            + "Click a Feature to select it and any Features within a certain "
            + "distance.");
    description.setWrapText(true);
    description.autosize();
    description.setEditable(false);

    // add sample label and description to the control panel
    vBoxControl.getChildren().addAll(descriptionLabel, description);
    try {

      // create a map with the streets basemap
      final Map map = new Map(Basemap.createStreets());

      // set an initial viewpoint
      Point leftPoint = new Point(-1131596.019761, 3893114.069099,
          SpatialReferences.getWebMercator());
      Point rightPoint = new Point(3926705.982140, 7977912.461790,
          SpatialReferences.getWebMercator());

      map.setInitialViewpoint(new Viewpoint(new Envelope(leftPoint,
          rightPoint)));

      // create the service feature table
      featureTable = new ServiceFeatureTable(SERVICE_FEATURE_URL);

      // create the feature layer using the service feature table
      featureLayer = new FeatureLayer(featureTable);
      featureLayer.setSelectionColor(new RgbColor(0, 255, 255, 255)); // cyan, fully opaque
      featureLayer.setSelectionWidth(3);

      // add the layer to the map
      map.getOperationalLayers().add(featureLayer);

      // create a view for this map and set map to it
      mapView = new MapView();
      mapView.setMap(map);

      mapView.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
        if (e.getButton() == MouseButton.PRIMARY) {
          // create point from where user clicked
          Point2D point = new Point2D(e.getX(), e.getY());

          // create map point from point
          Point mapPoint = mapView.screenToLocation(point);

          // select feature that user clicked
          selectFeatures(mapPoint);
        }
      });

      // add the map view and control box to stack pane
      stackPane.getChildren().addAll(mapView, vBoxControl);
      StackPane.setAlignment(vBoxControl, Pos.TOP_LEFT);
      StackPane.setMargin(vBoxControl, new Insets(10, 0, 0, 10));
    } catch (Exception e) {
      // on any error, display exception
      e.printStackTrace();
    }
  }

  /**
   * Selects features around map point.
   * 
   * @param mapPoint x,y coordinate pair
   */
  private void selectFeatures(Point mapPoint) {

    // create a buffer for the mapPoint
    double distance = mapView.getUnitsPerPixel() * 10;
    Polygon pointBuffer = GeometryEngine.buffer(mapPoint, distance);

    // create a query from pointBuffer
    QueryParameters queryParams = new QueryParameters();
    queryParams.setGeometry(pointBuffer);
    queryParams.setSpatialRelationship(SpatialRelationship.WITHIN);
    queryParams.getOutFields().add("*");

    // select the features based on the query
    queryResult = featureLayer.selectFeatures(queryParams, SelectionMode.NEW);

    try {
      // get selected features from the result
      FeatureQueryResult selectedFeatures = queryResult.get();

      AtomicInteger numberOfFeatures = new AtomicInteger(0);
      selectedFeatures.forEach(feature -> numberOfFeatures.incrementAndGet());

    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   * 
   * @throws Exception if security manager doesn't allow JVM to exit with
   *           current status
   */
  @Override
  public void stop() throws Exception {

    // release resources when the application closes
    if (mapView != null) {
      mapView.dispose();
    }
    Platform.exit();
    System.exit(0);
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
