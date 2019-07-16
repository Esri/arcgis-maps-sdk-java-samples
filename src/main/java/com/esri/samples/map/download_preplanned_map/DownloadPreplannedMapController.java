/* Copyright 2019 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.esri.samples.map.download_preplanned_map;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import com.esri.arcgisruntime.ArcGISRuntimeException;
import com.esri.arcgisruntime.concurrent.Job;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.Geodatabase;
import com.esri.arcgisruntime.data.GeodatabaseFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.mapping.MobileMapPackage;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import com.esri.arcgisruntime.tasks.offlinemap.DownloadPreplannedOfflineMapJob;
import com.esri.arcgisruntime.tasks.offlinemap.DownloadPreplannedOfflineMapParameters;
import com.esri.arcgisruntime.tasks.offlinemap.DownloadPreplannedOfflineMapResult;
import com.esri.arcgisruntime.tasks.offlinemap.OfflineMapTask;
import com.esri.arcgisruntime.tasks.offlinemap.PreplannedMapArea;
import com.esri.arcgisruntime.tasks.offlinemap.PreplannedUpdateMode;

public class DownloadPreplannedMapController {

  @FXML private Button deleteOfflineAreasBtn;
  @FXML private Button downloadAreaBtn;
  @FXML private ListView<PreplannedMapArea> preplannedAreasListView = new ListView<>();
  @FXML private MapView mapView;
  @FXML private ProgressBar downloadProgressBar;

  private ArcGISMap originalMap;
  private DownloadPreplannedOfflineMapJob downloadPreplannedOfflineMapJob;
  private ArrayList<MobileMapPackage> downloadedMaps;
  private OfflineMapTask offlineMapTask;
  private Path tempDirectory;

  @FXML
  private void initialize() {
    try {
      // set up a temporary directory for saving downloaded preplanned maps
      tempDirectory = Files.createTempDirectory("preplanned_offline_map");
      tempDirectory.toFile().deleteOnExit();

      // create a portal to ArcGIS Online
      Portal portal = new Portal("https://www.arcgis.com/");

      // create a portal item using the portal and the item id of a map service
      PortalItem portalItem = new PortalItem(portal, "acc027394bc84c2fb04d1ed317aac674");

      // create a map with the portal item
      originalMap = new ArcGISMap(portalItem);

      // make a list to keep track of downloaded mobile map packages
      downloadedMaps = new ArrayList<>();

      // show the map
      mapView.setMap(originalMap);

      // create an offline map task for the portal item
      offlineMapTask = new OfflineMapTask(portalItem);

      // find the available preplanned map areas
      ListenableFuture<List<PreplannedMapArea>> preplannedMapAreasFuture = offlineMapTask.getPreplannedMapAreasAsync();
      preplannedMapAreasFuture.addDoneListener(() -> {
        try {
          List<PreplannedMapArea> preplannedMapAreas = preplannedMapAreasFuture.get();

          // load each item and add it to the area selection list view
          for (PreplannedMapArea mapArea : preplannedMapAreas) {
            mapArea.loadAsync();
            preplannedAreasListView.getItems().add(mapArea);
          }

          // select the first item by default
          preplannedAreasListView.getSelectionModel().select(0);

          // enable the download button
          downloadAreaBtn.setDisable(false);

        } catch (InterruptedException | ExecutionException e) {
          new Alert(Alert.AlertType.ERROR, "Failed to get the Preplanned Map Areas from the Offline Map Task.").show();
        }
      });

      // make the list view show a preview of the preplanned map area items
      preplannedAreasListView.setCellFactory(c -> new PreplannedMapAreaListCell());

    } catch (IOException e) {
      new Alert(Alert.AlertType.ERROR, "Failed to create a temporary path for saving the Preplanned Map Areas.").show();
    }
  }

  /**
   * Downloads the selected preplanned map area.
   */
  @FXML
  private void handleDownloadArea() {
    if (preplannedAreasListView.getSelectionModel().getSelectedItem() != null) {

      // get the requested preplanned map area
      PreplannedMapArea selectedMapArea = preplannedAreasListView.getSelectionModel().getSelectedItem();

      // create a folder path where the map package will be downloaded to
      String path = tempDirectory + "/" + selectedMapArea.getPortalItem().getTitle();

      // if the area is already downloaded, open it
      if (Files.exists(Paths.get(path))) {

        // open the downloaded mobile map package
        MobileMapPackage localMapArea = new MobileMapPackage(path);
        localMapArea.loadAsync();
        localMapArea.addDoneLoadingListener(() -> mapView.setMap(localMapArea.getMaps().get(0)));
        return;
      }

      // disable the UI
      downloadAreaBtn.setDisable(true);
      deleteOfflineAreasBtn.setDisable(true);

      // create download parameters
      ListenableFuture<DownloadPreplannedOfflineMapParameters> downloadPreplannedOfflineMapParametersFuture = offlineMapTask.createDefaultDownloadPreplannedOfflineMapParametersAsync(selectedMapArea);
      downloadPreplannedOfflineMapParametersFuture.addDoneListener(() -> {
        try {
          DownloadPreplannedOfflineMapParameters downloadPreplannedOfflineMapParameters = downloadPreplannedOfflineMapParametersFuture.get();

          // set the parameters for the offline map to not query for updates
          downloadPreplannedOfflineMapParameters.setUpdateMode(PreplannedUpdateMode.NO_UPDATES);

          // create the job with the parameters and download path
          downloadPreplannedOfflineMapJob = offlineMapTask.downloadPreplannedOfflineMap(downloadPreplannedOfflineMapParameters, path);

          // show the job progress
          downloadProgressBar.setVisible(true);
          downloadPreplannedOfflineMapJob.addProgressChangedListener(() -> downloadProgressBar.setProgress((double) downloadPreplannedOfflineMapJob.getProgress() / 100));

          // start the job and wait for it to complete
          downloadPreplannedOfflineMapJob.start();
          downloadPreplannedOfflineMapJob.addJobDoneListener(() -> {

            // hide the progress bar and label
            downloadProgressBar.setVisible(false);

            if (downloadPreplannedOfflineMapJob.getStatus() == Job.Status.SUCCEEDED) {
              // get the result of the job
              DownloadPreplannedOfflineMapResult downloadPreplannedOfflineMapResult = downloadPreplannedOfflineMapJob.getResult();

              // check if the result has any errors and display them
              checkForOfflineMapResultErrors(downloadPreplannedOfflineMapResult);

              // show the result in the map view
              mapView.setMap(downloadPreplannedOfflineMapResult.getOfflineMap());

              // add the map to the list of downloaded maps
              downloadedMaps.add(downloadPreplannedOfflineMapResult.getMobileMapPackage());

              // re-enable the UI
              downloadAreaBtn.setDisable(false);
              deleteOfflineAreasBtn.setDisable(false);

              // display error details if the job fails
            } else if (downloadPreplannedOfflineMapJob.getStatus() == Job.Status.FAILED) {
              new Alert(Alert.AlertType.ERROR, "Download Preplanned Offline Map Job failed. Error: " + downloadPreplannedOfflineMapJob.getError());
            }
          });
        } catch (InterruptedException | ExecutionException e) {
          new Alert(Alert.AlertType.ERROR, "Could not create Default Parameters for the Download Preplanned Offline Map Job.").show();
        }
      });
    } else {
      new Alert(Alert.AlertType.ERROR, "No Preplanned Map Area selected for downloading.").show();
    }
  }

  /**
   * Checks for layer and table errors of an offline map result, and displays them if present.
   * @param downloadPreplannedOfflineMapResult the result to query for errors
   */
  private void checkForOfflineMapResultErrors(DownloadPreplannedOfflineMapResult downloadPreplannedOfflineMapResult) {
    if (downloadPreplannedOfflineMapResult.hasErrors()) {
      // accumulate all layer and table errors into a single message
      StringBuilder stringBuilder = new StringBuilder("Errors: ");

      Map<Layer, ArcGISRuntimeException> layerErrors = downloadPreplannedOfflineMapResult.getLayerErrors();
      layerErrors.forEach((layer, exception) ->
        stringBuilder.append("Layer: ").append(layer.getName()).append(". Exception: ").append(exception.getMessage()).append(". ")
      );

      Map<FeatureTable, ArcGISRuntimeException> tableError = downloadPreplannedOfflineMapResult.getTableErrors();
      tableError.forEach((table, exception) ->
        stringBuilder.append("Table: ").append(table.getTableName()).append(". Exception: ").append(exception.getMessage()).append(". ")
      );

      // show the message
      new Alert(Alert.AlertType.ERROR, "One or more errors occurred with the Offline Map Result: " + stringBuilder.toString()).show();
    }
  }

  /**
   * Deletes all the previously downloaded offline areas.
   */
  @FXML
  private void deleteOfflineAreas() {

    // cancel the job
    if (downloadPreplannedOfflineMapJob != null) {
      downloadPreplannedOfflineMapJob.cancel();
    }

    // reset the map view to the original map
    mapView.setMap(originalMap);

    // get the geodatabases from all downloaded mobile map packages
    ArrayList<Geodatabase> geodatabases = new ArrayList<>();
    downloadedMaps.forEach(mobileMapPackage -> {
      List<ArcGISMap> maps = mobileMapPackage.getMaps();
      maps.forEach(map ->{
        LayerList operationalLayers = map.getOperationalLayers();
        operationalLayers.forEach(layer -> {
          if (layer instanceof FeatureLayer) {
            FeatureLayer featurelayer = (FeatureLayer) layer;
            GeodatabaseFeatureTable geodatabaseFeatureTable = (GeodatabaseFeatureTable) featurelayer.getFeatureTable();
            // only add unique geodatabases to the list
            if (!geodatabases.contains(geodatabaseFeatureTable.getGeodatabase())) {
              geodatabases.add(geodatabaseFeatureTable.getGeodatabase());
            }
          }
        });
      });
    });

    // close all geodatabases to allow deleting
    geodatabases.forEach(Geodatabase::close);

    // delete all files in the temporary directory
    File localPreplannedMapDirectoryFile = tempDirectory.toFile();
    deleteDirectoryContents(localPreplannedMapDirectoryFile);

    // disable the 'delete offline areas' button
    deleteOfflineAreasBtn.setDisable(true);
  }

  /**
   * Deletes the contents of a directory recursively.
   *
   * @param directory of which to delete all content
   */
  private void deleteDirectoryContents(File directory) {
    if (directory.exists() && directory.listFiles() != null) {
      for (File file : directory.listFiles()) {
        if (file.isDirectory()) {
          deleteDirectoryContents(file);
        }
        try {
          Files.delete(file.toPath());
        } catch (IOException e) {
          if (!file.isDirectory()) {
            new Alert(Alert.AlertType.ERROR, "Could not delete file: " + e.getMessage()).show();
          } else {
            new Alert(Alert.AlertType.ERROR, "Could not clean directory contents: " + e.getMessage()).show();
          }
        }
      }
    }
  }

  /**
   * Stops the animation and disposes of application resources.
   */
  void terminate() {

    if (mapView != null) {
      mapView.dispose();
    }
  }

  /**
   * Shows the available thumbnail of the PreplannedMapArea in the selection list view.
   */
  class PreplannedMapAreaListCell extends ListCell<PreplannedMapArea> {

    @Override
    protected void updateItem(PreplannedMapArea preplannedMapArea, boolean empty) {
      super.updateItem(preplannedMapArea, empty);
      if (preplannedMapArea != null) {
        HBox hBox = new HBox();
        hBox.setMinHeight(65);

        // show the preplanned map area thumbnail image
        ImageView thumbnailImageView = new ImageView();
        hBox.getChildren().add(thumbnailImageView);
        thumbnailImageView.setFitHeight(65);
        thumbnailImageView.setPreserveRatio(true);
        ListenableFuture<byte[]> thumbnailData = preplannedMapArea.getPortalItem().fetchThumbnailAsync();
        thumbnailData.addDoneListener(() -> {
          try {
            thumbnailImageView.setImage(new Image(new ByteArrayInputStream(thumbnailData.get())));
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        });

        setGraphic(hBox);

        // set the list cell's text to the map's index
        setText(preplannedMapArea.getPortalItem().getTitle());
      } else {
        setGraphic(null);
        setText(null);
      }
    }
  }
}
