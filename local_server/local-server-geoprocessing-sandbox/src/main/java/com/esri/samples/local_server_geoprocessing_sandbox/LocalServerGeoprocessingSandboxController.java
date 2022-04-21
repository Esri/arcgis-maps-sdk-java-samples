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
import java.util.concurrent.ExecutionException;

import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.FeatureSet;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceGeodatabase;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.internal.jni.CoreRequest;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LayerSceneProperties;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.symbology.ColorUtil;
import com.esri.arcgisruntime.symbology.FillSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingFeatures;
import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingRaster;
import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingResult;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.Job;
import com.esri.arcgisruntime.data.TileCache;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.localserver.LocalGeoprocessingService;
import com.esri.arcgisruntime.localserver.LocalGeoprocessingService.ServiceType;
import com.esri.arcgisruntime.localserver.LocalServer;
import com.esri.arcgisruntime.localserver.LocalServerStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingDouble;
import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingJob;
import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingParameter;
import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingParameters;
import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingTask;
import javafx.scene.paint.Color;

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
//              String gpServiceURL = new File(System.getProperty("data.dir"), "
//              ./samples-data/local_server/new_script_test.gpkx").getAbsolutePath();
              String gpServiceURL = new File(System.getProperty("data.dir"), "./samples-data/local_server/interpolate" +
                ".gpkx").getAbsolutePath();
//              String gpServiceURL = new File(System.getProperty("data.dir"), "
//              ./samples-data/local_server/Fivekm_buffer_result.gpkx").getAbsolutePath();
//              String gpServiceURL = new File(System.getProperty("data.dir"), "
//              ./samples-data/local_server/clip_buffer_munros_model_builder_convert_data_to_file_geodatabase.gpkx")
//              .getAbsolutePath();
//              String gpServiceURL = new File(System.getProperty("data.dir"), "
//              ./samples-data/local_server/contour_model_builder.gpkx").getAbsolutePath();
//              String gpServiceURL = new File(System.getProperty("data.dir"), "./samples-data/local_server/Contour
//              .gpkx").getAbsolutePath();
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
                gpTask = new GeoprocessingTask(localGPService.getUrl() + "/Model");
//                gpTask = new GeoprocessingTask(localGPService.getUrl() + "/Model");
//                gpTask = new GeoprocessingTask(localGPService.getUrl() + "/NewScriptTest");
//                gpTask = new GeoprocessingTask(localGPService.getUrl() + "/Contour");
//                gpTask = new GeoprocessingTask(localGPService.getUrl() + "/Model 1");
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


    GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
    graphicsOverlay.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.RELATIVE);
    // create an output graphics overlay to show the viewsheds as orange areas
    int fillColor = ColorUtil.colorToArgb(Color.rgb(226, 119, 40, 0.5));
    SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.X, fillColor, 50);
    SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, fillColor, 10);
    graphicsOverlay.setRenderer(new SimpleRenderer(lineSymbol));
    sceneView.getGraphicsOverlays().add(graphicsOverlay);

    // tracking progress of creating contour map 
    progressBar.setVisible(true);
    // create parameter using interval set

    GeoprocessingParameters gpParameters = new GeoprocessingParameters(
      GeoprocessingParameters.ExecutionType.ASYNCHRONOUS_SUBMIT);
    System.out.println("gpParameters: " + gpParameters.getInputs());

    final Map<String, GeoprocessingParameter> inputs = gpParameters.getInputs();
