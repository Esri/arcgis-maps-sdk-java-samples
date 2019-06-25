<<<<<<< Updated upstream:src/main/java/com/esri/samples/localserver/local_server_services/LocalServerServicesController.java
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

package com.esri.samples.localserver.local_server_services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import org.apache.commons.io.FileUtils;

import com.esri.arcgisruntime.localserver.LocalFeatureService;
import com.esri.arcgisruntime.localserver.LocalGeoprocessingService;
import com.esri.arcgisruntime.localserver.LocalMapService;
import com.esri.arcgisruntime.localserver.LocalServer;
import com.esri.arcgisruntime.localserver.LocalServerStatus;
import com.esri.arcgisruntime.localserver.LocalService;

public class LocalServerServicesController {

  @FXML private TextField packagePath;
  @FXML private ComboBox<String> serviceOptions;
  @FXML private Button startServiceButton;
  @FXML private TextArea statusLog;
  @FXML private ListView<LocalService> runningServices;
  @FXML private Button stopServiceButton;

  private HostServices hostServices;
  private FileChooser packageChooser;
  private Path appDataPath;

  @FXML
  private void initialize() {

    if (LocalServer.INSTANCE.checkInstallValid()) {
      LocalServer server = LocalServer.INSTANCE;

      // configure app data path (path length must be short for some services)
      appDataPath = Path.of(System.getProperty("user.home"), "EsriSamples");
      if (!appDataPath.toFile().exists()) {
        try {
          Files.createDirectory(appDataPath);
        } catch (IOException ex) {
          new Alert(AlertType.ERROR, "Failed to set local server app data path. Some processes may not work.").show();
        }
      }
      LocalServer.INSTANCE.setAppDataPath(appDataPath.toFile().getAbsolutePath());

      // log the server status
      server.addStatusChangedListener(status -> statusLog.appendText("Server Status: " + status.getNewStatus()
          .toString() + "\n"));
      // start the local server
      server.startAsync();
    } else {
      Platform.runLater(() -> {
        Alert dialog = new Alert(AlertType.INFORMATION);
        dialog.initOwner(stopServiceButton.getScene().getWindow());
        dialog.setHeaderText("Local Server Load Error");
        dialog.setContentText("Local Server install path couldn't be located.");
        dialog.showAndWait();

        Platform.exit();
      });
    }
    // create a file chooser to select package files
    packageChooser = new FileChooser();
    packagePath.textProperty().bind(packageChooser.initialFileNameProperty());
    packageChooser.setInitialDirectory(new File("./samples-data/local_server"));
    packageChooser.setInitialFileName(packageChooser.getInitialDirectory().getAbsolutePath() + "/PointsOfInterest.mpk");

    // create filters to choose files for specific services
    ExtensionFilter mpkFilter = new ExtensionFilter("Map Packages (*.mpk, *.mpkx)", "*.mpk", "*.mpkx");
    ExtensionFilter gpkFilter = new ExtensionFilter("Geoprocessing Packages (*.gpk, *.gpkx)", "*.gpk", "*.gpkx");
    packageChooser.getExtensionFilters().add(mpkFilter);

    // use the ComboBox to select a filter
    serviceOptions.getSelectionModel().selectedItemProperty().addListener(o -> {
      packageChooser.setInitialFileName(null);
      packageChooser.getExtensionFilters().clear();
      if ("Geoprocessing Service".equals(serviceOptions.getSelectionModel().getSelectedItem())) {
        packageChooser.getExtensionFilters().add(gpkFilter);
      } else {
        packageChooser.getExtensionFilters().add(mpkFilter);
      }
    });

    // create list view representation of running services
    runningServices.setCellFactory(list -> new ListCell<>() {

      @Override
      protected void updateItem(LocalService service, boolean bln) {

        super.updateItem(service, bln);
        if (service != null) {
          setText(service.getName() + "  :  " + service.getUrl());
        } else {
          setText(null);
        }
      }

    });

    // setup UI bindings
    stopServiceButton.disableProperty().bind(runningServices.getSelectionModel().selectedItemProperty().isNull());
    startServiceButton.disableProperty().bind(packageChooser.initialFileNameProperty().isNull());
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
      packageChooser.setInitialDirectory(selectedFile.getParentFile());
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

  /**
   * Stops and releases all resources used in application.
   */
  void terminate() {

    // make sure all services are stopped before deleting app data
    LocalServer.INSTANCE.stopAsync().addDoneListener(() -> {
      if (appDataPath != null) {
        try {
          // delete the app data
          FileUtils.deleteDirectory(appDataPath.toFile());
        } catch (IOException ex) {
          ex.printStackTrace();
        }
      }
    });
  }
}
=======
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

package com.esri.samples.localserver.local_server_services;

import java.io.File;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import com.esri.arcgisruntime.localserver.LocalFeatureService;
import com.esri.arcgisruntime.localserver.LocalGeoprocessingService;
import com.esri.arcgisruntime.localserver.LocalMapService;
import com.esri.arcgisruntime.localserver.LocalServer;
import com.esri.arcgisruntime.localserver.LocalServerStatus;
import com.esri.arcgisruntime.localserver.LocalService;

public class LocalServerServicesController {

