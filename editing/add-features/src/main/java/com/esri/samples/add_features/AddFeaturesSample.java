/*
 * Copyright 2017 Esri.
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

package com.esri.samples.add_features;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;

public class AddFeaturesSample extends Application {

  private MapView mapView;

  private ServiceFeatureTable featureTable;

  private static final String SERVICE_LAYER_URL =
      "https://sampleserver6.arcgisonline.com/arcgis/rest/services/DamageAssessment/FeatureServer/0";

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Add Features Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a map with streets basemap style
      ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_STREETS);

      // create a map view and set its map
      mapView = new MapView();
      mapView.setMap(map);

      // set a viewpoint on the map view
      mapView.setViewpoint(new Viewpoint(40, -95, 40000000));

      // create service feature table from URL
      featureTable = new ServiceFeatureTable(SERVICE_LAYER_URL);

      // create a feature layer from table
      FeatureLayer featureLayer = new FeatureLayer(featureTable);

      // add the layer to the ArcGISMap
      map.getOperationalLayers().add(featureLayer);

      mapView.setOnMouseClicked(event -> {
        // check that the primary mouse button was clicked
        if (event.isStillSincePress() && event.getButton() == MouseButton.PRIMARY) {
          // create a point from where the user clicked
          Point2D point = new Point2D(event.getX(), event.getY());

          // create a map point from a point
          Point mapPoint = mapView.screenToLocation(point);

          // for a wrapped around map, the point coordinates include the wrapped around value
          // for a service in projected coordinate system, this wrapped around value has to be normalized
          Point normalizedMapPoint = (Point) GeometryEngine.normalizeCentralMeridian(mapPoint);

          // add a new feature to the service feature table
          addFeature(normalizedMapPoint, featureTable);
        }
      });

      // add the map view to stack pane
      stackPane.getChildren().addAll(mapView);

    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Adds a new Feature to a ServiceFeatureTable and applies the changes to the
   * server.
   * 
   * @param mapPoint location to add feature
   * @param featureTable service feature table to add feature
   */
  private void addFeature(Point mapPoint, ServiceFeatureTable featureTable) {

    // create default attributes for the feature
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("typdamage", "Destroyed");
    attributes.put("primcause", "Earthquake");

    // creates a new feature using default attributes and point
    Feature feature = featureTable.createFeature(attributes, mapPoint);

    // check if feature can be added to feature table
    if (featureTable.canAdd()) {
      // add the new feature to the feature table and to server
      featureTable.addFeatureAsync(feature).addDoneListener(() -> applyEdits(featureTable));
    } else {
      displayMessage(null, "Cannot add a feature to this feature table");
    }
  }

  /**
   * Sends any edits on the ServiceFeatureTable to the server.
   *
   * @param featureTable service feature table
   */
  private void applyEdits(ServiceFeatureTable featureTable) {

    // apply the changes to the server
    ListenableFuture<List<FeatureEditResult>> editResult = featureTable.applyEditsAsync();
    editResult.addDoneListener(() -> {
      try {
        List<FeatureEditResult> edits = editResult.get();
        // check if the server edit was successful
        if (edits != null && edits.size() > 0) {
          if (!edits.get(0).hasCompletedWithErrors()) {
            displayMessage(null, "Feature successfully added");
          } else {
            throw edits.get(0).getError();
          }
        }
      } catch (InterruptedException | ExecutionException e) {
        displayMessage("Exception applying edits on server", e.getCause().getMessage());
      }
    });
  }

  /**
   * Shows a message in an alert dialog.
   *
   * @param title title of alert
   * @param message message to display
   */
  private void displayMessage(String title, String message) {

    Platform.runLater(() -> {
      Alert dialog = new Alert(AlertType.INFORMATION);
      dialog.initOwner(mapView.getScene().getWindow());
      dialog.setHeaderText(title);
      dialog.setContentText(message);
      dialog.showAndWait();
    });
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
