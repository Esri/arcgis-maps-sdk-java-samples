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
package com.esri.samples.localserver;

import java.net.URISyntaxException;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.datasource.arcgis.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.localserver.LocalFeatureService;
import com.esri.arcgisruntime.localserver.LocalServer;
import com.esri.arcgisruntime.localserver.LocalServerStatus;
import com.esri.arcgisruntime.localserver.LocalService.StatusChangedEvent;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;

public class LocalServerFeatureLayer extends Application {

  private static final int APPLICATION_WIDTH = 800;

  private ArcGISMap map;
  private MapView mapView;
  private LocalServer server;
  private LocalFeatureService featureService;
  private ProgressBar featureLayerProgress;

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
      featureLayerProgress = new ProgressBar(-100.0);
      featureLayerProgress.setMaxWidth(APPLICATION_WIDTH / 4);

      // create local server
      server = LocalServer.INSTANCE;
      // listen for the status of the local server to change
      server.addStatusChangedListener(status -> {
        if (status.getNewStatus() == LocalServerStatus.STARTED) {
          try {
            String featureServiceURL = Paths.get(getClass().getResource("/PointsofInterest.mpk").toURI()).toString();
            featureService = new LocalFeatureService(featureServiceURL);
            featureService.addStatusChangedListener(this::addLocalFeatureLayer);
            featureService.startAsync();
          } catch (URISyntaxException e) {
            System.out.println("Failed to find mpk file. " + e.getMessage());
          }
        }
      });
      server.startAsync();

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

    } else if (status.getNewStatus() == LocalServerStatus.STOPPED) {
      // if feature layer is stopped then stop the server
      server.stopAsync();
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() throws Exception {

    if (featureService != null && featureService.getStatus() == LocalServerStatus.STARTED) {
      // stop feature service if it is running
      featureService.stopAsync();
    } else if (server != null && server.getStatus() == LocalServerStatus.STARTED) {
      // if server is only thing running stop it
      server.stopAsync();
    }

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
