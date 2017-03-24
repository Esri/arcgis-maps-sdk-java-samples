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

import static javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;

import com.esri.arcgisruntime.localserver.LocalFeatureService;
import com.esri.arcgisruntime.localserver.LocalGeoprocessingService;
import com.esri.arcgisruntime.localserver.LocalMapService;
import com.esri.arcgisruntime.localserver.LocalServer;
import com.esri.arcgisruntime.localserver.LocalServerStatus;
import com.esri.arcgisruntime.localserver.LocalService;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class LocalServerServicesController {

  @FXML private TextField packagePath;
  @FXML private ComboBox<String> serviceOptions;
  @FXML private TextArea statusLog;
  @FXML private ListView<LocalService> runningServices;
  @FXML private Button stopServiceButton;

  private HostServices hostServices;
  private FileChooser packageChooser;

  private static final LocalServer server = LocalServer.INSTANCE;

  @FXML
  private void initialize() {

    // start the local server
    server.startAsync();

    // log the server status
    server.addStatusChangedListener(status ->
        statusLog.appendText("Server Status: " + status.getNewStatus().toString() + "\n")
    );

    // setup UI bindings
    stopServiceButton.disableProperty().bind(runningServices.getSelectionModel().selectedItemProperty().isNull());
    packagePath.textProperty().addListener(o -> {
      serviceOptions.getItems().clear();
      String[] uriParts = packagePath.getText().split("\\.");
      if (uriParts.length > 0) {
        String extension = uriParts[uriParts.length - 1];
        switch (extension) {
          case "mpk": serviceOptions.getItems().addAll("Map Service", "Feature Service"); break;
          case "gpk": serviceOptions.getItems().add("Geoprocessing Service");
        }
        serviceOptions.getSelectionModel().select(0);
      }
    });

    // create a file chooser to select package files
    packageChooser = new FileChooser();
    ExtensionFilter extensionFilter = new ExtensionFilter("Local Server Packages", "*.mpk", "*.gpk");
    packagePath.textProperty().bind(packageChooser.initialFileNameProperty());
    packageChooser.setInitialDirectory(new File("./samples-data/local_server"));
    packageChooser.setInitialFileName(packageChooser.getInitialDirectory().getAbsolutePath() + "/PointsOfInterest.mpk");
    packageChooser.getExtensionFilters().add(extensionFilter);
    packageChooser.setSelectedExtensionFilter(extensionFilter);

    // create list view representation of running services
    runningServices.setCellFactory(list ->
      new ListCell<LocalService>(){

        @Override
        protected void updateItem(LocalService service, boolean bln) {
          super.updateItem(service, bln);
          if (service != null) {
            setText(service.getName() + "  :  " + service.getUrl());
          }
        }

      }
    );
  }

  /**
   * Creates and starts the selected service.
   */
  @FXML
  private void startSelectedService() {

    String selected = serviceOptions.getSelectionModel().getSelectedItem();
    // create local service
    final LocalService localService;
    final String serviceUrl = packageChooser.getInitialFileName();
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
          Platform.runLater(() -> runningServices.getItems().add(localService));
        }
      });
      localService.startAsync();
    }
  }

  /**
   * Opens a dialog to choose a package file.
   */
  @FXML
  private void openPackage() {

    File selectedFile = packageChooser.showOpenDialog(packagePath.getScene().getWindow());
    if (selectedFile != null) {
      packageChooser.setInitialFileName(selectedFile.getAbsolutePath());
    }
  }

  /**
   * Stops the selected service from the running services list.
   */
  @FXML
  private void stopSelectedService() {
    LocalService selectedService = runningServices.getSelectionModel().getSelectedItem();
    selectedService.stopAsync();
    runningServices.getItems().remove(selectedService);
  }

  /** 
   * Opens a browser to the url of the selected service.
   */
  @FXML
  private void openServiceURL() {

    String url = runningServices.getSelectionModel().getSelectedItem().getUrl();
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
