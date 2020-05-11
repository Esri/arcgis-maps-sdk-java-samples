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
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.concurrent.Job;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.VectorTileCache;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.ArcGISVectorTiledLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.ItemResourceCache;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import com.esri.arcgisruntime.security.AuthenticationManager;
import com.esri.arcgisruntime.security.DefaultAuthenticationChallengeHandler;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.tasks.vectortilecache.ExportVectorTilesJob;
import com.esri.arcgisruntime.tasks.vectortilecache.ExportVectorTilesParameters;
import com.esri.arcgisruntime.tasks.vectortilecache.ExportVectorTilesResult;
import com.esri.arcgisruntime.tasks.vectortilecache.ExportVectorTilesTask;

public class ExportVectorTilesSample extends Application {

  private MapView mapView;
  private PortalItem portalItem; // keeps loadable in scope to avoid garbage collection

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

      // set the map to the map view
      mapView = new MapView();

      // authenticate with an organization account on arcgis.com
      AuthenticationManager.setAuthenticationChallengeHandler(new DefaultAuthenticationChallengeHandler());

      // get the portal item of the vector tile service
      Portal portal = new Portal("http://www.arcgis.com", true);
      portalItem = new PortalItem(portal, "86f556a2d1fd468181855a35e344567f");
      portalItem.addDoneLoadingListener(() -> {
        if (portalItem.getLoadStatus() == LoadStatus.LOADED) {
          // loading the vector tiled layer will invoke the authentication challenge
          ArcGISVectorTiledLayer vectorTiledLayer = new ArcGISVectorTiledLayer(portalItem);
          ArcGISMap map = new ArcGISMap(new Basemap(vectorTiledLayer));
          mapView.setMap(map);
        } else {
          Alert alert = new Alert(Alert.AlertType.ERROR, portalItem.getLoadError().getCause().getMessage());
          alert.show();
        }
      });
      portalItem.loadAsync();

      // create a graphics overlay for the map view
      GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // create a graphic to show a box around the tiles we want to download
      Graphic downloadArea = new Graphic();
      graphicsOverlay.getGraphics().add(downloadArea);
      SimpleLineSymbol simpleLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFFFF0000, 2);
      downloadArea.setSymbol(simpleLineSymbol);

      // update the box whenever the viewpoint changes
      mapView.addViewpointChangedListener(viewpointChangedEvent -> {
        if (mapView.getMap().getLoadStatus() == LoadStatus.LOADED) {
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

      // create button to export tiles
      Button exportTilesButton = new Button("Export Vector Tiles");

      // create progress bar to show task progress
      ProgressBar progressBar = new ProgressBar();
      progressBar.setProgress(0.0);
      progressBar.setVisible(false);

      // when the button is clicked, export the tiles to a temporary file
      exportTilesButton.setOnAction(e -> {
        try {
          File vtpkFile = File.createTempFile("tiles", ".vtpk");
          File resDir = Files.createTempDirectory("StyleItemResources").toFile();
          progressBar.setVisible(true);
          Layer layer = mapView.getMap().getBasemap().getBaseLayers().get(0);
          double maxScale = layer.getMaxScale();
          ExportVectorTilesTask task = new ExportVectorTilesTask((PortalItem) layer.getItem());
          ListenableFuture<ExportVectorTilesParameters> createParams = task
              .createDefaultExportVectorTilesParametersAsync(downloadArea.getGeometry(), maxScale);
          createParams.addDoneListener(() -> {
            try {
              ExportVectorTilesParameters params = createParams.get();
              ExportVectorTilesJob job = task.exportVectorTiles(params, vtpkFile.getAbsolutePath(), resDir
                  .getAbsolutePath());
              job.start();
              job.addProgressChangedListener(() -> progressBar.setProgress(job.getProgress() / 100.0));
              job.addJobDoneListener(() -> {
                if (job.getStatus() == Job.Status.SUCCEEDED) {
                  // show preview of exported tiles in alert
                  ExportVectorTilesResult tilesResult = job.getResult();
                  VectorTileCache tileCache = tilesResult.getVectorTileCache();
                  ItemResourceCache resourceCache = tilesResult.getItemResourceCache();
                  Alert preview = new Alert(Alert.AlertType.INFORMATION);
                  preview.initOwner(mapView.getScene().getWindow());
                  preview.setTitle("Preview");
                  preview.setHeaderText("Exported tiles to " + tileCache.getPath() + "\nExported resources to " +
                      resourceCache.getPath());
                  MapView mapPreview = new MapView();
                  mapPreview.setMinSize(400, 400);
                  ArcGISVectorTiledLayer tiledLayerPreview = new ArcGISVectorTiledLayer(tileCache, resourceCache);
                  ArcGISMap previewMap = new ArcGISMap(new Basemap(tiledLayerPreview));
                  mapPreview.setMap(previewMap);
                  preview.getDialogPane().setContent(mapPreview);
                  preview.show();
                } else {
                  Alert alert = new Alert(Alert.AlertType.ERROR, job.getError().getAdditionalMessage());
                  alert.show();
                }
                Platform.runLater(() -> progressBar.setVisible(false));
              });
            } catch (InterruptedException | ExecutionException ex) {
              Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
              alert.show();
              progressBar.setVisible(false);
              progressBar.setProgress(0);
            }
          });
        } catch (IOException ex) {
          Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to create temporary file");
          alert.show();
        }
      });

      // add the map view, button, and progress bar to stack pane
      stackPane.getChildren().addAll(mapView, exportTilesButton, progressBar);
      StackPane.setAlignment(exportTilesButton, Pos.TOP_LEFT);
      StackPane.setAlignment(progressBar, Pos.TOP_RIGHT);
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
