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

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import com.esri.arcgisruntime.localserver.LocalFeatureService;
import com.esri.arcgisruntime.localserver.LocalGeoprocessingService;
import com.esri.arcgisruntime.localserver.LocalMapService;
import com.esri.arcgisruntime.localserver.LocalServer;
import com.esri.arcgisruntime.localserver.LocalServer.StatusChangedEvent;
import com.esri.arcgisruntime.localserver.LocalServerStatus;
import com.esri.arcgisruntime.localserver.LocalService;
import com.esri.arcgisruntime.util.ListenableList;

public class LocalServerServicesController {

  @FXML
  private Button btnStartServer;
  @FXML
  private Button btnStopServer;
  @FXML
  private Button btnStartService;
  @FXML
  private Button btnStopService;
  @FXML
  private Button btnURL;
  @FXML
  private ComboBox<String> serviceOption;
  @FXML
  private TextArea centerText;
  @FXML
  ListView<String> bottomView;

  private LocalServer server;
  private ListenableList<LocalService> services;

  @FXML
  protected void handleStartStopLocalServer(ActionEvent e) {

    EventTarget event = e.getTarget();

    if (event.equals(btnStartServer)) {
      // start local server
      server = LocalServer.INSTANCE;
      server.addStatusChangedListener(this::updateServerStatus);
      //      }
      server.startAsync();

      // tracking services attached to this local server
      services = server.getServices();
      System.out.println("Services: " + services.size());

      // update buttons on layout
      btnStartServer.setDisable(true);
      btnStopServer.setDisable(false);
      btnStartService.setDisable(false);

    } else if (event.equals(btnStopServer)) {
      terminate();
      btnStartServer.setDisable(false);
      btnStopServer.setDisable(true);
      btnStartService.setDisable(true);
      btnStopService.setDisable(true);
    }
  }

  private void updateServerStatus(StatusChangedEvent status) {
    centerText.appendText("Server Status: " + status.getNewStatus().toString() + "\n");
  }

  @FXML
  protected void handleStartService(ActionEvent e) throws URISyntaxException {
    if (server.getStatus() == LocalServerStatus.STARTED) {

      //      LocalService service = null;
      //      String statusMessage = "";
      String service = serviceOption.getSelectionModel().getSelectedItem();
      boolean serviceExists = false;
      for (String s : bottomView.getItems()) {
        if (s.startsWith(service)) {
          serviceExists = true;
        }
      }

      if (!serviceExists) {
        switch (service) {
          case "Map Service":
            String mapServiceURL = Paths.get(getClass().getResource("/PointsofInterest.mpk").toURI()).toString();
            LocalMapService mapService = new LocalMapService(mapServiceURL);
            mapService.addStatusChangedListener(status -> {
              centerText.appendText("Map Service Status: " + status.getNewStatus().toString() + "\n");
              if (status.getNewStatus() == LocalServerStatus.STARTED) {
                //              bottomText.appendText("Map Service " + mapService.getUrl());
                Platform.runLater(() -> bottomView.getItems().add("Map Service URL ->  " + mapService.getUrl()));
              }
            });
            mapService.startAsync();
            break;
          case "Feature Service":
            String featureServiceURL = Paths.get(getClass().getResource("/PointsofInterest.mpk").toURI()).toString();
            LocalFeatureService featureService = new LocalFeatureService(featureServiceURL);
            featureService.addStatusChangedListener(status -> {
              centerText.appendText("Feature Service Status: " + status.getNewStatus().toString() + "\n");
              if (status.getNewStatus() == LocalServerStatus.STARTED) {
                //              bottomText.appendText("Map Service " + mapService.getUrl());
                Platform
                    .runLater(() -> bottomView.getItems().add("Feature Service URL ->  " + featureService.getUrl()));
              }
            });
            featureService.startAsync();
            break;
          case "Geoprocessing Service":
            String geoServiceURL = Paths.get(getClass().getResource("/MessageInABottle.gpk").toURI()).toString();
            LocalGeoprocessingService geoService = new LocalGeoprocessingService(geoServiceURL);
            geoService.addStatusChangedListener(status -> {
              centerText.appendText("Geo Service Status: " + status.getNewStatus().toString() + "\n");
              if (status.getNewStatus() == LocalServerStatus.STARTED) {
                //              bottomText.appendText("Map Service " + mapService.getUrl());
                Platform.runLater(() -> bottomView.getItems().add("Geo Service URL ->  " + geoService.getUrl()));
              }
            });
            geoService.startAsync();
            break;
        }
      } else {
        Platform.runLater(() -> {
          Alert dialog = new Alert(AlertType.INFORMATION);
          dialog.setHeaderText("Service Running");
          dialog.setContentText(service + " has already been started.");
          dialog.showAndWait();
        });
      }
    }
  }

  @FXML
  protected void handleStopService(ActionEvent e) {
    int serviceIndex = bottomView.getSelectionModel().getSelectedIndex();
    services.get(serviceIndex).stopAsync();
    bottomView.getItems().remove(serviceIndex);

  }

  @FXML
  protected void enableButtons() {

    int listIndex = bottomView.getSelectionModel().getSelectedIndex();
    if (listIndex != -1) {
      btnStopService.setDisable(false);
      btnURL.setDisable(false);
    } else {
      btnStopService.setDisable(true);
      btnURL.setDisable(true);
    }
  }

  @FXML
  protected void handleURL(ActionEvent e) {
    if (services != null && services.size() > 0) {
      String url = bottomView.getSelectionModel().getSelectedItem().split(">")[1].trim();
      System.out.println("URL: " + url);
      try {
        Desktop.getDesktop().browse(new URI(url));
      } catch (IOException ex) {
        ex.printStackTrace();
      } catch (URISyntaxException exc) {
        exc.printStackTrace();
      }
    }
  }

  void terminate() {
    // stop services
    if (services != null && services.size() > 0) {
      for (LocalService service : services) {
        if (service.getStatus() == LocalServerStatus.STARTED) {
          service.stopAsync();
        }
      }
      bottomView.getItems().clear();
    }
    // stop local server
    if (server != null && server.getStatus() == LocalServerStatus.STARTED) {
      server.removeStatusChangedListener(this::updateServerStatus);
      System.out.println("Listener Removed");
      server.stopAsync();
    }
  }
}
