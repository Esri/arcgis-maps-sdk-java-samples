/*
 * Copyright 2016 Esri.
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

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import com.esri.arcgisruntime.concurrent.Job;
import com.esri.arcgisruntime.data.TileCache;
import com.esri.arcgisruntime.geoprocessing.GeoprocessingDouble;
import com.esri.arcgisruntime.geoprocessing.GeoprocessingFeatures;
import com.esri.arcgisruntime.geoprocessing.GeoprocessingJob;
import com.esri.arcgisruntime.geoprocessing.GeoprocessingParameter;
import com.esri.arcgisruntime.geoprocessing.GeoprocessingParameters;
import com.esri.arcgisruntime.geoprocessing.GeoprocessingTask;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.localserver.LocalGeoprocessingService;
import com.esri.arcgisruntime.localserver.LocalGeoprocessingService.ServiceType;
import com.esri.arcgisruntime.localserver.LocalServer;
import com.esri.arcgisruntime.localserver.LocalServerStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;

public class LocalServerGeoprocessingController {

  @FXML private TextField txtInterval;
  @FXML private Button btnGenerate;
  @FXML private Button btnClear;

  @FXML private MapView mapView;
  private LocalGeoprocessingService gpService;
  private GeoprocessingTask gpTask;

  private static final LocalServer server = LocalServer.INSTANCE;

  /**
   * Called after FXML loads. Sets up scene and map and configures property bindings.
   */
  public void initialize() {

    try {
      // create a view with a map and basemap
      ArcGISMap map = new ArcGISMap(Basemap.createLightGrayCanvas());
      mapView.setMap(map);

      //
      String rasterURL = Paths.get(getClass().getResource("/local_server/RasterHillshade.tpk").toURI()).toString();
      TileCache tileCache = new TileCache(rasterURL);
      ArcGISTiledLayer tiledLayer = new ArcGISTiledLayer(tileCache);
      tiledLayer.loadAsync();
      tiledLayer.addDoneLoadingListener(() -> {
        mapView.setViewpointGeometryAsync(tiledLayer.getFullExtent());
      });
      map.getOperationalLayers().add(tiledLayer);

      // listen for the status of the local server to change
      server.addStatusChangedListener(status -> {
        if (status.getNewStatus() == LocalServerStatus.STARTED) {
          try {
            String gpServiceURL = Paths.get(getClass().getResource("/local_server/Contour.gpk").toURI()).toString();
            gpService = new LocalGeoprocessingService(gpServiceURL, ServiceType.AsynchronousSubmitWithMapServerResult);
            gpService.addStatusChangedListener(s -> {
              System.out.println("Service Status: " + s.getNewStatus());
              if (s.getNewStatus() == LocalServerStatus.STARTED) {
                btnGenerate.setDisable(false);
                System.out.println("URL: " + gpService.getUrl() + "/Contour");
                gpTask = new GeoprocessingTask(gpService.getUrl() + "/Contour");
              }
            });
            gpService.startAsync();

          } catch (URISyntaxException e) {
            System.out.println("Failed to find gpk file. " + e.getMessage());
          }
        }
      });
      server.startAsync();

    } catch (Exception e) {
      // on any exception, print the stack trace
      e.printStackTrace();
    }
  }

  @FXML
  protected void handleGenerateContours(ActionEvent event) {

    double interval = Double.parseDouble(txtInterval.getText());
    GeoprocessingParameters gpParameters = new GeoprocessingParameters(
        GeoprocessingParameters.ExecutionType.SYNCHRONOUS_EXECUTE);

    final Map<String, GeoprocessingParameter> inputs = gpParameters.getInputs();
    inputs.put("Interval", new GeoprocessingDouble(interval));

    GeoprocessingJob gpJob = gpTask.run(gpParameters);
    gpJob.addJobChangedListener(() -> {
      System.out.println("Job Status: " + gpJob.getStatus());
    });

    gpJob.addJobDoneListener(() -> {
      if (gpJob.getStatus() == Job.Status.SUCCEEDED) {
        Map<String, GeoprocessingParameter> outputs = gpJob.getResult().getOutputs();
        System.out.println("Outputs: " + outputs.size());

        System.out.println("Parameter: " + outputs.get("Contour_Result"));

        GeoprocessingFeatures features = (GeoprocessingFeatures) outputs.get("Contour_Result");
        System.out.println("URL: " + features.getUrl());
        System.out.println("URi: " + features.getUri());
        //        gpJob.getResult().getMapImageLayer();

        //        System.out.println("Job Url: " + gpJob.getUri());
        // create a map image layer using url
        //        ArcGISMapImageLayer imageLayer = new ArcGISMapImageLayer(gpJob.getUri());
        // set viewpoint once layer has loaded
        //        imageLayer.addDoneLoadingListener(() -> {
        //          System.out.println("Image layer done");
        //          mapView.getMap().getOperationalLayers().add(imageLayer);
        //          //              Platform.runLater(() -> imageLayerProgress.setVisible(false));
        //        });
        //        imageLayer.loadAsync();
      } else {
        System.out.println("Error: " + gpJob.getError().getAdditionalMessage());
      }
    });

    gpJob.start();
  }

  @FXML
  protected void handleClearResults(ActionEvent event) {
  }

  /**
   * Stops the animation and disposes of application resources.
   */
  void terminate() {
    if (mapView != null) {
      mapView.dispose();
    }
  }
}
