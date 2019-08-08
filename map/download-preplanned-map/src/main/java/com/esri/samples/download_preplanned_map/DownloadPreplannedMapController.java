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

package com.esri.samples.download_preplanned_map;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import org.apache.commons.io.FileUtils;

import com.esri.arcgisruntime.ArcGISRuntimeException;
import com.esri.arcgisruntime.concurrent.Job;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.Geodatabase;
import com.esri.arcgisruntime.data.GeodatabaseFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.mapping.MobileMapPackage;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import com.esri.arcgisruntime.security.AuthenticationManager;
import com.esri.arcgisruntime.security.DefaultAuthenticationChallengeHandler;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.tasks.offlinemap.DownloadPreplannedOfflineMapJob;
import com.esri.arcgisruntime.tasks.offlinemap.DownloadPreplannedOfflineMapParameters;
import com.esri.arcgisruntime.tasks.offlinemap.DownloadPreplannedOfflineMapResult;
import com.esri.arcgisruntime.tasks.offlinemap.OfflineMapTask;
import com.esri.arcgisruntime.tasks.offlinemap.PreplannedMapArea;
import com.esri.arcgisruntime.tasks.offlinemap.PreplannedUpdateMode;

public class DownloadPreplannedMapController {

  @FXML private Button deleteOfflineAreasButton;
  @FXML private Button downloadAreaButton;
  @FXML private Button showOnlineMapButton;
  @FXML private ListView<PreplannedMapArea> preplannedAreasListView;
  @FXML private MapView mapView;
  @FXML private ProgressBar downloadProgressBar;

  private ArcGISMap onlineMap;
  private ArrayList<MobileMapPackage> openedMobileMapPackages;
  private ChangeListener<PreplannedMapArea> listViewSelectionChangeViewpointListener;
  private DownloadPreplannedOfflineMapJob downloadPreplannedOfflineMapJob;
  private GraphicsOverlay areasOfInterestGraphicsOverlay;
  private MobileMapPackage localMapArea;
  private OfflineMapTask offlineMapTask;
  private Path tempDirectory;

