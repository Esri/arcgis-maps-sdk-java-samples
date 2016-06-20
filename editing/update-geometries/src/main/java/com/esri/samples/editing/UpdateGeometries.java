/*
 * Copyright 2015 Esri. Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.esri.samples.editing;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.datasource.Feature;
import com.esri.arcgisruntime.datasource.arcgis.ArcGISFeature;
import com.esri.arcgisruntime.datasource.arcgis.FeatureEditResult;
import com.esri.arcgisruntime.datasource.arcgis.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;

public class UpdateGeometries extends Application {

  private MapView mapView;
  private static ServiceFeatureTable featureTable;
  private static FeatureLayer featureLayer;
  private static Feature selected;

  private static final String FEATURE_LAYER_URL =
      "http://sampleserver6.arcgisonline.com/arcgis/rest/services/DamageAssessment/FeatureServer/0";

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Update Geometries Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a ArcGISMap with streets basemap and initial viewpoint
      // coordinates
      ArcGISMap map = new ArcGISMap(Basemap.Type.STREETS, 51.5014, -0.1425, 11);

      // create a view for this ArcGISMap
      mapView = new MapView();

      // create service feature table from URL
      featureTable = new ServiceFeatureTable(FEATURE_LAYER_URL);

      // create a feature layer from table
      featureLayer = new FeatureLayer(featureTable);

      // add the layer to the ArcGISMap
      map.getOperationalLayers().add(featureLayer);

      // set ArcGISMap to be displayed in view
      mapView.setMap(map);

      mapView.setOnMouseClicked(event -> {
        // check for primary or secondary mouse click
        if (event.isStillSincePress() && event.getButton() == MouseButton.PRIMARY) {

          // create a point from where the user clicked
          Point2D point = new Point2D(event.getX(), event.getY());

          // create map point from point
          Point mapPoint = mapView.screenToLocation(point);

          // identify the clicked feature
          ListenableFuture<IdentifyLayerResult> results = mapView.identifyLayerAsync(featureLayer, point, 1, 1);
          results.addDoneListener(() -> {
            try {
              // get selected feature
              List<GeoElement> elements = results.get().getIdentifiedElements();
              if (elements.size() > 0 && elements.get(0) instanceof Feature) {
                selected = (Feature) elements.get(0);

                // replace the previous selection
                featureLayer.clearSelection();
                featureLayer.selectFeature(selected);
              } else {
                // move selected features
                moveSelected(mapPoint);
              }
            } catch (InterruptedException | ExecutionException e) {
              displayMessage("Exception getting identify result", e.getCause().getMessage());
            }
          });
          // check for secondary mouse click
        } else if (event.isStillSincePress() && event.getButton() == MouseButton.SECONDARY) {
          featureLayer.clearSelection();
        }
      });

      // add the map view to stack pane
      stackPane.getChildren().add(mapView);

    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Updates the location of the selected features.
   * 
   * @param newPoint new location to move selected feature
   */
  private void moveSelected(Point newPoint) {

    // check if feature allows updating and move it
    Stream.of(selected).map(f -> (ArcGISFeature) f).filter(ArcGISFeature::canUpdateGeometry).forEach(f -> {
      // update position
      f.setGeometry(newPoint);

      // update the feature to display on map view
      ListenableFuture<Void> featureTableResult = featureTable.updateFeatureAsync(f);
      featureTableResult.addDoneListener(UpdateGeometries::applyEdits);
    });
  }

  /**
   * Sends any edits on the ServiceFeatureTable to the server.
   */
  private static void applyEdits() {

    // apply the changes to the server
    ListenableFuture<List<FeatureEditResult>> editResult = featureTable.applyEditsAsync();
    editResult.addDoneListener(() -> {
      try {
        List<FeatureEditResult> edits = editResult.get();
        // check if the server edit was successful
        if (edits != null && edits.size() > 0 && edits.get(0).hasCompletedWithErrors()) {
          throw edits.get(0).getError();
        }
      } catch (InterruptedException | ExecutionException e) {
        displayMessage("Error applying edits on server", e.getCause().getMessage());
      }
    });
  }

  /**
   * Shows a message in an alert dialog.
   *
   * @param title title of alert
   * @param message message to display
   */
  private static void displayMessage(String title, String message) {

    Platform.runLater(() -> {
      Alert dialog = new Alert(Alert.AlertType.INFORMATION);
      dialog.setHeaderText(title);
      dialog.setContentText(message);
      dialog.showAndWait();
    });
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() throws Exception {

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
