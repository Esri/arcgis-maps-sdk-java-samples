/* Copyright 2019 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.esri.samples.editing.edit_and_sync_features;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.concurrent.Job;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.Geodatabase;
import com.esri.arcgisruntime.data.GeodatabaseFeatureTable;
import com.esri.arcgisruntime.data.TileCache;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.tasks.geodatabase.*;

public class EditAndSyncFeaturesSample extends Application {

  private MapView mapView;
  private ArcGISMap map;
  private final AtomicInteger replica = new AtomicInteger();
  private EditAndSyncFeaturesSample.EditState currentEditState;
  private GraphicsOverlay graphicsOverlay;
  private Geodatabase geodatabase;
  private GeodatabaseSyncTask geodatabaseSyncTask;
  private Button geodatabaseButton;
  private ProgressIndicator progressIndicator;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size and add scene to stage
      stage.setTitle("Edit and Sync Features Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // set edit state to not ready until geodatabase generation has completed successfully
      currentEditState = EditState.NOTREADY;

      // create a map view and add a map
      mapView = new MapView();

      // create a graphics overlay and symbol to mark the extent
      graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // use local tile package for the base map
      TileCache sanFranciscoTileCache = new TileCache("samples-data/sanfrancisco/SanFrancisco.tpk");
      ArcGISTiledLayer tiledLayer = new ArcGISTiledLayer(sanFranciscoTileCache);
      Basemap basemap = new Basemap(tiledLayer);
      map = new ArcGISMap(basemap);
      mapView.setMap(map);

      // create a control panel
      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY, Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(180, 20);
      controlsVBox.getStyleClass().add("panel-region");

      // create button for user interaction
      geodatabaseButton = new Button("Generate Geodatabase");
      geodatabaseButton.setMaxWidth(Double.MAX_VALUE);
      controlsVBox.getChildren().add(geodatabaseButton);

      // create progress indicator
      progressIndicator = new ProgressIndicator();
      progressIndicator.setVisible(false);

      // add listener to handle generate/sync geodatabase button
      geodatabaseButton.setOnAction(e -> {
        if (currentEditState == EditState.NOTREADY) {
          // update button text and disable button
          geodatabaseButton.setDisable(true);
          geodatabaseButton.setText("Generating Geodatabase...");

          // show the extent of the geodatabase to be generated
          final SimpleLineSymbol boundarySymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF0000FF, 5);
          final Envelope extent = mapView.getVisibleArea().getExtent();
          Graphic boundary = new Graphic(extent, boundarySymbol);
          graphicsOverlay.getGraphics().add(boundary);

          // generate Geodatabase for the chosen extent
          generateGeodatabase(extent);
        } else if (currentEditState == EditState.READY) {
          syncGeodatabase();
        }
      });

      // handle clicks on the map view to select and move features
      mapView.setOnMouseClicked((event) -> {
        if (event.isStillSincePress() && event.getButton() == MouseButton.PRIMARY) {
          // get screen point where user clicked
          Point2D screenPoint = new Point2D(event.getX(), event.getY());

          // get map location corresponding to screen point
          Point mapPoint = mapView.screenToLocation(screenPoint);

          // identify any clicked feature
          ArrayList<Feature> selectedFeatures = new ArrayList<>();

          // iterate through all feature layers in the map
          for (Layer layer : mapView.getMap().getOperationalLayers()) {
            final FeatureLayer featureLayer = (FeatureLayer) layer;

            // identify any clicked feature
            ListenableFuture<IdentifyLayerResult> results = mapView.identifyLayerAsync(featureLayer, screenPoint, 1, false, 1);
            results.addDoneListener(() -> {
              try {
                // get selected feature, if it is a point
                List<GeoElement> elementList = results.get().getElements();
                if (elementList.size() > 0 && elementList.get(0) instanceof ArcGISFeature && elementList.get(0).getGeometry().getGeometryType() == GeometryType.POINT) {

                  // clicked on a feature, select it
                  ArcGISFeature selectedFeature = (ArcGISFeature) elementList.get(0);
                  // TODO: this doesn't clear the selection in all FL, hence some elements stay selected?
                  featureLayer.clearSelection(); // clear previous selection
                  featureLayer.selectFeature(selectedFeature);

                  // change the state to editing
                  currentEditState = EditState.EDITING;

                } else {
                  // didn't click on a feature
                  ListenableFuture<FeatureQueryResult> selectedQuery = featureLayer.getSelectedFeaturesAsync();
                  selectedQuery.addDoneListener(() -> {
                    try {
                      // check if a feature is currently selected
                      FeatureQueryResult selectedQueryResult = selectedQuery.get();
                      Iterator<Feature> features = selectedQueryResult.iterator();
                      if (features.hasNext()) {
                        // move selected feature to clicked location
                        ArcGISFeature selectedFeature = (ArcGISFeature) features.next();
                        selectedFeature.loadAsync();
                        selectedFeature.addDoneLoadingListener(() -> {
                          if (selectedFeature.canUpdateGeometry()) {
                            // apply the edits
                            selectedFeature.setGeometry(mapPoint);
                            selectedFeature.getFeatureTable().updateFeatureAsync(selectedFeature);

                            // refresh ui to enable syncinc
                            currentEditState = EditState.READY;
                            geodatabaseButton.setText("Sync Geodatabase");
                            geodatabaseButton.setDisable(false);
                          }
                        });
                      } // else nothing currently selected, do nothing
                    } catch (InterruptedException | ExecutionException e) {
                      displayMessage("Exception getting selected feature", e.getCause().getMessage());
                    }
                  });
                }
              } catch (InterruptedException | ExecutionException e) {
                displayMessage("Exception getting clicked feature", e.getCause().getMessage());
              }
            });
          }
        } else if (event.isStillSincePress() && event.getButton() == MouseButton.SECONDARY) {
          // on secondary mouse click, clear feature selection
          for (Layer layer : mapView.getMap().getOperationalLayers()) {
            final FeatureLayer featureLayer = (FeatureLayer) layer;
            featureLayer.clearSelection();
          }
        }
      });

      // add map view, controlsVBox and progressBar to stack pane
      stackPane.getChildren().addAll(mapView, controlsVBox, progressIndicator);
      StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
      StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));
      StackPane.setAlignment(progressIndicator, Pos.BOTTOM_CENTER);
      StackPane.setMargin(progressIndicator, new Insets(0, 0, 50, 0));

    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Generates a local geodatabase and sets it to the map.
   *
   * @args extent     the extent of the map from which a geodatabase is generated
   */
  public void generateGeodatabase(Envelope extent) {

    // define geodatabase sync task
    geodatabaseSyncTask = new GeodatabaseSyncTask("https://sampleserver6.arcgisonline.com/arcgis/rest/services/Sync/WildfireSync/FeatureServer");
    geodatabaseSyncTask.loadAsync();
    geodatabaseSyncTask.addDoneLoadingListener(() -> {

      // create generate geodatabase parameters for the current extent
      final ListenableFuture<GenerateGeodatabaseParameters> defaultParameters = geodatabaseSyncTask
              .createDefaultGenerateGeodatabaseParametersAsync(extent);
      defaultParameters.addDoneListener(() -> {
        try {
          // set parameters and don't include attachments
          GenerateGeodatabaseParameters parameters = defaultParameters.get();
          parameters.setReturnAttachments(false);

          // create a temporary file for the geodatabase
          File tempFile = File.createTempFile("gdb" + replica.getAndIncrement(), ".geodatabase");
          tempFile.deleteOnExit();

          // create and start the job
          GenerateGeodatabaseJob geodatabaseJob = geodatabaseSyncTask.generateGeodatabase(parameters, tempFile.getAbsolutePath());
          geodatabaseJob.start();

          // show progress
          showProgress(geodatabaseJob);

          // get geodatabase when done
          geodatabaseJob.addJobDoneListener(() -> {
            if (geodatabaseJob.getStatus() == Job.Status.SUCCEEDED) {
              geodatabase = geodatabaseJob.getResult();
              geodatabase.loadAsync();
              geodatabase.addDoneLoadingListener(() -> {
                if (geodatabase.getLoadStatus() == LoadStatus.LOADED) {
                  // add the geodatabase FeatureTables to the map as a FeatureLayer
                  geodatabase.getGeodatabaseFeatureTables().forEach(geodatabaseFeatureTable -> {
                    // TODO: why is the adding of the FT to the map not within a doneListener?
                    geodatabaseFeatureTable.loadAsync();
                    mapView.getMap().getOperationalLayers().add(new FeatureLayer(geodatabaseFeatureTable));
                  });
                  displayMessage("Geodatabase loaded successfully", null);
                } else {
                  displayMessage("Error loading geodatabase", geodatabase.getLoadError().getMessage());
                }
                // set edit state to ready, update button text
                currentEditState = EditState.READY;
                geodatabaseButton.setText("Sync Geodatabase");
              });
            } else if (geodatabaseJob.getError() != null) {
              displayMessage("Error generating geodatabase", geodatabaseJob.getError().getMessage());
            } else {
              displayMessage("Unknon Error generating geodatabase", null);
            }
          });

        } catch (InterruptedException | ExecutionException e) {
          displayMessage("Error generating geodatabase parameters", e.getMessage());
        } catch (IOException e) {
          displayMessage("Could not create file for geodatabase", e.getMessage());
        }
      });
    });
  }

  /**
   * Syncs changes made on either the local or web service geodatabase with each other.
   */
  private void syncGeodatabase() {

    // create parameters for the sync task
    SyncGeodatabaseParameters syncGeodatabaseParameters = new SyncGeodatabaseParameters();
    syncGeodatabaseParameters.setSyncDirection(SyncGeodatabaseParameters.SyncDirection.BIDIRECTIONAL);
    syncGeodatabaseParameters.setRollbackOnFailure(false);

    // get the layer ID for each feature table in the geodatabase, then add to the sync job
    for (GeodatabaseFeatureTable geodatabaseFeatureTable : geodatabase.getGeodatabaseFeatureTables()) {
      long serviceLayerId = geodatabaseFeatureTable.getServiceLayerId();
      SyncLayerOption syncLayerOption = new SyncLayerOption(serviceLayerId);
      syncGeodatabaseParameters.getLayerOptions().add(syncLayerOption);
    }

    // create a Sync Job and start it.
    final SyncGeodatabaseJob syncGeodatabaseJob = geodatabaseSyncTask.syncGeodatabase(syncGeodatabaseParameters, geodatabase);
    syncGeodatabaseJob.start();

    // show progress
    showProgress(syncGeodatabaseJob);

    // add a job done listener to the Sync Job
    syncGeodatabaseJob.addJobDoneListener(() -> {
      if (syncGeodatabaseJob.getStatus() == Job.Status.SUCCEEDED) {
        displayMessage("Database Sync Complete", null);
        geodatabaseButton.setDisable(true);
      } else {
        displayMessage("Database did not sync correctly!", syncGeodatabaseJob.getError().getMessage());
      }
    });
  }

  /**
   * Show a progress bar
   *
   * @param job the job to show progress for
   */
  private void showProgress(Job job) {

    // disable the button while the job runs
    geodatabaseButton.setDisable(true);

    //  show progress
    job.addProgressChangedListener(() -> progressIndicator.setVisible(true));

    // re-enable button, hide progress indicator on complete
    job.addJobDoneListener(() -> {
      if (job.getStatus() == Job.Status.SUCCEEDED) {
        geodatabaseButton.setDisable(false);
        Platform.runLater(() -> progressIndicator.setVisible(false));
      }
    });
  }

  /**
   * Show a message in an alert dialog.
   *
   * @param title   title of alert
   * @param message message to display
   */
  private void displayMessage(String title, String message) {

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

  private enum EditState { // enumeration to track editing of points
    NOTREADY,           // Geodatabase has not yet been generated
    EDITING,            // A feature is in the process of being moved
    READY               // The Geodatabase is ready for synchronisation or further edits
  }
}
