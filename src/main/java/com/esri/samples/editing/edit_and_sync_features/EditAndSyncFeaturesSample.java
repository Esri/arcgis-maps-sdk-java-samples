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
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.arcgisservices.IdInfo;
import com.esri.arcgisruntime.concurrent.Job;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.Geodatabase;
import com.esri.arcgisruntime.data.GeodatabaseFeatureTable;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.data.TileCache;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.layers.LayerContent;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.view.DrawStatus;
import com.esri.arcgisruntime.mapping.view.DrawStatusChangedEvent;
import com.esri.arcgisruntime.mapping.view.DrawStatusChangedListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.ViewpointChangedListener;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.tasks.geodatabase.GenerateGeodatabaseJob;
import com.esri.arcgisruntime.tasks.geodatabase.GenerateGeodatabaseParameters;
import com.esri.arcgisruntime.tasks.geodatabase.GeodatabaseSyncTask;
import com.esri.arcgisruntime.tasks.geodatabase.SyncGeodatabaseJob;
import com.esri.arcgisruntime.tasks.geodatabase.SyncGeodatabaseParameters;
import com.esri.arcgisruntime.tasks.geodatabase.SyncLayerOption;

public class EditAndSyncFeaturesSample extends Application {

  private Button geodatabaseButton;
  private Button syncButton;
  private Graphic geodatabaseExtentGraphics;
  private ProgressBar progressBar;

  private ArcGISMap map;
  private EditAndSyncFeaturesSample.EditState currentEditState = EditState.NOTREADY;
  private Geodatabase geodatabase;
  private GeodatabaseSyncTask geodatabaseSyncTask;
  private MapView mapView;
  private ViewpointChangedListener viewpointChangedListener;
  private String featureServiceUrl = "https://sampleserver6.arcgisonline.com/arcgis/rest/services/Sync/WildfireSync/FeatureServer";
  private GraphicsOverlay graphicsOverlay;

  // enumeration to track editing of points
  private enum EditState {
    NOTREADY,           // Geodatabase has not yet been generated
    EDITING,            // A feature is in the process of being moved
    READY               // The Geodatabase is ready for synchronisation or further edits
  }

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

