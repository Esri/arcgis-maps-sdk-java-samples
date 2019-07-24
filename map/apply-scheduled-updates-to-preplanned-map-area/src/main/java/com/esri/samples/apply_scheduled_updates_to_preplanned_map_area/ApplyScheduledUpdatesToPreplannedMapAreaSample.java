package com.esri.samples.apply_scheduled_updates_to_preplanned_map_area;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.concurrent.Job;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.MobileMapPackage;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.tasks.geodatabase.SyncGeodatabaseParameters;
import com.esri.arcgisruntime.tasks.offlinemap.OfflineMapSyncJob;
import com.esri.arcgisruntime.tasks.offlinemap.OfflineMapSyncParameters;
import com.esri.arcgisruntime.tasks.offlinemap.OfflineMapSyncResult;
import com.esri.arcgisruntime.tasks.offlinemap.OfflineMapSyncTask;
import com.esri.arcgisruntime.tasks.offlinemap.OfflineMapUpdatesInfo;
import com.esri.arcgisruntime.tasks.offlinemap.OfflineUpdateAvailability;
import com.esri.arcgisruntime.tasks.offlinemap.PreplannedScheduledUpdatesOption;

import org.apache.commons.io.FileUtils;

public class ApplyScheduledUpdatesToPreplannedMapAreaSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {
    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Update Offline Map Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a map view
      mapView = new MapView();

      // create progress indicator
      ProgressIndicator progressIndicator = new ProgressIndicator();
      progressIndicator.setVisible(false);

      // create a button to update the offline map
      Button applyUpdatesButton = new Button("Apply Scheduled Updates");
      applyUpdatesButton.setDisable(true);

      // create labels to show update availability and size
      Label updateAvailableLabel = new Label("Updates: ");
      updateAvailableLabel.setTextFill(Color.WHITE);
      Label updateSizeLabel = new Label("Update size: ");
      updateSizeLabel.setTextFill(Color.WHITE);

      // create a control panel for the UI elements
      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY,
              Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(180, 110);
      controlsVBox.getChildren().addAll(applyUpdatesButton, updateAvailableLabel, updateSizeLabel);

      // create a temporary copy of the local offline map files, so that updating does not overwrite them permanently
      Path tempMobileMapPackageDirectory = Files.createTempDirectory("canyonlands_offline_map");
      tempMobileMapPackageDirectory.toFile().deleteOnExit();
      Path sourceDirectory = Paths.get("./samples-data/canyonlands/");
      FileUtils.copyDirectory(sourceDirectory.toFile(), tempMobileMapPackageDirectory.toFile());

      // load the offline map as a mobile map package
      MobileMapPackage mobileMapPackage = new MobileMapPackage(tempMobileMapPackageDirectory.toString());
      mobileMapPackage.loadAsync();
      mobileMapPackage.addDoneLoadingListener(() -> {
        if (mobileMapPackage.getLoadStatus() == LoadStatus.LOADED && !mobileMapPackage.getMaps().isEmpty()) {

          //add the map from the mobile map package to the map view
          mapView.setMap(mobileMapPackage.getMaps().get(0));

          // show progress indicator
          progressIndicator.setVisible(true);

          // create an offline map sync task with the mobile map package
          OfflineMapSyncTask offlineMapSyncTask = new OfflineMapSyncTask(mobileMapPackage.getMaps().get(0));

          // check for updates to the offline map
          ListenableFuture<OfflineMapUpdatesInfo> offlineMapUpdatesInfoFuture = offlineMapSyncTask.checkForUpdatesAsync();
          offlineMapUpdatesInfoFuture.addDoneListener(() -> {
            try {
              // get and check the results
              OfflineMapUpdatesInfo offlineMapUpdatesInfo = offlineMapUpdatesInfoFuture.get();

              // update UI for available updates
              if (offlineMapUpdatesInfo.getDownloadAvailability() == OfflineUpdateAvailability.AVAILABLE) {
                updateAvailableLabel.setText("Updates: AVAILABLE");

                // check and show update size
                long updateSize = offlineMapUpdatesInfo.getScheduledUpdatesDownloadSize();
                updateSizeLabel.setText("Update size: " + updateSize + " bytes.");

                // hide the progress indicator
                progressIndicator.setVisible(false);

                // enable the 'Apply Scheduled Updates' button
                applyUpdatesButton.setDisable(false);
                // when the button is clicked, synchronize the mobile map package
                applyUpdatesButton.setOnAction(e -> {

                  // show progress indicator
                  progressIndicator.setVisible(true);

                  // create default parameters for the sync task
                  ListenableFuture<OfflineMapSyncParameters> offlineMapSyncParametersFuture = offlineMapSyncTask.createDefaultOfflineMapSyncParametersAsync();
                  offlineMapSyncParametersFuture.addDoneListener(() -> {
                    try {
                      OfflineMapSyncParameters offlineMapSyncParameters = offlineMapSyncParametersFuture.get();

                      // set the sync direction to none, since we only want to update
                      offlineMapSyncParameters.setSyncDirection(SyncGeodatabaseParameters.SyncDirection.NONE);
                      // set the parameters to download all updated for the mobile map packages
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

                          // reopen the mobile map package if required
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

                          // update labels
                          updateAvailableLabel.setText("Updates: Up to date");
                          updateSizeLabel.setText("Update size: N/A");

                        } else {
                          new Alert(Alert.AlertType.ERROR, "Error syncing the offline map: " + offlineMapSyncJob.getError().getMessage()).show();
                        }

                        // disable the 'Apply Scheduled Updates' button
                        applyUpdatesButton.setDisable(true);
                        // hide progress indicator
                        progressIndicator.setVisible(false);

                      });
                    } catch (InterruptedException | ExecutionException ex) {
                      new Alert(Alert.AlertType.ERROR, "Error creating DefaultOfflineMapSyncParameters" + ex.getMessage()).show();
                    }
                  });
                });

              } else {
                updateAvailableLabel.setText("Updates: NOT AVAILABLE");
              }
            } catch (Exception e) {
              new Alert(Alert.AlertType.ERROR, "Error checking for Scheduled Updates Availability: " + e.getMessage()).show();
            }
          });
        } else {
          new Alert(Alert.AlertType.ERROR, "Failed to load the mobile map package.").show();
        }
      });

      // add the map view and UI elements to the stack pane
      stackPane.getChildren().addAll(mapView, progressIndicator, controlsVBox);
      StackPane.setAlignment(progressIndicator, Pos.CENTER);
      StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
      StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
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