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

package com.esri.samples.delete_features;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;

public class DeleteFeaturesSample extends Application {

  private MapView mapView;
  private FeatureLayer featureLayer;
  private ServiceFeatureTable featureTable;
  private Button deleteButton;
  private ListenableFuture<FeatureQueryResult> selectionResult;

  private static final String FEATURE_LAYER_URL =
      "https://sampleserver6.arcgisonline.com/arcgis/rest/services/DamageAssessment/FeatureServer/0";

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Delete Features Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a delete button and fill the width of the screen
      deleteButton = new Button("Delete");
      deleteButton.setMaxWidth(130);
      deleteButton.setDisable(true);

      // create event to delete the selected features on click
      deleteButton.setOnAction(event -> {
        // get selected features
        selectionResult = featureLayer.getSelectedFeaturesAsync();
        selectionResult.addDoneListener(() -> {
          try {
            FeatureQueryResult selected = selectionResult.get();
            // delete selected features
            deleteFeatures(selected, featureTable);
          } catch (InterruptedException | ExecutionException e) {
            displayMessage("Cannot delete features", e.getCause().getMessage());
          }
        });
      });

      // create a map with streets basemap
      ArcGISMap map = new ArcGISMap(Basemap.Type.STREETS, 40, -95, 4);

      // create a view for this ArcGISMap
      mapView = new MapView();

      // create service feature table from URL
      featureTable = new ServiceFeatureTable(FEATURE_LAYER_URL);

      // create a feature layer from table
      featureLayer = new FeatureLayer(featureTable);

      // add the layer to the ArcGISMap
      map.getOperationalLayers().add(featureLayer);

      mapView.setOnMouseClicked(event -> {
        // check for primary or secondary mouse click
        if (event.isStillSincePress() && event.getButton() == MouseButton.PRIMARY) {
          // create a point from where the user clicked
          Point2D point = new Point2D(event.getX(), event.getY());
          // identify the clicked feature
          ListenableFuture<IdentifyLayerResult> results = mapView.identifyLayerAsync(featureLayer, point, 1, false);
          results.addDoneListener(() -> {
            try {
              IdentifyLayerResult layer = results.get();
              // search the layers for identified features
              List<Feature> features = layer.getElements().stream().filter(g -> g instanceof Feature).map(
                  g -> (Feature) g).collect(Collectors.toList());
              // add the clicked feature to the selection
              if (features.size() > 0) {
                featureLayer.selectFeatures(features);
                deleteButton.setDisable(false);
              }
            } catch (InterruptedException | ExecutionException e) {
              displayMessage("Exception getting identify result", e.getCause().getMessage());
            }
          });
        }
      });

      // set ArcGISMap to be displayed in map view
      mapView.setMap(map);

      // add the map view and control box to stack pane
      stackPane.getChildren().addAll(mapView, deleteButton);
      StackPane.setAlignment(deleteButton, Pos.TOP_LEFT);
      StackPane.setMargin(deleteButton, new Insets(10, 0, 0, 10));

    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Deletes features from a ServiceFeatureTable and applies the changes to the
   * server.
   */
  private void deleteFeatures(FeatureQueryResult features, ServiceFeatureTable featureTable) {

    // delete feature from the feature table and apply edit to server
    featureTable.deleteFeaturesAsync(features).addDoneListener(() -> applyEdits(featureTable));
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
            displayMessage(null, "Feature successfully deleted");
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
