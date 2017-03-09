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

package com.esri.samples.localserver.local_server_services;

import java.io.File;

import com.esri.arcgisruntime.localserver.LocalFeatureService;
import com.esri.arcgisruntime.localserver.LocalGeoprocessingService;
import com.esri.arcgisruntime.localserver.LocalMapService;
import com.esri.arcgisruntime.localserver.LocalServer;
import com.esri.arcgisruntime.localserver.LocalServerStatus;
import com.esri.arcgisruntime.localserver.LocalService;
import com.esri.arcgisruntime.util.ListenableList;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

public class LocalServerServicesController {

  @FXML private Button btnStartServer;
  @FXML private ComboBox<String> serviceOptions;
  @FXML private Button btnSelectPackage;
  @FXML private Label lblSelectedPackage;
  @FXML private TextArea statusLog;
  @FXML private ListView<String> runningServices;

  private ListenableList<LocalService> services;
  private HostServices hostServices;

  private static final LocalServer server = LocalServer.INSTANCE;

  @FXML
  private void initialize() {
    // watch server status
    server.addStatusChangedListener(status -> {
      statusLog.appendText("Server Status: " + status.getNewStatus().toString() + "\n");
      btnStartServer.setDisable(status.getNewStatus() == LocalServerStatus.STARTED);
      handleSelectServiceOption();
    });
  }

  /** 
   * Handles starting a local server.
   */
  @FXML
  private void handleStartLocalServer() {
    // start local server
    server.startAsync();

    // get observable list of services
    services = server.getServices();
  }

  /** 
   * Handles stopping a local server.
   */
  @FXML
  private void handleStopLocalServer() {
    // stop local server
    if (server.getStatus() == LocalServerStatus.STARTED) {
      server.stopAsync();
    }

    // remove listed running services
    runningServices.getItems().clear();
  }

  /**
   * Creates and starts the selected local service in the dropdown.
   * <p>
   * Shows a warning popup if the local service is already running.
   */
  @FXML
  private void handleStartSelectedService() {

    String selected = serviceOptions.getSelectionModel().getSelectedItem();
    if (runningServices.getItems().filtered(n -> n.startsWith(selected)).size() > 0) {
      // warn service is already running
      Platform.runLater(() -> {
        Alert dialog = new Alert(AlertType.INFORMATION);
        dialog.setHeaderText("Service Running");
        dialog.setContentText(selected + " has already been started.");
        dialog.showAndWait();
      });
    } else {
      // create local service
      final String serviceUrl;
      final LocalService localService;
      serviceUrl = new File(lblSelectedPackage.getText()).getAbsolutePath();
      switch (selected) {
        case "Map Service":
          localService = new LocalMapService(serviceUrl);
          break;
        case "Feature Service":
          localService = new LocalFeatureService(serviceUrl);
          break;
        case "Geoprocessing Service":
          localService = new LocalGeoprocessingService(serviceUrl);
          break;
        default: 
          localService = null;
      }

      // start local service and watch for updates
      if (localService != null) {
        localService.addStatusChangedListener(status -> {
          statusLog.appendText(selected + " Status: " + status.getNewStatus().toString() + "\n");
          if (status.getNewStatus() == LocalServerStatus.STARTED) {
            Platform.runLater(() -> runningServices.getItems().add(selected + " URL ->  " + localService.getUrl()));
          }
        });
        localService.startAsync();
      }
    }
  }
  
  @FXML
  private void handleSelectServiceOption() {
    String selected = serviceOptions.getSelectionModel().getSelectedItem();
    String pathStart = "./samples-data/local_server/";
    switch (selected) {
      case "Map Service":
      case "Feature Service":
        lblSelectedPackage.setText(pathStart + "PointsofInterest.mpk");
        break;
      case "Geoprocessing Service":
        lblSelectedPackage.setText(pathStart +  "MessageInABottle.gpk");
        break;
      default:
        break;
    }
  }
  
  @FXML
  private void handleSelectPackage() {
    File selectedFile = LocalServerServicesSample.openFileDialog();
    if (selectedFile == null) {
      return;
    }
    lblSelectedPackage.setText(selectedFile.getAbsolutePath());
  }

  /**
   * Stops the selected service from the running services list.
   */
  @FXML
  private void handleStopSelectedService() {

    int selectedIndex = runningServices.getSelectionModel().getSelectedIndex();
    services.get(selectedIndex).stopAsync();
    runningServices.getItems().remove(selectedIndex);
  }

  /** 
   * Opens a browser to the url of the selected service.
   */
  @FXML
  private void handleURL() {

    String url = runningServices.getSelectionModel().getSelectedItem().split(">")[1].trim();
    hostServices.showDocument(url);
  }

  /** 
   * Allows access to the Host Services of the main JavaFX application.
   * 
   * @param hostServices Hosted Services from main JavaFX application 
   */
  void setHostServices(HostServices hostServices) {

    this.hostServices = hostServices;
  }
}
