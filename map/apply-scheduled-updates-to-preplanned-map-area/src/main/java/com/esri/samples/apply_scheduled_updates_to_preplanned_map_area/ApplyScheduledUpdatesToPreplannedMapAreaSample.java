/*
 * Copyright 2019 Esri.
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

package com.esri.samples.apply_scheduled_updates_to_preplanned_map_area;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import com.esri.arcgisruntime.concurrent.Job;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.MobileMapPackage;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.tasks.offlinemap.OfflineMapSyncJob;
import com.esri.arcgisruntime.tasks.offlinemap.OfflineMapSyncParameters;
import com.esri.arcgisruntime.tasks.offlinemap.OfflineMapSyncResult;
import com.esri.arcgisruntime.tasks.offlinemap.OfflineMapSyncTask;
import com.esri.arcgisruntime.tasks.offlinemap.OfflineMapUpdatesInfo;
import com.esri.arcgisruntime.tasks.offlinemap.OfflineUpdateAvailability;
import com.esri.arcgisruntime.tasks.offlinemap.PreplannedScheduledUpdatesOption;

public class ApplyScheduledUpdatesToPreplannedMapAreaSample extends Application {

  private MapView mapView;
  private MobileMapPackage mobileMapPackage;
  private OfflineMapSyncTask offlineMapSyncTask;

  @Override
  public void start(Stage stage) {
    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Apply Scheduled Updates to Preplanned Map Area");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a map view
      mapView = new MapView();

      // create a temporary copy of the local offline map files, so that updating does not overwrite them permanently
      Path tempMobileMapPackageDirectory = Files.createTempDirectory("canyonlands_offline_map");
      tempMobileMapPackageDirectory.toFile().deleteOnExit();
      Path sourceDirectory = Paths.get("./samples-data/canyonlands/");
      FileUtils.copyDirectory(sourceDirectory.toFile(), tempMobileMapPackageDirectory.toFile());

      // load the offline map as a mobile map package
      mobileMapPackage = new MobileMapPackage(tempMobileMapPackageDirectory.toString());
      mobileMapPackage.loadAsync();
      mobileMapPackage.addDoneLoadingListener(() -> {
        if (mobileMapPackage.getLoadStatus() == LoadStatus.LOADED && !mobileMapPackage.getMaps().isEmpty()) {

          // add the map from the mobile map package to the map view
          ArcGISMap offlineMap = mobileMapPackage.getMaps().get(0);
          mapView.setMap(offlineMap);

          // create an offline map sync task with the preplanned area
          offlineMapSyncTask = new OfflineMapSyncTask(offlineMap);

          // check for available updates to the mobile map package
          checkForScheduledUpdates();

        } else {
          new Alert(Alert.AlertType.ERROR, "Failed to load the mobile map package.").show();
        }
      });

      // add the map view to the stack pane
      stackPane.getChildren().add(mapView);

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Checks for scheduled updates to the preplanned map area.
   */
  private void checkForScheduledUpdates() {

    // check for updates to the offline map
    ListenableFuture<OfflineMapUpdatesInfo> offlineMapUpdatesInfoFuture = offlineMapSyncTask.checkForUpdatesAsync();
    offlineMapUpdatesInfoFuture.addDoneListener(() -> {
      try {
        // get and check the results
        OfflineMapUpdatesInfo offlineMapUpdatesInfo = offlineMapUpdatesInfoFuture.get();

        // update UI for available updates
        if (offlineMapUpdatesInfo.getDownloadAvailability() == OfflineUpdateAvailability.AVAILABLE) {

          // get the update size
          long updateSize = offlineMapUpdatesInfo.getScheduledUpdatesDownloadSize();

          // create a dialog to show the update information
          Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
          alert.setTitle("Updates Available");
          alert.setHeaderText("An update is available for this preplanned map area.");
          alert.setResizable(false);
          alert.setContentText("Update size: " + updateSize + " bytes. Apply the update?");

          // show the dialog and wait for confirmation
          Optional<ButtonType> result = alert.showAndWait();
          ButtonType button = result.orElse(ButtonType.CANCEL);

          // apply the update if the dialog is confirmed
          if (button == ButtonType.OK) {
            applyScheduledUpdates();
          }

        } else {
          // show a dialog that no updates are available
          Alert alert = new Alert(Alert.AlertType.INFORMATION);
          alert.setTitle("Up to Date");
          alert.setHeaderText("No updates available.");
          alert.setResizable(false);
          alert.setContentText("The preplanned map area is up to date.");
          alert.show();
        }

      } catch (Exception ex) {
        new Alert(Alert.AlertType.ERROR, "Error checking for Scheduled Updates Availability.").show();
      }
    });
  }

  /**
   * Applies the scheduled updates to the preplanned map area.
   */
  private void applyScheduledUpdates() {

    // create default parameters for the sync task
    ListenableFuture<OfflineMapSyncParameters> offlineMapSyncParametersFuture = offlineMapSyncTask.createDefaultOfflineMapSyncParametersAsync();
    offlineMapSyncParametersFuture.addDoneListener(() -> {
      try {
        OfflineMapSyncParameters offlineMapSyncParameters = offlineMapSyncParametersFuture.get();

        // set the parameters to download all updates for the mobile map packages
        offlineMapSyncParameters.setPreplannedScheduledUpdatesOption(PreplannedScheduledUpdatesOption.DOWNLOAD_ALL_UPDATES);
        // set the map package to rollback to the old state should the sync job fail
        offlineMapSyncParameters.setRollbackOnFailure(true);

        // create a sync job using the parameters
        OfflineMapSyncJob offlineMapSyncJob = offlineMapSyncTask.syncOfflineMap(offlineMapSyncParameters);

        // start the job and get the results
        offlineMapSyncJob.start();
        offlineMapSyncJob.addJobDoneListener(() -> {
          if (offlineMapSyncJob.getStatus() == Job.Status.SUCCEEDED) {
            OfflineMapSyncResult offlineMapSyncResult = offlineMapSyncJob.getResult();

            // if mobile map package reopen is required, close the existing mobile map package and load it again
            if (offlineMapSyncResult.isMobileMapPackageReopenRequired()) {
              mobileMapPackage.close();
              mobileMapPackage.loadAsync();
              mobileMapPackage.addDoneLoadingListener(() -> {
                if (mobileMapPackage.getLoadStatus() == LoadStatus.LOADED && !mobileMapPackage.getMaps().isEmpty()) {

                  // add the map from the mobile map package to the map view
                  mapView.setMap(mobileMapPackage.getMaps().get(0));

                } else {
                  new Alert(Alert.AlertType.ERROR, "Failed to load the mobile map package.").show();
                }
              });
            }

            // perform another check for updates, to make sure that the newest update was applied
            checkForScheduledUpdates();

          } else {
            new Alert(Alert.AlertType.ERROR, "Error syncing the offline map.").show();
          }

        });
      } catch (InterruptedException | ExecutionException ex) {
        new Alert(Alert.AlertType.ERROR, "Error creating DefaultOfflineMapSyncParameters").show();
      }
    });
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