  @FXML private TextField packagePath;
  @FXML private ComboBox<String> serviceOptions;
  @FXML private Button startServiceButton;
  @FXML private TextArea statusLog;
  @FXML private ListView<LocalService> runningServices;
  @FXML private Button stopServiceButton;

  private HostServices hostServices;
  private FileChooser packageChooser;

  @FXML
  private void initialize() {

    if (LocalServer.INSTANCE.checkInstallValid()) {
      LocalServer server = LocalServer.INSTANCE;
      // log the server status
      server.addStatusChangedListener(status -> statusLog.appendText("Server Status: " + status.getNewStatus()
          .toString() + "\n"));
      // start the local server
      server.startAsync();
    } else {
      Platform.runLater(() -> {
        Alert dialog = new Alert(AlertType.INFORMATION);
        dialog.initOwner(stopServiceButton.getScene().getWindow());
        dialog.setHeaderText("Local Server Load Error");
        dialog.setContentText("Local Server install path couldn't be located.");
        dialog.showAndWait();

        Platform.exit();
      });
    }
    // create a file chooser to select package files
    packageChooser = new FileChooser();
    packagePath.textProperty().bind(packageChooser.initialFileNameProperty());
    packageChooser.setInitialDirectory(new File("./samples-data/local_server"));
    packageChooser.setInitialFileName(packageChooser.getInitialDirectory().getAbsolutePath() + "/PointsOfInterest.mpk");

    // create filters to choose files for specific services
    ExtensionFilter mpkFilter = new ExtensionFilter("Map Packages (*.mpk, *.mpkx)", "*.mpk", "*.mpkx");
    ExtensionFilter gpkFilter = new ExtensionFilter("Geoprocessing Packages (*.gpk, *.gpkx)", "*.gpk", "*.gpkx");
    packageChooser.getExtensionFilters().add(mpkFilter);

    // use the ComboBox to select a filter
    serviceOptions.getSelectionModel().selectedItemProperty().addListener(o -> {
      packageChooser.setInitialFileName(null);
      packageChooser.getExtensionFilters().clear();
      switch (serviceOptions.getSelectionModel().getSelectedItem()) {
        case "Geoprocessing Service":
          packageChooser.getExtensionFilters().add(gpkFilter);
          break;
        default:
          packageChooser.getExtensionFilters().add(mpkFilter);
      }
    });

    // create list view representation of running services
    runningServices.setCellFactory(list -> new ListCell<LocalService>() {

      @Override
      protected void updateItem(LocalService service, boolean bln) {

        super.updateItem(service, bln);
        if (service != null) {
          setText(service.getName() + "  :  " + service.getUrl());
        } else {
          setText(null);
        }
      }

    });

    // setup UI bindings
    stopServiceButton.disableProperty().bind(runningServices.getSelectionModel().selectedItemProperty().isNull());
    startServiceButton.disableProperty().bind(packageChooser.initialFileNameProperty().isNull());
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
      packageChooser.setInitialDirectory(selectedFile.getParentFile());
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
>>>>>>> Stashed changes:local_server/local_server_services/LocalServerServicesController.java
