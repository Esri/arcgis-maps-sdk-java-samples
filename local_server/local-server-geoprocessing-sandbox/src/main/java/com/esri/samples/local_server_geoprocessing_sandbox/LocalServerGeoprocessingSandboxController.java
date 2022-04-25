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

package com.esri.samples.local_server_geoprocessing_sandbox;

import java.io.File;
import java.util.Map;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.ServiceGeodatabase;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.Viewpoint;

import com.esri.arcgisruntime.mapping.view.LayerSceneProperties;
import com.esri.arcgisruntime.mapping.view.SceneView;

import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingFeatures;
import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingString;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.Job;

import com.esri.arcgisruntime.layers.ArcGISTiledLayer;

import com.esri.arcgisruntime.localserver.LocalGeoprocessingService;
import com.esri.arcgisruntime.localserver.LocalGeoprocessingService.ServiceType;
import com.esri.arcgisruntime.localserver.LocalServer;
import com.esri.arcgisruntime.localserver.LocalServerStatus;

import com.esri.arcgisruntime.mapping.BasemapStyle;

import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingJob;
import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingParameter;
import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingParameters;
import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingTask;


public class LocalServerGeoprocessingSandboxController {

  @FXML
  private TextField txtInterval;
  @FXML
  private Button btnGenerate;
  @FXML
  private Button btnClear;
  @FXML
  private ProgressBar progressBar;
  @FXML
  private SceneView sceneView;

  private ArcGISTiledLayer tiledLayer; // keep loadable in scope to avoid garbage collection
  private GeoprocessingTask gpTask;
  private LocalGeoprocessingService localGPService;

  private static LocalServer server;

