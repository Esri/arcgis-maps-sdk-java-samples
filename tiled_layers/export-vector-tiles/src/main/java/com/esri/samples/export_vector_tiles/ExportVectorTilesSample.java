/*
 * Copyright 2018 Esri.
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

package com.esri.samples.export_vector_tiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ExecutionException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.Job;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.VectorTileCache;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.ArcGISVectorTiledLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.ItemResourceCache;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.tasks.vectortilecache.ExportVectorTilesParameters;
import com.esri.arcgisruntime.tasks.vectortilecache.ExportVectorTilesResult;
import com.esri.arcgisruntime.tasks.vectortilecache.ExportVectorTilesTask;

public class ExportVectorTilesSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Export Vector Tiles Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a new map with the streets night style (a vector tile layer)
      ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_STREETS_NIGHT);

      // set the map to the map view
      mapView = new MapView();
      // set the map to the mapview
      mapView.setMap(map);

      // set the viewpoint over Redlands, California, USA
      mapView.setViewpointAsync(new Viewpoint(34.049, -117.181, 1e4));

      // create a graphics overlay for the map view
      var graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // create a graphic to show a red outline square around the tiles to be downloaded
      Graphic downloadArea = new Graphic();
      graphicsOverlay.getGraphics().add(downloadArea);
      var simpleLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 2);
      downloadArea.setSymbol(simpleLineSymbol);

      // create button to export tiles
      Button exportVectorTilesButton = new Button("Export Vector Tiles");
      exportVectorTilesButton.setDisable(true);

      // create progress bar to show task progress
      var progressBar = new ProgressBar(0.0);
      progressBar.setVisible(false);

      // update the square whenever the viewpoint changes
      mapView.addViewpointChangedListener(viewpointChangedEvent -> {
        if (map.getLoadStatus() == LoadStatus.LOADED) {
          // upper left corner of the downloaded tile cache area
          Point2D minScreenPoint = new Point2D(50, 50);
          // lower right corner of the downloaded tile cache area
          Point2D maxScreenPoint = new Point2D(mapView.getWidth() - 50, mapView.getHeight() - 50);
          // convert screen points to map points
          Point minPoint = mapView.screenToLocation(minScreenPoint);
          Point maxPoint = mapView.screenToLocation(maxScreenPoint);
          // use the points to define and return an envelope
          if (minPoint != null && maxPoint != null) {
            Envelope envelope = new Envelope(minPoint, maxPoint);
            downloadArea.setGeometry(envelope);
          }
        }
      });

      // when the map has loaded, create a vector tiled layer from it and export tiles
      map.addDoneLoadingListener(() -> {
        if (map.getLoadStatus() == LoadStatus.LOADED) {
          // enable the export tiles button
          exportVectorTilesButton.setDisable(false);

          // check that the layer from the basemap is a vector tiled layer
          var layer = map.getBasemap().getBaseLayers().get(0);
          if (layer instanceof ArcGISVectorTiledLayer) {
            ArcGISVectorTiledLayer vectorTiledLayer = (ArcGISVectorTiledLayer) layer;

            // when the button is clicked, export the tiles to a temporary file
            exportVectorTilesButton.setOnAction(e -> {
              try {
                // disable the button and show the progress bar
                exportVectorTilesButton.setDisable(true);
                progressBar.setVisible(true);

                // create temporary files for the .vtpk file and style item resources
                File vtpkFile = File.createTempFile("tiles", ".vtpk");
                File resDir = Files.createTempDirectory("StyleItemResources").toFile();
                vtpkFile.deleteOnExit();
                resDir.deleteOnExit();

                // create a new export vector tiles task
                var exportVectorTilesTask = new ExportVectorTilesTask(vectorTiledLayer.getUri());

                // create parameters for the export vector tiles job
                double mapScale = mapView.getMapScale();
                // the max scale parameter is set to 10% of the map's scale to limit the
                // number of tiles exported to within the vector tiled layer's max tile export limit
                ListenableFuture<ExportVectorTilesParameters> exportVectorTilesParametersFuture = exportVectorTilesTask
                  .createDefaultExportVectorTilesParametersAsync(downloadArea.getGeometry(), mapScale * 0.1);

                exportVectorTilesParametersFuture.addDoneListener(() -> {
                  try {
                    var exportVectorTilesParameters = exportVectorTilesParametersFuture.get();

                    // create a job with the parameters
                    var exportVectorTilesJob =
                      exportVectorTilesTask.exportVectorTiles(exportVectorTilesParameters, vtpkFile.getAbsolutePath(), resDir.getAbsolutePath());

                    // start the job and wait for it to finish
                    exportVectorTilesJob.start();
                    exportVectorTilesJob.addProgressChangedListener(() -> progressBar.setProgress(exportVectorTilesJob.getProgress() / 100.0));
                    exportVectorTilesJob.addJobDoneListener(() -> {

                      if (exportVectorTilesJob.getStatus() == Job.Status.SUCCEEDED) {
                        // show preview of exported tiles in alert
                        ExportVectorTilesResult tilesResult = exportVectorTilesJob.getResult();
                        VectorTileCache tileCache = tilesResult.getVectorTileCache();
                        ItemResourceCache resourceCache = tilesResult.getItemResourceCache();
                        Alert preview = new Alert(Alert.AlertType.INFORMATION);
                        preview.initOwner(mapView.getScene().getWindow());
                        preview.setTitle("Preview");
                        preview.setHeaderText("Exported tiles to " + tileCache.getPath() + "\nExported resources to " +
                          resourceCache.getPath());
                        MapView previewMapView = new MapView();
                        previewMapView.setMinSize(400, 400);
                        ArcGISVectorTiledLayer vectorTiledLayerPreview = new ArcGISVectorTiledLayer(tileCache, resourceCache);
                        ArcGISMap previewMap = new ArcGISMap(new Basemap(vectorTiledLayerPreview));
                        previewMapView.setMap(previewMap);
                        preview.getDialogPane().setContent(previewMapView);
                        preview.show();
                        // dispose of the preview mapview's resources
                        preview.setOnCloseRequest(event -> previewMapView.dispose());

                      } else {
                        new Alert(Alert.AlertType.ERROR, exportVectorTilesJob.getError().getMessage()).show();
                      }

                      // reset the UI
                      progressBar.setVisible(false);
                      progressBar.setProgress(0);
                      exportVectorTilesButton.setDisable(false);
                    });

                  } catch (InterruptedException | ExecutionException ex) {
                    new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
                    progressBar.setVisible(false);
                    progressBar.setProgress(0);
                  }
                });

              } catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to create temporary file");
                alert.show();
              }
            });
          }

        } else {
          new Alert(Alert.AlertType.ERROR, "Map could not be loaded").show();
        }

      });

      // add the map view, button, and progress bar to stack pane
      stackPane.getChildren().addAll(mapView, exportVectorTilesButton, progressBar);
      StackPane.setAlignment(exportVectorTilesButton, Pos.BOTTOM_CENTER);
      StackPane.setMargin(exportVectorTilesButton, new Insets(0, 0, 100, 0));
      StackPane.setAlignment(progressBar, Pos.BOTTOM_CENTER);
      StackPane.setMargin(progressBar, new Insets(0, 0, 80, 0));

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
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
