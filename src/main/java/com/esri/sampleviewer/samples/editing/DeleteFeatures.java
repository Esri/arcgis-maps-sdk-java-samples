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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This sample demonstrates how to delete a <@Feature> from a
 * <@ServiceFeatureTable>. How it works: a ServiceFeatureTable is created from a
 * URL holding the <@FeatureLayer>. This FeatureLayer is then added to a
 * <@Map> and displayed on a <@MapView>. Once the user selects a Feature and the
 * delete button is pressed the Feature will then be deleted from the
 * ServiceFeatureTable. Lastly, the deleted Feature action will be saved to the
 * server.
 */
public class DeleteFeatures extends Application {

  private MapView mapView;
  private FeatureLayer featureLayer;
  private ServiceFeatureTable featureTable;
  private FeatureQueryResult selectedFeatures;
  private Button deleteButton;

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
    stage.setTitle("Delete Features Sample");
    stage.setWidth(700);
    stage.setHeight(800);
    stage.setScene(scene);
    stage.show();

    // create a control panel
    VBox vBoxControl = new VBox(6);
    vBoxControl.setMaxSize(240, 120);
    vBoxControl.getStyleClass().add("panel-region");

    // create sample label and description
    Label descriptionLabel = new Label("Sample Description");
    descriptionLabel.getStyleClass().add("panel-label");

    TextArea description = new TextArea(
        "This sample shows how to delete a\n"
            + "feature from a ServiceFeatureTable.\n"
            + "Click on the feature to highligh it and\n"
            + "press the delete button to delete it.");
    description.setEditable(false);
    description.setMinSize(210, 80);

    // create a delete button and fill the width of the screen
    deleteButton = new Button("Delete Feature");
    deleteButton.setMaxWidth(Double.MAX_VALUE);
    deleteButton.setDisable(true);

    // delete the selected feature
    deleteButton.setOnAction(e -> {
      deleteFeature();
    });

    // add sample label and description to the control panel
    vBoxControl.getChildren().addAll(descriptionLabel, description,
        deleteButton);

    try {
      // create an initial viewpoint
      Point pointLondon = new Point(-036773, 6710477, SpatialReferences
          .getWebMercator());
      Viewpoint viewpoint = new Viewpoint(pointLondon, 200000);

      // create a map with streets basemap
      Map map = new Map(Basemap.createStreets());

      // set initial viewpoint for the map
      map.setInitialViewpoint(viewpoint);

      // create a view for this map
      mapView = new MapView();

      // create service feature table from URL
      featureTable = new ServiceFeatureTable(FEATURE_LAYER_URL);

      // focuses on the features that are located in the viewpoint
      featureTable.setBufferFactor(1);
      featureTable.getOutFields().add("*"); // * gets all fields from the
      // table

      // create a feature layer from table
      featureLayer = new FeatureLayer(featureTable);

      // add the layer to the map
      map.getOperationalLayers().add(featureLayer);

      mapView.setOnMouseClicked(e -> {
        // check for primary or secondary mouse click
        if (e.getButton() == MouseButton.PRIMARY) {
          // create a point from where the user clicked
          Point2D point = new Point2D(e.getX(), e.getY());

          // create a map point from a point
          Point mapPoint = mapView.screenToLocation(point);

          // select feature that the user clicked
          selectFeature(mapPoint);
        }
      });

      // set map to be displayed in map view
      mapView.setMap(map);

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
   * Checks if any features were selected around the map point. If any features
   * were selected, store those features in selectFeatures. If not set
   * selectedFeatures to null.
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
    ListenableFuture<FeatureQueryResult> result = featureLayer.selectFeatures(
        queryParams, SelectionMode.NEW);

    try {
      // get selection
      selectedFeatures = result.get();

      // if feature wasn't selected set selectedFeatures to null
      if (!selectedFeatures.iterator().hasNext()) {
        selectedFeatures = null;
        // disable the button
        deleteButton.setDisable(true);
      } else {
        // enable the delete button
        deleteButton.setDisable(false);
      }

    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Deletes the Features from a ServiceFeatureTable and applies the changes to
   * the server.
   */
  private void deleteFeature() {

    if (selectedFeatures != null) {
      // delete the features from the service feature table
      final ListenableFuture<Boolean> tableResult = featureTable
          .deleteFeaturesAsync(selectedFeatures);

      try {
        // update the changes to the server if successful
        if (tableResult.get().booleanValue()) {
          // apply changes to the server
          final ListenableFuture<List<FeatureEditResult>> serverResult =
              featureTable.applyEditsAsync();
          // check if the server result was successful
          if (!serverResult.get().get(0).hasCompletedWithErrors()) {
            System.out.println("Feature successfully deleted");
            // disable the delete button
            deleteButton.setDisable(true);
          } else {
            System.out.println(
                "Server Error: Feature failed to be deleted to Server.");
          }
        } else {
          System.out.println(
              "Local Error: Feature failed to be deleted to ServiceFeatureTable locally.");
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
