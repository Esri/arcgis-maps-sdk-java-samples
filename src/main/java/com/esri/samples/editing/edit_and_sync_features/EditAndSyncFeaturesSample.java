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

import com.esri.arcgisruntime.concurrent.Job;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Geodatabase;
import com.esri.arcgisruntime.data.GeodatabaseFeatureTable;
import com.esri.arcgisruntime.data.TileCache;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.tasks.geodatabase.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class EditAndSyncFeaturesSample extends Application {

    private MapView mapView;
    private ArcGISMap map;
    private final AtomicInteger replica = new AtomicInteger();
    private EditAndSyncFeaturesSample.EditState currentEditState;
    private GraphicsOverlay graphicsOverlay;
    private ProgressBar progressBar;
    private Geodatabase geodatabase;
    private GeodatabaseSyncTask geodatabaseSyncTask;
    private Button geodatabaseButton;

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

            // set edit state t not ready until geodatabase job has completed successfully
            currentEditState = EditState.NOTREADY;

            // create a map view and add a map
            mapView = new MapView();
            // create a graphics overlay and symbol to mark the extent
            graphicsOverlay = new GraphicsOverlay();
            mapView.getGraphicsOverlays().add(graphicsOverlay);

            // load cached tiles
            loadTileCache();

            // create a control panel
            VBox controlsVBox = new VBox(6);
            controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY,
                    Insets.EMPTY)));
            controlsVBox.setPadding(new Insets(10.0));
            controlsVBox.setMaxSize(180, 20);
            controlsVBox.getStyleClass().add("panel-region");

            // create button for user interaction
            geodatabaseButton = new Button("Generate Geodatabase");
            geodatabaseButton.setMaxWidth(Double.MAX_VALUE);
            controlsVBox.getChildren().add(geodatabaseButton);

            // create progress bar
            progressBar = new ProgressBar();
            progressBar.setProgress(0.0);
            progressBar.setVisible(false);

            // add listener to handle generate/sync geodatabase button
            geodatabaseButton.setOnAction(e -> {
                if (currentEditState == EditState.NOTREADY) {
                    // update button text and disable button
                    geodatabaseButton.setDisable(true);
                    geodatabaseButton.setText("Generating Geodatabase...");
                    
                    // show the extent of the geodatabase using a graphics
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

            // add map view, controlsVBox and progressBar to stack pane
            stackPane.getChildren().addAll(mapView, controlsVBox, progressBar);
            StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
            StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));
            StackPane.setAlignment(progressBar, Pos.BOTTOM_CENTER);
            StackPane.setMargin(progressBar, new Insets(0, 0, 50, 0));

        } catch (Exception e) {
            // on any error, display the stack trace
            e.printStackTrace();
        }
    }

    /**
     * Load local tile cache.
     */
    private void loadTileCache() {

        // use local tile package for the base map
        TileCache sanFranciscoTileCache = new TileCache("samples-data/sanfrancisco/SanFrancisco.tpk");
        ArcGISTiledLayer tiledLayer = new ArcGISTiledLayer(sanFranciscoTileCache);
        Basemap basemap = new Basemap(tiledLayer);
        map = new ArcGISMap(basemap);
        mapView.setMap(map);
    }

    /**
     * Generates a local geodatabase and sets it to the map.
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
                                    displayMessage("Geodatabase Loaded",null);
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
    private void syncGeodatabase(){

        // create parameters for the sync task
        SyncGeodatabaseParameters syncGeodatabaseParameters = new SyncGeodatabaseParameters();
        syncGeodatabaseParameters.setSyncDirection(SyncGeodatabaseParameters.SyncDirection.BIDIRECTIONAL);
        syncGeodatabaseParameters.setRollbackOnFailure(false);

        // get the layer ID for each feature table in the geodatabase, then add to the sync job
        for (GeodatabaseFeatureTable geodatabaseFeatureTable : geodatabase.getGeodatabaseFeatureTables()){
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
        syncGeodatabaseJob.addJobDoneListener(()->{
            if (syncGeodatabaseJob.getStatus() == Job.Status.SUCCEEDED){
                displayMessage("Database Sync Complete", null);
                geodatabaseButton.setDisable(true);
            } else {
                displayMessage("Database did not sync correctly!", syncGeodatabaseJob.getError().getMessage());
            }
        });
    }

    // TODO: enable selecting/moving features


    /**
     * Show a progress bar
     *
     * @param job the job to show progress of
     */
    private void showProgress(Job job) {

        // disable the button while the job runs
        geodatabaseButton.setDisable(true);

        // show progress
        progressBar.setVisible(true);
        job.addProgressChangedListener(() -> {
            int progress = job.getProgress();
            progressBar.setProgress((double) progress / 100.0);
        });

        // re-enable button, hide progress bar on complete
        job.addJobDoneListener(()->{
            if (job.getStatus() == Job.Status.SUCCEEDED){
                geodatabaseButton.setDisable(false);
                Platform.runLater(() -> progressBar.setVisible(false));
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
