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

package com.esri.samples.local_server_feature_layer;

import java.io.File;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.localserver.LocalFeatureService;
import com.esri.arcgisruntime.localserver.LocalServer;
import com.esri.arcgisruntime.localserver.LocalServerStatus;
import com.esri.arcgisruntime.localserver.LocalService.StatusChangedEvent;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;

public class LocalServerFeatureLayerSample extends Application {

  private ArcGISMap map;
  private FeatureLayer featureLayer; // keep loadable in scope to avoid garbage collection
  private LocalFeatureService featureService;
  private MapView mapView;
  private ProgressIndicator featureLayerProgress;

  private static LocalServer server;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Local Server Feature Layer Sample");
      stage.setWidth(800);
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

      // check that local server install path can be accessed
      if (LocalServer.INSTANCE.checkInstallValid()) {
        server = LocalServer.INSTANCE;
        server.addStatusChangedListener(status -> {
          if (server.getStatus() == LocalServerStatus.STARTED) {
            // start feature service
            File mpkFile = new File(System.getProperty("data.dir"), "./samples-data/local_server/PointsofInterest.mpk");
            featureService = new LocalFeatureService(mpkFile.getAbsolutePath());
            featureService.addStatusChangedListener(this::addLocalFeatureLayer);
            featureService.startAsync();
          }
        });
        // start local server
        server.startAsync();
      } else {
        Platform.runLater(() -> {
          Alert dialog = new Alert(AlertType.INFORMATION);
          dialog.initOwner(mapView.getScene().getWindow());
          dialog.setHeaderText("Local Server Load Error");
          dialog.setContentText("Local Geoprocessing Failed to load.");
          dialog.showAndWait();

          Platform.exit();
        });
      }

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
    if (status.getNewStatus() == LocalServerStatus.STARTED && mapView.getMap() != null) {
      // get the url of where feature service is located
      String url = featureService.getUrl() + "/0";
      // create a feature layer using the url
      ServiceFeatureTable featureTable = new ServiceFeatureTable(url);
      featureTable.loadAsync();
      featureLayer = new FeatureLayer(featureTable);
      featureLayer.addDoneLoadingListener(() -> {
        Envelope extent = featureLayer.getFullExtent();
        if (featureLayer.getLoadStatus() == LoadStatus.LOADED && extent != null) {
          mapView.setViewpoint(new Viewpoint(extent));
          Platform.runLater(() -> featureLayerProgress.setVisible(false));
        } else {
          new Alert(Alert.AlertType.ERROR, "Feature Layer Failed to Load!").show();
        }
      });
      featureLayer.loadAsync();
      // add feature layer to map
      map.getOperationalLayers().add(featureLayer);

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
