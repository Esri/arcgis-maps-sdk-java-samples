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

import java.nio.file.Paths;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class LocalServerMapImageLayer extends Application {

  private ArcGISMap map;
  private MapView mapView;
  private LocalServer server;

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Local Server Feature Layer");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a view with a map and basemap
      map = new ArcGISMap(Basemap.createStreets());
      mapView = new MapView();
      mapView.setMap(map);

      // add view to application window
      stackPane.getChildren().add(mapView);

      server = LocalServer.INSTANCE;
      server.addStatusChangedListener(status -> {
        System.out.println("Running");
        if (status.getNewStatus() == LocalServerStatus.STARTED) {
          System.out.println("Local Server has started!");
          try {
            String featureServiceURL = Paths.get(getClass().getResource("/PointsofInterest.mpk").toURI()).toString();
            LocalFeatureService featureService = new LocalFeatureService(featureServiceURL);
            featureService.addStatusChangedListener(s -> {
              if (s.getNewStatus() == LocalServerStatus.STARTED) {
                System.out.println("Local Service has started!");
                String url = featureService.getUrl() + "/0";
                System.out.println("Url: " + url);
                ServiceFeatureTable featureTable = new ServiceFeatureTable(url);
                featureTable.loadAsync();
                FeatureLayer featureLayer = new FeatureLayer(featureTable);
                featureLayer.loadAsync();
                map.getOperationalLayers().add(featureLayer);
                mapView.setViewpoint(new Viewpoint(featureLayer.getFullExtent()));
              }
            });
            featureService.startAsync();
          } catch (Exception e) {
            e.printStackTrace();
          }
        } else if (status.getNewStatus() == LocalServerStatus.STOPPED) {
          System.out.println("STOPPED");
        }
      });
      server.startAsync();
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() throws Exception {

    if (server != null) {
      ListenableList<LocalService> services = server.getServices();
      // stop any services that have been started
      for (LocalService service : server.getServices()) {
        if (service.getStatus() == LocalServerStatus.STARTED) {

          ListenableFuture<Void> stop = service.stopAsync();
          // stop server once all services have stopped
          stop.addDoneListener(() -> {
            int servicesStarted = services.size();
            for (LocalService s : services) {
              if (s.getStatus() == LocalServerStatus.STOPPED) {
                servicesStarted--;
              }
            }
            if (servicesStarted == 0) {
              server.stopAsync();
            }
          });
        }
      }
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
