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

package com.esri.samples.localserver.local_server_geoprocessing;

import java.io.File;
import java.util.Map;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

import com.esri.arcgisruntime.concurrent.Job;
import com.esri.arcgisruntime.data.TileCache;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.localserver.LocalGeoprocessingService;
import com.esri.arcgisruntime.localserver.LocalGeoprocessingService.ServiceType;
import com.esri.arcgisruntime.localserver.LocalServer;
import com.esri.arcgisruntime.localserver.LocalServerStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingDouble;
import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingJob;
import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingParameter;
import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingParameters;
import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingTask;

public class LocalServerGeoprocessingController {

  @FXML private TextField txtInterval;
  @FXML private Button btnGenerate;
  @FXML private Button btnClear;
  @FXML private ProgressBar progressBar;
  @FXML private MapView mapView;

  private LocalGeoprocessingService localGPService;
  private GeoprocessingTask gpTask;

  private static LocalServer server;

  /**
   * Called after FXML loads. Sets up scene and map and configures property bindings.
   */
  public void initialize() {

    try {
      // create a view with a map and basemap
      ArcGISMap map = new ArcGISMap(Basemap.createLightGrayCanvas());
      mapView.setMap(map);

      //load tiled layer and zoom to location
      String rasterURL = new File("./samples-data/local_server/RasterHillshade.tpk").getAbsolutePath();
      TileCache tileCache = new TileCache(rasterURL);
      ArcGISTiledLayer tiledLayer = new ArcGISTiledLayer(tileCache);
      tiledLayer.loadAsync();
      tiledLayer.addDoneLoadingListener(() -> mapView.setViewpointGeometryAsync(tiledLayer.getFullExtent()));
      map.getOperationalLayers().add(tiledLayer);

      // check that local server install path can be accessed
      if (LocalServer.INSTANCE.checkInstallValid()) {
        progressBar.setVisible(true);
        server = LocalServer.INSTANCE;
        // start the local server
        server.addStatusChangedListener(status -> {
          if (server.getStatus() == LocalServerStatus.STARTED) {
            try {
              String gpServiceURL = new File("./samples-data/local_server/Contour.gpk").getAbsolutePath();
              // need map server result to add contour lines to map
              localGPService =
                  new LocalGeoprocessingService(gpServiceURL, ServiceType.ASYNCHRONOUS_SUBMIT_WITH_MAP_SERVER_RESULT);
            } catch (Exception e) {
              e.printStackTrace();
            }

            localGPService.addStatusChangedListener(s -> {
              // create geoprocessing task once local geoprocessing service is started 
              if (s.getNewStatus() == LocalServerStatus.STARTED) {
                // add `/Contour` to use contour geoprocessing tool 
                gpTask = new GeoprocessingTask(localGPService.getUrl() + "/Contour");
                btnClear.disableProperty().bind(btnGenerate.disabledProperty().not());
                btnGenerate.setDisable(false);
                progressBar.setVisible(false);
              }
            });
            localGPService.startAsync();
          } else if (server.getStatus() == LocalServerStatus.FAILED) {
            showMessage("Loval Geoprocessing Load Error", "Local Geoprocessing Failed to load.");
          }
        });
        server.startAsync();
      } else {
        showMessage("Local Server Load Error", "Local Server install path couldn't be located.");
      }

    } catch (Exception e) {
      // on any exception, print the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Creates a Map Image Layer that displays contour lines on the map using the interval the that is set.
   */
  @FXML
  protected void handleGenerateContours() {

    // tracking progress of creating contour map 
    progressBar.setVisible(true);
    // create parameter using interval set
    GeoprocessingParameters gpParameters = new GeoprocessingParameters(
        GeoprocessingParameters.ExecutionType.ASYNCHRONOUS_SUBMIT);

    final Map<String, GeoprocessingParameter> inputs = gpParameters.getInputs();
    double interval = Double.parseDouble(txtInterval.getText());
    inputs.put("Interval", new GeoprocessingDouble(interval));

    // adds contour lines to map
    GeoprocessingJob gpJob = gpTask.createJob(gpParameters);

    gpJob.addProgressChangedListener(() -> {
      progressBar.setProgress(((double) gpJob.getProgress()) / 100);
    });

    gpJob.addJobDoneListener(() -> {
      if (gpJob.getStatus() == Job.Status.SUCCEEDED) {
        // creating map image url from local groprocessing service url
        String serviceUrl = localGPService.getUrl();
        String mapServerUrl = serviceUrl.replace("GPServer", "MapServer/jobs/" + gpJob.getServerJobId());
        ArcGISMapImageLayer mapImageLayer = new ArcGISMapImageLayer(mapServerUrl);
        mapImageLayer.loadAsync();
        mapView.getMap().getOperationalLayers().add(mapImageLayer);
        btnGenerate.setDisable(true);
      } else {
        Alert dialog = new Alert(AlertType.ERROR);
        dialog.setHeaderText("Geoprocess Job Fail");
        dialog.setContentText("Error: " + gpJob.getError().getAdditionalMessage());
        dialog.showAndWait();
      }
      progressBar.setVisible(false);
    });
    gpJob.start();
  }

  /**
   * Removes contour lines from map if any are applied.
   */
  @FXML
  protected void handleClearResults() {

    if (mapView.getMap().getOperationalLayers().size() > 1) {
      mapView.getMap().getOperationalLayers().remove(1);
      btnGenerate.setDisable(false);
    }
  }

  private void showMessage(String title, String message) {

    Platform.runLater(() -> {
      Alert dialog = new Alert(AlertType.INFORMATION);
      dialog.setHeaderText(title);
      dialog.setContentText(message);
      dialog.showAndWait();

      Platform.exit();
    });
  }

  /**
   * Stops and releases all resources used in application.
   */
  void terminate() {

    if (mapView != null) {
      mapView.dispose();
    }
  }
}
