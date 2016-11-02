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

package com.esri.samples.localserver.local_server_feature_layer;

import java.io.File;
import java.util.concurrent.ExecutionException;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.localserver.LocalFeatureService;
import com.esri.arcgisruntime.localserver.LocalServer;
import com.esri.arcgisruntime.localserver.LocalServerStatus;
import com.esri.arcgisruntime.localserver.LocalService.StatusChangedEvent;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class LocalServerFeatureLayerSample extends Application {

  private static final int APPLICATION_WIDTH = 800;

  private ArcGISMap map;
  private MapView mapView;
  private LocalFeatureService featureService;
  private ProgressIndicator featureLayerProgress;
  
  private static final LocalServer server = LocalServer.INSTANCE;

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Local Server Feature Layer");
      stage.setWidth(APPLICATION_WIDTH);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a view with a map and basemap
      map = new ArcGISMap(Basemap.createStreets());
      mapView = new MapView();
      mapView.setMap(map);

      // track progress of loading feature layer to map
      featureLayerProgress = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);
      featureLayerProgress.setMaxWidth(30);

      // start local server
      server.startAsync();
      server.addStatusChangedListener(status -> {
        if (server.getStatus() == LocalServerStatus.STARTED) {
          // start feature service
          String featureServiceURL = new File("samples-data/local_server/PointsofInterest.mpk").getAbsolutePath();
          featureService = new LocalFeatureService(featureServiceURL);
          featureService.addStatusChangedListener(this::addLocalFeatureLayer);
          featureService.startAsync();
        }
      });

      // add view to application window
      stackPane.getChildren().addAll(mapView, featureLayerProgress);
      StackPane.setAlignment(featureLayerProgress, Pos.CENTER);
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Once the feature service starts, a feature layer is created from that service and added to the map.
   * <p>
   * When the feature layer is done loading the view will zoom to the location of were the features were added.
   * 
   * @param status status of feature service
   */
  private void addLocalFeatureLayer(StatusChangedEvent status) {

    // check that the feature service has started
    if (status.getNewStatus() == LocalServerStatus.STARTED) {
      // get the url of where feature service is located
      String url = featureService.getUrl() + "/0";
      // create a feature layer using the url
      ServiceFeatureTable featureTable = new ServiceFeatureTable(url);
      featureTable.loadAsync();
      FeatureLayer featureLayer = new FeatureLayer(featureTable);
      featureLayer.addDoneLoadingListener(() -> {
        // zoom to location were features were added
        mapView.setViewpoint(new Viewpoint(featureLayer.getFullExtent().getCenter(), 30000000));
        // turn off progress, feature layer is loaded
        Platform.runLater(() -> featureLayerProgress.setVisible(false));
      });
      featureLayer.loadAsync();
      // add feature layer to map
      map.getOperationalLayers().add(featureLayer);

    }
  }

  /**
   * Stops the local server and its running services.
   */
  private void stopLocalServer() throws InterruptedException, ExecutionException {

    if (server != null && server.getStatus() == LocalServerStatus.STARTED) {
      server.stopAsync().get();
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() throws Exception {

    stopLocalServer();

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
