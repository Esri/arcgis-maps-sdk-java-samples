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

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.datasource.FeatureQueryResult;
import com.esri.arcgisruntime.datasource.QueryParameters;
import com.esri.arcgisruntime.datasource.QueryParameters.SpatialRelationship;
import com.esri.arcgisruntime.datasource.arcgis.FeatureEditResult;
import com.esri.arcgisruntime.datasource.arcgis.ServiceFeatureTable;
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

/**
 * This sample demonstrates how to update the geometry of a <@Feature> from a
 * <@ServiceFeatureTable>. How it works: a ServiceFeatureTable is created from a
 * URL holding the <@FeatureLayer>. The FeatureLayer is then added to a
 * <@Map> and displayed on a <@MapView>. First click on a feature to select it
 * on the map. Then click on a new location to save the new geometry for that
 * feature. A secondary click while a feature is selected, will deselect the
 * feature. Note: only a thousand features can be loaded at one time, so when
 * beginning a session make sure to be zoomed in to produce the best results.
 */
public class UpdateGeometries extends Application {

  private MapView mapView;
  private FeatureLayer featureLayer;
  private FeatureQueryResult selectedFeatures;

  private static final String FEATURE_LAYER_URL =
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
    stage.setTitle("Update Geometries Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    // create a control panel
    VBox vBoxControl = new VBox(6);
    vBoxControl.setMaxSize(240, 120);
    vBoxControl.getStyleClass().add("panel-region");

    // create sample label and description
    Label descriptionLabel = new Label("Sample Description");
    descriptionLabel.getStyleClass().add("panel-label");

    TextArea description = new TextArea("This sample shows how to update \n"
        + "the geometry of a feature. Click on a \n"
        + "feature to select it, then click on the\n"
        + "map to move the feature to a new\n" + "location.");

    description.setEditable(false);
    description.setMinSize(210, 100);

    // add sample label and description to the control panel
    vBoxControl.getChildren().addAll(descriptionLabel, description);

    try {
      // create spatial reference for point
      SpatialReference spatialReference = SpatialReferences.getWebMercator();
      // create an initial viewpoint with a point and scale
      Point pointLondon = new Point(-036773, 6710477, spatialReference);
      Viewpoint viewpoint = new Viewpoint(pointLondon, 200000);

      // create a map with streets basemap
      Map map = new Map(Basemap.createStreets());

      // set viewpoint for map
      map.setInitialViewpoint(viewpoint);

      // create a view for this map
      mapView = new MapView();

      // create service feature table from URL
      ServiceFeatureTable featureTable = new ServiceFeatureTable(
          FEATURE_LAYER_URL);
      featureTable.getOutFields().add("*"); // * gets all fields from the
      // table

      // create a feature layer from table
      featureLayer = new FeatureLayer(featureTable);

      // add the layer to the map
      map.getOperationalLayers().add(featureLayer);

      // set map to be displayed in view
      mapView.setMap(map);

      mapView.setOnMouseClicked(e -> {
        // check for primary and secondary mouse click
        if (e.getButton() == MouseButton.PRIMARY) {
          // create point from where user clicked
          Point2D point = new Point2D(e.getX(), e.getY());

          // create map point from point
          Point mapPoint = mapView.screenToLocation(point);

          // check that no feature is selected
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
            // clear the selection
            featureLayer.clearSelection();
            // set selectedFeatures to null
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
   * Selects features around map point.
   * 
   * @param mapPoint x,y-coordinate pair
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
    final ListenableFuture<FeatureQueryResult> queryFeatures = featureLayer
        .selectFeatures(queryParams,
            SelectionMode.NEW);

    try {
      // get user's selection
      selectedFeatures = queryFeatures.get();
    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Updates the location of the features that are selected.
   * 
   * @param newPoint x,y-coordinate pair
   * @param damageTable holds all Feature data
   */
  private void updateGeometry(Point newPoint, ServiceFeatureTable damageTable) {

    selectedFeatures.forEach(feature -> {
      // set x,y-coordinate pair from feature to newPoint
      feature.setGeometry(newPoint);

      // update the feature to display on map view
      final ListenableFuture<Boolean> featureTableResult = damageTable
          .updateFeatureAsync(feature);

      try {
        // if successful, update changes to the server
        if (featureTableResult.get().booleanValue()) {
          final ListenableFuture<List<FeatureEditResult>> serverResult =
              damageTable.applyEditsAsync();
          // check if the server result was successful
          if (!serverResult.get().get(0).hasCompletedWithErrors()) {
            System.out.println("Successful");
          } else {
            System.out.println(
                "Server Error: Feature failed to update geometry to server.");
          }
        } else {
          System.out
              .println(
                  "Local Error: Feature failed to update geometry to ServiceFeatureTable locally.");
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