  @FXML
  private void initialize() {
    try {
      // set up a temporary directory for saving downloaded preplanned maps
      tempDirectory = Files.createTempDirectory("preplanned_offline_map");
      tempDirectory.toFile().deleteOnExit();

      // make a list to store opened mobile map packages
      openedMobileMapPackages = new ArrayList<>();

      // create a portal to ArcGIS Online
      Portal portal = new Portal("https://www.arcgis.com/");

      // set the authentication manager to handle OAuth challenges when accessing the portal
      AuthenticationManager.setAuthenticationChallengeHandler(new DefaultAuthenticationChallengeHandler());

      // create a portal item using the portal and the item id of a map service
      PortalItem portalItem = new PortalItem(portal, "acc027394bc84c2fb04d1ed317aac674");

      // create a map with the portal item
      onlineMap = new ArcGISMap(portalItem);

      // show the map
      mapView.setMap(onlineMap);

      // create a graphics overlay to show the preplanned map areas extents (areas of interest)
      areasOfInterestGraphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(areasOfInterestGraphicsOverlay);

      // create a red outline to mark the areas of interest of the preplanned map areas
      SimpleLineSymbol areaOfInterestLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0x80FF0000, 5.0f);
      // create simple renderer for areas of interest, and set it to use the line symbol
      SimpleRenderer areaOfInterestRenderer = new SimpleRenderer();
      areaOfInterestRenderer.setSymbol(areaOfInterestLineSymbol);
      areasOfInterestGraphicsOverlay.setRenderer(areaOfInterestRenderer);

      // create an offline map task for the portal item
      offlineMapTask = new OfflineMapTask(portalItem);

      // find the available preplanned map areas
      ListenableFuture<List<PreplannedMapArea>> preplannedMapAreasFuture = offlineMapTask.getPreplannedMapAreasAsync();
      preplannedMapAreasFuture.addDoneListener(() -> {
        try {
          // get the preplanned areas and add them to the list view
          List<PreplannedMapArea> preplannedMapAreas = preplannedMapAreasFuture.get();
          preplannedAreasListView.getItems().addAll(preplannedMapAreas);

          // load each item and get the area of interest
          for (PreplannedMapArea mapArea : preplannedMapAreas) {
            mapArea.loadAsync();
            mapArea.addDoneLoadingListener(() -> {
              if (mapArea.getLoadStatus() == LoadStatus.LOADED) {

                // create a graphic for the area of interest and add it to the graphics overlay
                areasOfInterestGraphicsOverlay.getGraphics().add(new Graphic(mapArea.getAreaOfInterest()));
              }
            });
          }

          // enable the download button
          downloadAreaButton.setDisable(false);

        } catch (InterruptedException | ExecutionException e) {
          new Alert(Alert.AlertType.ERROR, "Failed to get the Preplanned Map Areas from the Offline Map Task.").show();
        }
      });

      // add a cell factory to the list view that shows the preplanned area's title
      preplannedAreasListView.setCellFactory(c -> new PreplannedMapAreaListCell());

      // create a listener that changes the viewpoint to the extent of the preplanned map area when an item is selected in the list view
      listViewSelectionChangeViewpointListener = (obs, oldValue, newValue) -> {
        if (newValue != null) {
          changeViewPoint(newValue);
        }
      };

      // create a listener that checks if the area has been previously downloaded, and changes the button text accordingly
      ChangeListener<PreplannedMapArea> listViewSelectionButtonLabelListener = (obs, oldValue, newValue) -> {
        if (newValue != null) {
          // get the path for the preplanned area
          String path = tempDirectory + "/" + newValue.getPortalItem().getTitle();

          // check if the path exists and set the button text accordingly
          if (Files.exists(Paths.get(path))) {
            downloadAreaButton.setText("View Downloaded Area");
          } else {
            downloadAreaButton.setText("Download Preplanned Area");
          }
        }
      };

//      downloadAreaButton.disableProperty().bind(preplannedAreasListView.getSelectionModel().selectedItemProperty().isNull());

      // add the listeners to the list view
      preplannedAreasListView.getSelectionModel().selectedItemProperty().addListener(listViewSelectionChangeViewpointListener);
      preplannedAreasListView.getSelectionModel().selectedItemProperty().addListener(listViewSelectionButtonLabelListener);

    } catch (IOException e) {
      new Alert(Alert.AlertType.ERROR, "Failed to create a temporary path for saving the Preplanned Map Areas.").show();
    }
  }

  /**
   * Changes the viewpoint to the area of interest of a preplanned map area.
   *
   * @param preplannedMapArea to change the viewpoint to.
   */
  private void changeViewPoint(PreplannedMapArea preplannedMapArea) {
    if (preplannedMapArea != null) {
      // get the extent of the area of interest, and add a buffer
      Envelope areaOfInterest = GeometryEngine.buffer(preplannedMapArea.getAreaOfInterest(), 100).getExtent();

      // set the point of view to the area of interest
      mapView.setViewpointAsync(new Viewpoint(areaOfInterest), 1.50F);
    }
  }

  /**
   * Downloads and shows the selected preplanned map area.
   */
  @FXML
  private void handleDownloadPreplannedAreaButtonClicked() {
    if (preplannedAreasListView.getSelectionModel().getSelectedItem() != null) {

      // remove the listener from the list view to stop the viewpoint changing on selection
      preplannedAreasListView.getSelectionModel().selectedItemProperty().removeListener(listViewSelectionChangeViewpointListener);
      // enable the 'show online map' button
      showOnlineMapButton.setDisable(false);
      // get the requested preplanned map area
      PreplannedMapArea selectedMapArea = preplannedAreasListView.getSelectionModel().getSelectedItem();
      // create a folder path where the map package will be downloaded to
      String path = tempDirectory + "/" + selectedMapArea.getPortalItem().getTitle();

      // download the preplanned area if it has not been downloaded previously
      if (!Files.exists(Paths.get(path))) {

        // disable the UI
        showOnlineMapButton.setDisable(true);
        downloadAreaButton.setDisable(true);
        deleteOfflineAreasButton.setDisable(true);

        // create download parameters from the task
        ListenableFuture<DownloadPreplannedOfflineMapParameters> downloadPreplannedOfflineMapParametersFuture = offlineMapTask.createDefaultDownloadPreplannedOfflineMapParametersAsync(selectedMapArea);
        downloadPreplannedOfflineMapParametersFuture.addDoneListener(() -> {
          try {
            DownloadPreplannedOfflineMapParameters downloadPreplannedOfflineMapParameters = downloadPreplannedOfflineMapParametersFuture.get();

            // set the parameters for the offline map to not receive updates
            downloadPreplannedOfflineMapParameters.setUpdateMode(PreplannedUpdateMode.NO_UPDATES);

            // create the job with the parameters and download path
            downloadPreplannedOfflineMapJob = offlineMapTask.downloadPreplannedOfflineMap(downloadPreplannedOfflineMapParameters, path);

            // show the job progress
            downloadProgressBar.setVisible(true);
            downloadPreplannedOfflineMapJob.addProgressChangedListener(() -> downloadProgressBar.setProgress((double) downloadPreplannedOfflineMapJob.getProgress() / 100));

            // start the job and wait for it to complete
            downloadPreplannedOfflineMapJob.start();
            downloadPreplannedOfflineMapJob.addJobDoneListener(() -> {

              // hide the progress bar
              downloadProgressBar.setVisible(false);

              // hide the graphics overlay with the areas of interest
              areasOfInterestGraphicsOverlay.setVisible(false);

              if (downloadPreplannedOfflineMapJob.getStatus() == Job.Status.SUCCEEDED) {

                // get the result of the job
                DownloadPreplannedOfflineMapResult downloadPreplannedOfflineMapResult = downloadPreplannedOfflineMapJob.getResult();

                // check if the result has any errors and display them
                checkForOfflineMapResultErrors(downloadPreplannedOfflineMapResult);

                // show the result in the map view
                mapView.setMap(downloadPreplannedOfflineMapResult.getOfflineMap());

                // add the package to the list of opened map packages
                if (!openedMobileMapPackages.contains(downloadPreplannedOfflineMapResult.getMobileMapPackage())) {
                  openedMobileMapPackages.add(downloadPreplannedOfflineMapResult.getMobileMapPackage());
                }

                // update the button text
                downloadAreaButton.setText("View Downloaded Area");

                // re-enable the UI
                showOnlineMapButton.setDisable(false);
                downloadAreaButton.setDisable(false);
                deleteOfflineAreasButton.setDisable(false);

                // display error details if the job fails
              } else if (downloadPreplannedOfflineMapJob.getStatus() == Job.Status.FAILED) {
                new Alert(Alert.AlertType.ERROR, "Download Preplanned Offline Map Job failed. Error: " + downloadPreplannedOfflineMapJob.getError());
              }
            });
          } catch (InterruptedException | ExecutionException e) {
            new Alert(Alert.AlertType.ERROR, "Could not create Default Parameters for the Download Preplanned Offline Map Job.").show();
          }
        });

        // if the area is already downloaded, open it
      } else {

        // hide the graphics overlay with the areas of interest
        areasOfInterestGraphicsOverlay.setVisible(false);

        // open and load the mobile map package
        localMapArea = new MobileMapPackage(path);
        localMapArea.loadAsync();
        // display the map from the mobile map package
        localMapArea.addDoneLoadingListener(() -> mapView.setMap(localMapArea.getMaps().get(0)));

        // add the mobile map package to the list of opened map packages
        if (!openedMobileMapPackages.contains(localMapArea)) {
          openedMobileMapPackages.add(localMapArea);
        }
      }

    } else {
      new Alert(Alert.AlertType.ERROR, "No Preplanned Map Area selected for downloading.").show();
    }
  }

  /**
   * Checks for layer and table errors of an offline map result, and displays them if present.
   *
   * @param downloadPreplannedOfflineMapResult the result to query for errors.
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
  private void handleDeleteOfflineAreasButtonClicked() {
    try {

      // reset the map view to the online map
      showOnlineMap();

      // close all previously opened geodatabases to allow deleting the files
      fetchAllGeodatabases().forEach(Geodatabase::close);

      // delete all files in the temporary directory
      File localPreplannedMapDirectoryFile = tempDirectory.toFile();
      FileUtils.cleanDirectory(localPreplannedMapDirectoryFile);

      // disable the 'delete offline areas' button
      deleteOfflineAreasButton.setDisable(true);

      // reset the UI
      preplannedAreasListView.getSelectionModel().select(null);
      downloadAreaButton.setText("Download Preplanned Area");

      // show confirmation
      new Alert(Alert.AlertType.INFORMATION, "All preplanned map areas deleted.").show();

    } catch (IOException e) {
      new Alert(Alert.AlertType.ERROR, "Error deleting preplanned map areas.").show();
    }
  }

  /**
   * Resets the map view to show the online map from the portal.
   */
  @FXML
  private void showOnlineMap() {
    // set the online map to the map view
    mapView.setMap(onlineMap);

    // show the graphics overlay with the areas of interest
    areasOfInterestGraphicsOverlay.setVisible(true);

    // add the listener to the list view to change the viewpoint on selection
    preplannedAreasListView.getSelectionModel().selectedItemProperty().addListener(listViewSelectionChangeViewpointListener);

    // change the viewpoint to the currently selected preplanned map area
    changeViewPoint(preplannedAreasListView.getSelectionModel().getSelectedItem());

    // disable the 'show online map' button
    showOnlineMapButton.setDisable(true);
  }

  /**
   * Returns all GeoDatabases from the previously opened MobileMapPackages.
   */
  private ArrayList<Geodatabase> fetchAllGeodatabases() {

    ArrayList<Geodatabase> geodatabases = new ArrayList<>();

    // get the geodatabases from all downloaded mobile map packages
    openedMobileMapPackages.forEach(mobileMapPackage -> {
      List<ArcGISMap> maps = mobileMapPackage.getMaps();
      maps.forEach(map -> {
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

    return geodatabases;
  }

  /**
   * Stops and releases all resources used in application.
   */
  void terminate() {

    // close all previously opened geodatabases to allow deleting the files
    fetchAllGeodatabases().forEach(Geodatabase::close);

    // delete all files in the temporary directory
    File localPreplannedMapDirectoryFile = tempDirectory.toFile();
    try {
      FileUtils.cleanDirectory(localPreplannedMapDirectoryFile);
    } catch (IOException e) {
      new Alert(Alert.AlertType.ERROR, "Error deleting preplanned map areas.").show();
    }

    if (mapView != null) {
      mapView.dispose();
    }
  }

}