      // set title, size and add scene to stage
      stage.setTitle("Edit and Sync Features Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // use local tile package to create a tiled layer
      TileCache sanFranciscoTileCache = new TileCache("samples-data/sanfrancisco/SanFrancisco.tpk");
      ArcGISTiledLayer tiledLayer = new ArcGISTiledLayer(sanFranciscoTileCache);

      // create a basemap from the tiled layer, and add it to a map
      Basemap basemap = new Basemap(tiledLayer);
      map = new ArcGISMap(basemap);

      // create a map view and add the map
      mapView = new MapView();
      mapView.setMap(map);

      // create a control panel
      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY, Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMinSize(180, 100);
      controlsVBox.setMaxSize(180, 100);
      controlsVBox.getStyleClass().add("panel-region");

      // create progress bar
      progressBar = new ProgressBar();
      progressBar.setVisible(false);
      progressBar.setMinWidth(160);

      // create buttons for user interaction
      geodatabaseButton = new Button("Generate Geodatabase");
      geodatabaseButton.setMaxWidth(Double.MAX_VALUE);
      geodatabaseButton.setDisable(true);
      syncButton = new Button("Sync Geodatabase");
      syncButton.setMaxWidth(Double.MAX_VALUE);
      syncButton.setDisable(true);
      controlsVBox.getChildren().addAll(geodatabaseButton, syncButton, progressBar);

      // create a graphics overlay
      graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      monitorViewpointChanges();

      // create a task for generating a geodatabase
      geodatabaseSyncTask = new GeodatabaseSyncTask(featureServiceUrl);
      geodatabaseSyncTask.loadAsync();

      // show the contents of the feature service
      geodatabaseSyncTask.addDoneLoadingListener(() -> {
        if (geodatabaseSyncTask.getLoadStatus() == LoadStatus.LOADED) {
          // add all graphics from the service to the map.
          for (IdInfo layer : geodatabaseSyncTask.getFeatureServiceInfo().getLayerInfos()) {
            // get the URL for this particular layer
            String onlineTableUri = featureServiceUrl + "/" + layer.getId();

            // create the service feature table
            ServiceFeatureTable onlineFeatureTable = new ServiceFeatureTable(onlineTableUri);
            onlineFeatureTable.loadAsync();

            // add the features to the map view
            onlineFeatureTable.addDoneLoadingListener(() -> {
              // only add tables that contain point features to the map as feature layers
              if (onlineFeatureTable.getLoadStatus() == LoadStatus.LOADED && onlineFeatureTable.getGeometryType() == GeometryType.POINT) {
                map.getOperationalLayers().add(new FeatureLayer(onlineFeatureTable));
              }

            });
          }
          geodatabaseButton.setDisable(false);
        } else {
          new Alert(Alert.AlertType.ERROR, "Error initiating geodatabase task").show();
        }
      });

      // add listener to handle generate geodatabase button
      geodatabaseButton.setOnAction(e -> {
        if (currentEditState == EditState.NOTREADY) {
          // update button text and disable button
          geodatabaseButton.setDisable(true);
          geodatabaseButton.setText("Generating Geodatabase...");

          // disable updating the geodatabase extent
          mapView.removeViewpointChangedListener(viewpointChangedListener);

          // generate a geodatabase for the chosen area
          generateGeodatabase();
        }
      });

      // add listener to handle sync geodatabase button
      syncButton.setOnAction(e -> {
        if (currentEditState == EditState.READY) {
          // sync the geodatabase
          syncGeodatabase();
        }
      });

      // handle clicks on the map view to select and move features
      mapView.setOnMouseClicked(event -> {

        // disregard click if not ready for edits
        if (currentEditState == EditState.NOTREADY) {
          return;
        } else {

          // get screen point where user clicked
          Point2D screenPoint = new Point2D(event.getX(), event.getY());

          // get map location corresponding to screen point
          Point mapPoint = mapView.screenToLocation(screenPoint);

          // on primary click, move an already selected feature, or select a clicked feature
          if (event.getButton() == MouseButton.PRIMARY && event.isStillSincePress()) {

            // if an edit is in process (a feature is selected), finish the edit
            if (currentEditState == EditState.EDITING) {

              moveSelectedFeature(mapPoint);

              // else start an edit
            } else {

              findAndSelectFeature(screenPoint);

            }
          } else if (event.getButton() == MouseButton.SECONDARY && event.isStillSincePress()) {

            // on secondary mouse click, clear feature selection
            for (Layer layer : mapView.getMap().getOperationalLayers()) {
              FeatureLayer featureLayer = (FeatureLayer) layer;
              featureLayer.clearSelection();
            }

            // set the edit state to ready
            currentEditState = EditState.READY;
          }
        }
      });
      // add map view, controlsVBox and progressBar to stack pane
      stackPane.getChildren().addAll(mapView, controlsVBox);
      StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
      StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));
    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Generates a local geodatabase and sets it to the map.
   */
  private void generateGeodatabase() {

    // define geodatabase sync task
    geodatabaseSyncTask = new GeodatabaseSyncTask(featureServiceUrl);
    geodatabaseSyncTask.loadAsync();
    geodatabaseSyncTask.addDoneLoadingListener(() -> {

      // create generate geodatabase parameters for the current extent
      final ListenableFuture<GenerateGeodatabaseParameters> defaultParametersFuture = geodatabaseSyncTask
              .createDefaultGenerateGeodatabaseParametersAsync(geodatabaseExtentGraphics.getGeometry());
      defaultParametersFuture.addDoneListener(() -> {
        try {
          // set parameters and don't include attachments
          GenerateGeodatabaseParameters defaultParameters = defaultParametersFuture.get();
          defaultParameters.setReturnAttachments(false);

          // create a temporary file for the geodatabase
          File tempFile = File.createTempFile("gdb", ".geodatabase");
          tempFile.deleteOnExit();

          // create and start the job
          GenerateGeodatabaseJob geodatabaseJob = geodatabaseSyncTask.generateGeodatabase(defaultParameters, tempFile.getAbsolutePath());
          geodatabaseJob.start();

          // show the job progress
          showJobProgress(geodatabaseJob);

          // get the geodatabase when done
          geodatabaseJob.addJobDoneListener(() -> handleGenerationCompleted(geodatabaseJob));

        } catch (InterruptedException | ExecutionException e) {
          displayMessage("Error generating geodatabase parameters", e.getMessage());
        } catch (IOException e) {
          displayMessage("Could not create file for geodatabase", e.getMessage());
        }
      });
    });
  }

  /**
   * Create feature layers from the geodatabase, adds these to the map view, and toggles UI
   *
   * @param generateGeodatabaseJob the generate geodatabase job that is handled
   */
  private void handleGenerationCompleted(GenerateGeodatabaseJob generateGeodatabaseJob) {
    // check whether the geodatabase generation is complete successfully
    if (generateGeodatabaseJob.getStatus() == Job.Status.SUCCEEDED) {
      // get the generated database
      geodatabase = generateGeodatabaseJob.getResult();
      geodatabase.loadAsync();

      // display the contents of the geodatabase to the map
      geodatabase.addDoneLoadingListener(() -> {
        if (geodatabase.getLoadStatus() == LoadStatus.LOADED) {

          // remove the existing layers from the map
          map.getOperationalLayers().clear();

          // iterate through the feature tables in the geodatabase and add new layers to the map
          geodatabase.getGeodatabaseFeatureTables().forEach(geodatabaseFeatureTable -> {
            geodatabaseFeatureTable.loadAsync();
            geodatabaseFeatureTable.addDoneLoadingListener(() -> {

              // add only feature tables that have point data
              if (geodatabaseFeatureTable.getGeometryType() == GeometryType.POINT) {
                // create a new feature layer from the table
                FeatureLayer featureLayer = new FeatureLayer(geodatabaseFeatureTable);

                // add the feature layer to the map
                map.getOperationalLayers().add(featureLayer);
              }
            });
          });

          // set editing state to ready
          currentEditState = EditState.READY;

          // show success message
          displayMessage("Geodatabase loaded successfully", null);
        } else {
          displayMessage("Error loading geodatabase", geodatabase.getLoadError().getMessage());
        }
        // update button text to signal we are ready to edit
        geodatabaseButton.setText("Geodatabase Ready");
        geodatabaseButton.setDisable(true);
      });
    } else if (generateGeodatabaseJob.getError() != null) {
      displayMessage("Error generating geodatabase", generateGeodatabaseJob.getError().getMessage());
    } else {
      displayMessage("Unknown Error generating geodatabase", null);
    }
  }

  /**
   * Syncs changes made on either the local or web service geodatabase with each other.
   */
  private void syncGeodatabase() {
    // disable the button and update text
    syncButton.setText("Syncing Geodatabase...");
    syncButton.setDisable(true);

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

    // show job progress
    showJobProgress(syncGeodatabaseJob);

    // add a job done listener to the Sync Job
    syncGeodatabaseJob.addJobDoneListener(() -> handleSyncCompleted(syncGeodatabaseJob));
  }
  
  /**
   * Shows a success message and updates the UI when a geodatabase sync job is complete.
   *
   * @param syncGeodatabaseJob the sync geodatabase job that is handled
   */
  private void handleSyncCompleted(SyncGeodatabaseJob syncGeodatabaseJob) {
    if (syncGeodatabaseJob.getStatus() == Job.Status.SUCCEEDED) {

      // show success message
      displayMessage("Database Sync Complete", null);

      // update button text to signal we are ready to edit
      syncButton.setText("Sync Geodatabase");
      syncButton.setDisable(false);
    } else {
      displayMessage("Database did not sync correctly!", syncGeodatabaseJob.getError().getMessage());
    }
  }

  /**
   * Queries all available feature layers for a selected feature, then moves the feature to a map point.
   * @param mapPoint the target point to which the feature is moved
   */
  private void moveSelectedFeature(Point mapPoint){
    // iterate through all feature layers in the map
    mapView.getMap().getOperationalLayers().stream().filter(layer -> layer instanceof FeatureLayer).forEach(layer -> {
      FeatureLayer featureLayer = (FeatureLayer) layer;
      // retrieve the selected features in the feature layer
      ListenableFuture<FeatureQueryResult> selectedQuery = featureLayer.getSelectedFeaturesAsync();
      selectedQuery.addDoneListener(() -> {
        try {

          // get the result of the query (a set of features)
          FeatureQueryResult selectedQueryResult = selectedQuery.get();

          // create an iterator from the result
          Iterator<Feature> features = selectedQueryResult.iterator();

          // check if there are elements in the iteration
          if (features.hasNext()) {

            // retrieve the currently selected feature
            ArcGISFeature selectedFeature = (ArcGISFeature) features.next();
            selectedFeature.loadAsync();
            selectedFeature.addDoneLoadingListener(() -> {
              if (selectedFeature.canUpdateGeometry()) {

                // move the selected feature to the clicked map point
                selectedFeature.setGeometry(mapPoint);

                // update the feature table
                selectedFeature.getFeatureTable().updateFeatureAsync(selectedFeature);

                // deselect the feature
                featureLayer.clearSelection();

                // refresh ui to enable syncing
                currentEditState = EditState.READY;
                syncButton.setDisable(false);
              }
            });
          }
        } catch (InterruptedException | ExecutionException e) {
          displayMessage("Exception getting selected feature", e.getMessage());
        }
      });

    });
  }

  /**
   * Queries all feature layers to find features at a given screen point, and marks a feature as selected
   * @param screenPoint
   */
  private void findAndSelectFeature(Point2D screenPoint){
    // query all layers for features at the clicked point
    ListenableFuture<List<IdentifyLayerResult>> identifyLayersResultFuture = mapView.identifyLayersAsync(screenPoint, 10, false);
    identifyLayersResultFuture.addDoneListener(() -> {
      try {
        // get the result of the query
        List<IdentifyLayerResult> identifyLayerResults = identifyLayersResultFuture.get();
        identifyLayerResults.forEach(identifyLayerResult -> {

          // get the content of each layer result
          LayerContent layerContent = identifyLayerResult.getLayerContent();

          // check that the result is a feature layer
          if (layerContent instanceof FeatureLayer) {

            // retrieve the geoelements in the feature layer
            List<GeoElement> geoElements = identifyLayerResult.getElements();
            geoElements.forEach(geoElement -> {
              if (geoElement instanceof Feature) {

                // grab the new feature and make it selected
                ((FeatureLayer) layerContent).selectFeature((Feature) geoElement);

                // change the state to editing
                currentEditState = EditState.EDITING;
              }
            });
          }
        });
      } catch (Exception e) {
        new Alert(Alert.AlertType.ERROR, "error finding selected features").show();
      }
    });
  }


  /**
   * Adds listeners to the viewpoint to capture the extent of the geodatabase to be generated.
   */
  private void monitorViewpointChanges() {
    // create a graphic (using a red line) to mark the extent of the geodatabase to be generated
    geodatabaseExtentGraphics = new Graphic();
    graphicsOverlay.getGraphics().add(geodatabaseExtentGraphics);
    SimpleLineSymbol boundarySymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFFFF0000, 2);
    geodatabaseExtentGraphics.setSymbol(boundarySymbol);

    // when the draw status is completed for the first time, draw the extent of the geodatabase to be generated
    DrawStatusChangedListener drawStatusChangedListener = new DrawStatusChangedListener() {
      @Override
      public void drawStatusChanged(DrawStatusChangedEvent drawStatusChangedEvent) {
        if (drawStatusChangedEvent.getDrawStatus() == DrawStatus.COMPLETED) {
          updateGeodatabaseExtentEnvelope();
          mapView.removeDrawStatusChangedListener(this);
        }
      }
    };
    mapView.addDrawStatusChangedListener(drawStatusChangedListener);

    // create a listener used to update the extent of the geodatabase area when the viewpoint changes
    viewpointChangedListener = viewpointChangedEvent -> updateGeodatabaseExtentEnvelope();

    // add the listener to the map view, to update the extent whenever the viewpoint changes
    mapView.addViewpointChangedListener(viewpointChangedListener);
  }

  /**
   * Updates the extent of the area marked with a red border to be used for geodatabase generation.
   */
  private void updateGeodatabaseExtentEnvelope() {
    if (map.getLoadStatus() == LoadStatus.LOADED) {

      // get the upper left corner of the extent
      Point2D minScreenPoint = new Point2D(50, 50);

      // get the lower right corner of the extent
      Point2D maxScreenPoint = new Point2D(mapView.getWidth() - 50, mapView.getHeight() - 50);

      // convert the screen points to map points
      Point minPoint = mapView.screenToLocation(minScreenPoint);
      Point maxPoint = mapView.screenToLocation(maxScreenPoint);

      // use these points to define and create an envelope
      if (minPoint != null && maxPoint != null) {
        Envelope geodatabaseExtentEnvelope = new Envelope(minPoint, maxPoint);

        // update the graphics
        geodatabaseExtentGraphics.setGeometry(geodatabaseExtentEnvelope);
      }
    }
  }

  /**
   * Shows a message in an alert dialog.
   *
   * @param title   title of alert
   * @param message message to display
   */
  private void displayMessage(String title, String message) {
    Alert dialog = new Alert(Alert.AlertType.INFORMATION);
    dialog.setHeaderText(title);
    dialog.setContentText(message);
    dialog.showAndWait();
  }

  /**
   * Shows the progress of a job.
   *
   * @param job the job of which to show the progress
   */
  private void showJobProgress(Job job) {
    // show the progress bar
    progressBar.setProgress(0.0);
    progressBar.setVisible(true);

    // update the progress bar as the job progresses
    job.addProgressChangedListener(() -> {
      int progress = job.getProgress();
      progressBar.setProgress((double) progress / 100.0);
    });

    // hide the progress bar when the job is finished
    job.addJobDoneListener(() -> progressBar.setVisible(false));
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
