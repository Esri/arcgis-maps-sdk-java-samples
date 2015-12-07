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

package com.esri.sampleviewer.samples.editing;

import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.datasource.Feature;
import com.esri.arcgisruntime.datasource.FeatureQueryResult;
import com.esri.arcgisruntime.datasource.QueryParameters;
import com.esri.arcgisruntime.datasource.QueryParameters.SpatialRelationship;
import com.esri.arcgisruntime.datasource.arcgis.FeatureEditResult;
import com.esri.arcgisruntime.datasource.arcgis.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.FeatureLayer.SelectionMode;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.Viewpoint;

/**
 * This sample demonstrates how to update the Geometry of a Feature from a
 * ServiceFeatureTable.
 * <h4>How it Works</h4>
 * 
 * A {@link ServiceFeatureTable} is created from a URL which stores
 * {@link Feature}s that are associated with that URL. To interact with those
 * Features a {@link FeatureLayer} needs to be created from that table. A
 * Feature is then selected using the {@link FeatureLayer#selectFeatures} method
 * and the {@link Geometry} can be changed by passing the user's new
 * {@link Point} location to {@link Feature#setGeometry}. Lastly all that needs
 * to be done is to pass that updated Feature to
 * {@link ServiceFeatureTable#updateFeatureAsync} for the changes to take
 * effect.
 * <p>
 * By default a thousand features are fetched for a visible extent, so zoomed in
 * to produce the best results.
 * <p>
 * Also ListenableFuture needs to be a class level field because it could get
 * garbage collected right after being set.
 */
public class UpdateGeometries extends Application {

  private MapView mapView;
  private FeatureLayer featureLayer;
  private FeatureQueryResult selectedFeatures;

  private ListenableFuture<FeatureQueryResult> queryResult;
  private ListenableFuture<Boolean> featureTableResult;
  private ListenableFuture<List<FeatureEditResult>> serverResult;

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
    stage.setTitle("Update Geometrys Sample");
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
    TextArea description = new TextArea("This sample shows how to update the "
        + "Geometry of a Feature. Click on a Feature to select it, then click "
        + "on the Map to move the Feature to a new location.");
    description.setWrapText(true);
    description.autosize();
    description.setEditable(false);

    // add sample label and description to the control panel
    vBoxControl.getChildren().addAll(descriptionLabel, description);
    try {

      // create a map with streets basemap
      final Map map = new Map(Basemap.createStreets());

      // create starting viewpoint for that map
      final SpatialReference spatialReference = SpatialReferences
          .getWebMercator();
      Point startPoint = new Point(-036773, 6710477, spatialReference);
      Viewpoint viewpoint = new Viewpoint(startPoint, 200000); // point and scale

      // set viewpoint for map
      map.setInitialViewpoint(viewpoint);

      // create service feature table from URL
      final ServiceFeatureTable featureTable = new ServiceFeatureTable(
          SERVICE_FEATURE_URL);
      featureTable.getOutFields().add("*"); // * gets all fields from the table

      // create a feature layer from table
      featureLayer = new FeatureLayer(featureTable);

      // add the layer to the map
      map.getOperationalLayers().add(featureLayer);

      // create a view and set map to it
      mapView = new MapView();
      mapView.setMap(map);

      mapView.setOnMouseClicked(e -> {
        if (e.getButton() == MouseButton.PRIMARY) {
          // create point from where user clicked
          Point2D point = new Point2D(e.getX(), e.getY());

          // create map point from point
          Point mapPoint = mapView.screenToLocation(point);

          // no feature selected
          if (selectedFeatures == null) {
            // select feature that the user clicked
            selectFeature(mapPoint);
          } else {
            // move feature to new location
            updateGeometry(mapPoint, featureTable);
            // clear the selection
            featureLayer.clearSelection();
            // set selectedFeatures to null
            selectedFeatures = null;
          }

        } else if (e.getButton() == MouseButton.SECONDARY) {
          // deselect the feature if selected
          if (selectedFeatures != null) {
            featureLayer.clearSelection();
            selectedFeatures = null;
          }
        }
      });

      // add the map view and control box to stack pane
      stackPane.getChildren().addAll(mapView, vBoxControl);
      StackPane.setAlignment(vBoxControl, Pos.TOP_LEFT);
      StackPane.setMargin(vBoxControl, new Insets(10, 0, 0, 10));
    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Selects Features around map point.
   * 
   * @param mapPoint x,y coordinate pair
   */
  private void selectFeature(Point mapPoint) {

    // get unit per pixel times ten
    double distance = mapView.getUnitsPerPixel() * 10;
    // create a buffer for the mapPoint
    Polygon pointBuffer = GeometryEngine.buffer(mapPoint, distance);

    // create a query from pointBuffer
    QueryParameters queryParams = new QueryParameters();
    queryParams.setGeometry(pointBuffer);
    queryParams.setSpatialRelationship(SpatialRelationship.WITHIN);
    queryParams.getOutFields().add("*");

    // select based on the query
    queryResult = featureLayer.selectFeatures(queryParams, SelectionMode.NEW);

    try {
      // get user's selection
      selectedFeatures = queryResult.get();
    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Updates the Geometry of the Features that are selected.
   * 
   * @param newPoint x,y coordinate pair
   * @param featureTable holds all Feature data
   */
  private void updateGeometry(Point newPoint,
      ServiceFeatureTable featureTable) {

    selectedFeatures.forEach(feature -> {
      // set x,y coordinate pair from feature to newPoint
      feature.setGeometry(newPoint);

      // update the feature to display on map view
      featureTableResult = featureTable.updateFeatureAsync(feature);

      try {
        // if successful update server
        if (featureTableResult.get().booleanValue()) {
          serverResult = featureTable.applyEditsAsync();

          // apply changes to the server
          if (!serverResult.get().get(0).hasCompletedWithErrors()) {
            System.out.println("Successful");
          } else {
            System.out.println(
                "Server Error: Failed to update feature's geometry to server.");
          }
        } else {
          System.out.println(
              "Local Error: Failed to update feature's geometry to ServiceFeatureTable.");
        }
      } catch (Exception e) {
        // on any error, display the stack trace
        e.printStackTrace();
      }
    });
  }

  /**
   * Stops and releases all resources used in application.
   * 
   * @throws Exception if security manager doesn't allow JVM to exit with
   *           current status
   */
  @Override
  public void stop() throws Exception {

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