  /**
   * Called after FXML loads. Sets up scene and map and configures property bindings.
   */
  public void initialize() {

    try {
      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a map with the light gray basemap style
      ArcGISScene scene = new ArcGISScene(BasemapStyle.ARCGIS_LIGHT_GRAY);

      // set the map to the map view
      sceneView.setArcGISScene(scene);

      // check that local server install path can be accessed
      System.out.println(LocalServer.INSTANCE.getInstallPath());

      if (LocalServer.INSTANCE.checkInstallValid()) {
        progressBar.setVisible(true);
        server = LocalServer.INSTANCE;
        System.out.println("Temp data path " + server.getTempDataPath());
        // start the local server
        server.addStatusChangedListener(status -> {
          if (server.getStatus() == LocalServerStatus.STARTED) {
            try {
//              String gpServiceURL = new File(System.getProperty("data.dir"), "./samples-data/local_server/interpolate" +
//                ".gpkx").getAbsolutePath();
              String gpServiceURL = new File(System.getProperty("data.dir"), "./samples-data/local_server/create_elevation_profile_model.gpkx").getAbsolutePath();

              // need map server result to add contour lines to map
              localGPService =
                new LocalGeoprocessingService(gpServiceURL, ServiceType.ASYNCHRONOUS_SUBMIT_WITH_MAP_SERVER_RESULT);
            } catch (Exception e) {
              e.printStackTrace();
            }

            localGPService.addStatusChangedListener(s -> {
              // create geoprocessing task once local geoprocessing service is started 
              if (s.getNewStatus() == LocalServerStatus.STARTED) {
                // add `/NameOfScript/Model` to use contour geoprocessing tool 
                gpTask = new GeoprocessingTask(localGPService.getUrl() + "/CreateElevationProfileModel");
//                gpTask = new GeoprocessingTask(localGPService.getUrl() + "/Model");

                System.out.println(localGPService.getUrl());
                btnClear.disableProperty().bind(btnGenerate.disabledProperty().not());
                btnGenerate.setDisable(false);
                progressBar.setVisible(false);
              }
            });
            localGPService.startAsync();
          } else if (server.getStatus() == LocalServerStatus.FAILED) {
            showMessage("Local Geoprocessing Load Error", "Local Geoprocessing Failed to load.");
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
    System.out.println("gpParameters: " + gpParameters.getInputs());

    final Map<String, GeoprocessingParameter> inputs = gpParameters.getInputs();
    System.out.println("Values " + inputs.values());
//    double interval = Double.parseDouble(txtInterval.getText());
    var inputRasterPath = ("C:\\Users\\rach9955\\Downloads\\arran-lidar-data\\arran-lidar-data\\MergedArranRasters" +
      ".tif");
    // input raster data
    // name of input parameter, input type (geoprocessing raster)
//    inputs.put("MergedArranRasters_tif", new GeoprocessingRaster(inputRasterPath, "tif")); // just the string, GeoprocessingString
    inputs.put("Input_Raster", new GeoprocessingString(inputRasterPath)); // just the string, GeoprocessingString

    // input line of section path
    // name of input parameter, json url
//    inputs.put("Profile", new GeoprocessingFeatures("C:\\Users\\rach9955\\Documents\\ArcGIS\\Projects" +
//      "\\ArranCrossSection\\Profile_FeaturesToJSON.json"));
    inputs.put("Input_Polyline", new GeoprocessingFeatures("C:\\Users\\rach9955\\Documents\\ArcGIS\\Projects" +
      "\\ArranCrossSection\\Profile_FeaturesToJSON.json"));

    // adds contour lines to map
    GeoprocessingJob gpJob = gpTask.createJob(gpParameters);

    gpJob.addJobDoneListener(() -> {

      System.out.println("GP Job status: " + gpJob.getStatus());
      if (gpJob.getStatus() == Job.Status.SUCCEEDED) {

//        GeoprocessingResult geoprocessingResult = gpJob.getResult(); // gets a collection of outputs, can get 
        // parameter from this. name of the output will be the name in the model builder

        // creating map image url from local geoprocessing service url
//        GeoprocessingFeatures resultFeatures = (GeoprocessingFeatures) geoprocessingResult.getOutputs().get(
//          "XYZ_Profile");
//        Map<String, GeoprocessingParameter> outputs = geoprocessingResult.getOutputs();
//
//        System.out.println("Output size " + geoprocessingResult.getOutputs().size()); // Returns: 1
//        System.out.println("Contains XYZ Profile? " + outputs.containsKey("XYZ_Profile")); // Returns: true
//        System.out.println("Can fetch output features? " + resultFeatures.canFetchOutputFeatures()); // Returns: true

        String serviceUrl = localGPService.getUrl();
        String mapServerUrl = serviceUrl.replace("GPServer", "MapServer/jobs/" + gpJob.getServerJobId());

        System.out.println("service url: " + serviceUrl);
        System.out.println("Server Job ID: " + gpJob.getServerJobId());
        System.out.println("Map server url: " + mapServerUrl);

        ServiceGeodatabase serviceGeodatabase = new ServiceGeodatabase(mapServerUrl);
        serviceGeodatabase.addDoneLoadingListener(() -> {

          FeatureTable featureTable = serviceGeodatabase.getTable(0);
          FeatureLayer featureLayer = new FeatureLayer(featureTable);
          featureLayer.loadAsync();
          featureLayer.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.ABSOLUTE);

          featureLayer.addDoneLoadingListener(() -> {

            sceneView.getArcGISScene().getOperationalLayers().add(featureLayer); // purple profile line
            sceneView.setViewpoint(new Viewpoint(featureLayer.getFullExtent()));
          
          });
          
          btnGenerate.setDisable(true);

        });
        serviceGeodatabase.loadAsync();

      } else {
        Alert dialog = new Alert(AlertType.ERROR);
        dialog.setHeaderText("Geoprocess Job Fail");
        dialog.setContentText("Error: " + gpJob.getError().getAdditionalMessage());
        System.out.println("Error: " + gpJob.getError().getAdditionalMessage());
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

    if (sceneView.getArcGISScene().getOperationalLayers().size() > 1) {
      sceneView.getArcGISScene().getOperationalLayers().remove(1);
      btnGenerate.setDisable(false);
    }
  }

  private void showMessage(String title, String message) {

    Platform.runLater(() -> {
      Alert dialog = new Alert(AlertType.INFORMATION);
      dialog.initOwner(sceneView.getScene().getWindow());
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

    if (sceneView != null) {
      sceneView.dispose();
    }
  }
}
