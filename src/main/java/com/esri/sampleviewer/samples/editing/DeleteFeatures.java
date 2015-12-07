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
import javafx.scene.control.Button;
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
 * This sample demonstrates how to delete a Feature from a ServiceFeatureTable.
 * <h4>How it Works</h4>
 * 
 * A {@link ServiceFeatureTable} is created from a URL which stores
 * {@link Feature}s that are associated with that URL. To interact with those
 * Features a {@link FeatureLayer} needs to be created from that table. A
 * Feature is then selected using the {@link FeatureLayer#selectFeatures} method
 * and can be deleted from the table with the
 * {@link ServiceFeatureTable#deleteFeaturesAsync} method.
 * <p>
 * A ListenableFuture needs to be a class level field because it could get
 * garbage collected right after being set.
 */
public class DeleteFeatures extends Application {

  private MapView mapView;
  private FeatureLayer featureLayer;
  private ServiceFeatureTable featureTable;
  private FeatureQueryResult selectedFeatures;

  private ListenableFuture<FeatureQueryResult> queryResult;
  private ListenableFuture<Boolean> tableResult;
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
    stage.setTitle("Delete Features Sample");
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
    TextArea description = new TextArea("This sample shows how to delete "
        + "Features from a Service Feature Table. Click on the Feature to "
        + "select it and press the delete button to delete it.");
    description.setWrapText(true);
    description.autosize();
    description.setEditable(false);

    // create a delete button
    Button deleteButton = new Button("Delete Feature");
    deleteButton.setMaxWidth(Double.MAX_VALUE);
    deleteButton.setDisable(true);

    // delete the selected feature
    deleteButton.setOnAction(e -> {
      if (selectedFeatures != null) {
        deleteFeature();
      }
    });

    // add label, description, and button to the control panel
    vBoxControl.getChildren().addAll(descriptionLabel, description,
        deleteButton);
    try {

      // create a map with streets basemap
      final Map map = new Map(Basemap.createStreets());

      // create starting viewpoint for that map
      final SpatialReference spatialReference = SpatialReferences
          .getWebMercator();
      Point startPoint = new Point(-036773, 6710477, spatialReference);
      Viewpoint viewpoint = new Viewpoint(startPoint, 200000); // point and scale

      // set a initial viewpoint for the map
      map.setInitialViewpoint(viewpoint);

      // create service feature table from URL
      featureTable = new ServiceFeatureTable(SERVICE_FEATURE_URL);
      featureTable.getOutFields().add("*"); // * gets all fields from the table

      // create a feature layer from table
      featureLayer = new FeatureLayer(featureTable);

      // enable button when feature layer is done loading
      featureLayer.addDoneLoadingListener(() -> {
        deleteButton.setDisable(false);
      });

      // add the layer to the map
      map.getOperationalLayers().add(featureLayer);

      // create a view and set map to it
      mapView = new MapView();
      mapView.setMap(map);

      mapView.setOnMouseClicked(e -> {
        if (e.getButton() == MouseButton.PRIMARY) {
          // create a point from where the user clicked
          Point2D point = new Point2D(e.getX(), e.getY());

          // create a map point from the point
          Point mapPoint = mapView.screenToLocation(point);

          // select feature clicked
          selectFeatures(mapPoint);
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
   * Selects Features around mapPoint.
   * 
   * @param mapPoint x,y coordinate pair
   */
  private void selectFeatures(Point mapPoint) {

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
      // get selection
      selectedFeatures = queryResult.get();

      // if feature not selected
      if (!selectedFeatures.iterator().hasNext()) {
        selectedFeatures = null;
      }
    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Deletes selected Features from a ServiceFeatureTable and applies the
   * changes to the server.
   */
  private void deleteFeature() {

    if (selectedFeatures != null) {
      // delete the features from the service feature table
      tableResult = featureTable.deleteFeaturesAsync(selectedFeatures);

      try {
        // if successful update server
        if (tableResult.get().booleanValue()) {
          // apply changes to the server
          serverResult = featureTable.applyEditsAsync();

          // check if the server result was successful
          if (!serverResult.get().get(0).hasCompletedWithErrors()) {
            System.out.println("Feature successfully deleted");
          } else {
            System.out.println(
                "Server Error: Failed to delete feature from Server.");
          }
        } else {
          System.out.println(
              "Table Error: Failed to delete feature from ServiceFeatureTable.");
        }
      } catch (Exception e) {
        // on any error, display the stack trace
        e.printStackTrace();
      }
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