//    double interval = Double.parseDouble(txtInterval.getText());
    var mergedRasterTifUrl = ("C:\\Users\\rach9955\\Downloads\\arran-lidar-data\\arran-lidar-data\\MergedArranRasters" +
      ".tif");
    inputs.put("MergedArranRasters_tif", new GeoprocessingRaster(mergedRasterTifUrl, "tif"));

    inputs.put("Profile", new GeoprocessingFeatures("C:\\Users\\rach9955\\Documents\\ArcGIS\\Projects" +
      "\\ArranCrossSection\\Profile_FeaturesToJSON.json"));

    // adds contour lines to map
    GeoprocessingJob gpJob = gpTask.createJob(gpParameters);

    gpJob.addProgressChangedListener(() -> {
        System.out.println(gpJob.getProgress());
        progressBar.setProgress(((double) gpJob.getProgress()) / 100);
      }
    );

    gpJob.addJobDoneListener(() -> {

      System.out.println("GP Job status: " + gpJob.getStatus());
      if (gpJob.getStatus() == Job.Status.SUCCEEDED) {

        GeoprocessingResult geoprocessingResult = gpJob.getResult(); // gets a collection of outputs, can get 
        // parameter from this. name of the output will be the name in the model builder

        // creating map image url from local geoprocessing service url


        GeoprocessingFeatures resultFeatures = (GeoprocessingFeatures) geoprocessingResult.getOutputs().get(
          "XYZ_Profile");
        Map<String, GeoprocessingParameter> outputs = geoprocessingResult.getOutputs();

        System.out.println("Output size " + geoprocessingResult.getOutputs().size()); // Returns: 1
        System.out.println("Contains XYZ Profile? " + outputs.containsKey("XYZ_Profile")); // Returns: true
        System.out.println("Can fetch output features? " + resultFeatures.canFetchOutputFeatures()); // Returns: true

//        resultFeatures.fetchOutputFeaturesAsync().addDoneListener(() -> {
//
//          resultFeatures.getFeatures();
//          var feature = resultFeatures.getFeatures();
//          System.out.println(feature.getGeometryType()); // null thrown here
//
//        });
//
////         loop through the result features to get the viewshed geometries
//        FeatureSet featureSet = resultFeatures.getFeatures();
//        System.out.println("feature set to string" + featureSet.toString());
//        for (Feature feature : featureSet) {
//          // add the viewshed geometry as a graphic to the output graphics overlay
//          Graphic graphic = new Graphic(feature.getGeometry());
//          graphicsOverlay.getGraphics().add(graphic);
//        }

        String serviceUrl = localGPService.getUrl();
        System.out.println("service url: " + serviceUrl);
        String mapServerUrl = serviceUrl.replace("GPServer", "MapServer/jobs/" + gpJob.getServerJobId());
        String mapServerUrlFirstLayer = serviceUrl.replace("GPServer", "MapServer/jobs/" + gpJob.getServerJobId() +
          "/0");
        System.out.println("Server Job ID: " + gpJob.getServerJobId());
        System.out.println("Map server url: " + mapServerUrl);
        System.out.println("Map server first layer url: " + mapServerUrlFirstLayer);
        ArcGISMapImageLayer mapImageLayer = new ArcGISMapImageLayer(mapServerUrl);
        mapImageLayer.loadAsync();
//        sceneView.getArcGISScene().getOperationalLayers().add(mapImageLayer);

        ServiceGeodatabase serviceGeodatabase = new ServiceGeodatabase(mapServerUrl);
        serviceGeodatabase.loadAsync();
        serviceGeodatabase.addDoneLoadingListener(() -> {

          FeatureTable featureTable = serviceGeodatabase.getTable(0);
          FeatureLayer featureLayer = new FeatureLayer(featureTable);
          featureLayer.loadAsync();


          // create a query for the state that was entered
          QueryParameters query = new QueryParameters();
          query.setWhereClause("1=1");

          // search for the state feature in the feature table
          var tableQueryResult = featureTable.queryFeaturesAsync(query);
          
          tableQueryResult.addDoneListener(() -> {

            // get the result from the query
            FeatureQueryResult result = null;
            try {
              result = tableQueryResult.get();
            } catch (InterruptedException | ExecutionException e) {
              e.printStackTrace();
            }
            // if a state feature was found
            assert result != null;
            if (result.iterator().hasNext()) {
              // get state feature and zoom to it
              Feature feature = result.iterator().next();
              Envelope envelope = feature.getGeometry().getExtent();
              Graphic graphic = new Graphic(feature.getGeometry());
              graphicsOverlay.getGraphics().add(graphic);


              featureLayer.addDoneLoadingListener(() -> System.out.println(featureLayer.getLoadStatus()));
              sceneView.getArcGISScene().getOperationalLayers().add(featureLayer);
              System.out.println(featureLayer.getName());

            }
          });



          btnGenerate.setDisable(true);
//        if (mapImageLayer.getFullExtent() != null) {
//          sceneView.setViewpoint(mapImageLayer.getFullExtent());
//        }

        });
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
