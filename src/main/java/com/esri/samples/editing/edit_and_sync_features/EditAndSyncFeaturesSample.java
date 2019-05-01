/* Copyright 2017 Esri
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
import com.esri.arcgisruntime.tasks.geodatabase.GenerateGeodatabaseJob;
import com.esri.arcgisruntime.tasks.geodatabase.GenerateGeodatabaseParameters;
import com.esri.arcgisruntime.tasks.geodatabase.GeodatabaseSyncTask;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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

    @Override
    public void start(Stage stage) {

        try{
            // create stack pane and application scene
            StackPane stackPane = new StackPane();
            Scene scene = new Scene(stackPane);

            // set title, size and add scene to stage
            stage.setTitle("Edit and Sync Features Sample");
            stage.setWidth(800);
            stage.setHeight(700);
            stage.setScene(scene);
            stage.show();

            // set edit state t not ready until geodatabase job has completed successfuly
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
            controlsVBox.setMaxSize(180,20);
            controlsVBox.getStyleClass().add("panel-region");

            // create button for user interaction
            Button geodatabaseButton = new Button("Sync Geodatabase");
            geodatabaseButton.setMaxWidth(Double.MAX_VALUE);

            // add button to the controlsVBox
            controlsVBox.getChildren().add(geodatabaseButton);

            // set edit state to not ready until geodatabase job has completed successfully


            // TODO: make button generate or sync
            // add listener to handle generate/sync geodatabase button
            geodatabaseButton.setOnAction(e -> {
                if (currentEditState == EditState.NOTREADY) {
                    generateGeodatabase();
                } else if (currentEditState == EditState.READY) {
                 //   syncGeodatabase();
                }
            });


            // add map view to stack pane
            stackPane.getChildren().addAll(mapView, controlsVBox);
            StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
            StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));

        } catch (Exception e) {
            // on any error, display the stack trace
            e.printStackTrace();
        }
    }

    public void generateGeodatabase(){
         // define geodatabase sync task
        GeodatabaseSyncTask geodatabaseSyncTask = new GeodatabaseSyncTask("https://sampleserver6.arcgisonline.com/arcgis/rest/services/Sync/WildfireSync/FeatureServer");
        geodatabaseSyncTask.loadAsync();
        geodatabaseSyncTask.addDoneLoadingListener(() -> {
            // show the extend to the geodatabase using a graphics
            final SimpleLineSymbol boundarySymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF0000FF, 5);
            final Envelope extent = mapView.getVisibleArea().getExtent();
            Graphic boundary = new Graphic(extent, boundarySymbol);
            graphicsOverlay.getGraphics().add(boundary);

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

                    // TODO: make loading bar and show progress
                    geodatabaseJob.addProgressChangedListener(() -> {
                        int progress = geodatabaseJob.getProgress();
                    });

                    // get geodatabase when done
                    geodatabaseJob.addJobDoneListener(() -> {
                        if (geodatabaseJob.getStatus() == Job.Status.SUCCEEDED) {
                            Geodatabase geodatabase = geodatabaseJob.getResult();
                            geodatabase.loadAsync();
                            geodatabase.addDoneLoadingListener(() -> {
                                if (geodatabase.getLoadStatus() == LoadStatus.LOADED) {
                                    // add the geodatabase FeatureTables to the map as a FeatureLayer
                                    geodatabase.getGeodatabaseFeatureTables().forEach(geodatabaseFeatureTable -> {
                                        // TODO: why is the adding of the FT to the map not within a done listener?
                                        geodatabaseFeatureTable.loadAsync();
                                        map.getOperationalLayers().add(new FeatureLayer(geodatabaseFeatureTable));
                                    });
                                    System.out.println("loaded");
                                } else {
                                    displayMessage("Error loading geodatabase", geodatabase.getLoadError().getMessage());
                                }
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
        System.out.println("button pressed");
    }


    // TODO: write method syncGeodatabase()
//            1. To sync changes between the local and web geodatabases:
//            1. Define `SyncGeodatabaseParameters` including setting the `SyncGeodatabaseParameters.SyncDirection`.
//            1. Create a `SyncGeodatabaseJob` from `GeodatabaseSyncTask` using `.syncGeodatabaseAsync(...)` passing the `SyncGeodatabaseParameters` and `Geodatabase` as arguments.
//            1. Start the `SyncGeodatabaseJob`.

    //

    /*
     * Load local tile cache.
     */
    private void loadTileCache(){
        // use local tile package for the base map
        TileCache sanFranciscoTileCache = new TileCache("samples-data/sanfrancisco/SanFrancisco.tpk");
        ArcGISTiledLayer tiledLayer = new ArcGISTiledLayer(sanFranciscoTileCache);
        Basemap basemap = new Basemap(tiledLayer);
        map = new ArcGISMap(basemap);
        mapView.setMap(map);
    }

    /**
     * Shows a message in an alert dialog.
     *
     * @param title title of alert
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

    private enum EditState{ // enumeration to track editing of points
        NOTREADY,           // Geodatabase has not yet been generated
        EDITING,            // A feature is in the process of being moved
        READY               // The Geodatabase is ready for synchronisation or further edits
    }
}
