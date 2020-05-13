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

import com.esri.arcgisruntime.ArcGISRuntimeException;
import com.esri.arcgisruntime.concurrent.Job;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
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
import com.esri.arcgisruntime.tasks.offlinemap.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class DownloadPreplannedMapController {

  @FXML private ListView<PreplannedMapArea> preplannedAreasListView;
  @FXML private ListView<DownloadPreplannedOfflineMapJob> downloadJobsListView;
  @FXML private MapView mapView;
  @FXML private Button downloadButton;

  private ArcGISMap onlineMap;
  private GraphicsOverlay areasOfInterestGraphicsOverlay;
  private OfflineMapTask offlineMapTask;
  private List<PreplannedMapArea> preplannedMapAreas; // keep loadable in scope to avoid garbage collection

  @FXML
  private void initialize() {
    try {

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
      SimpleRenderer areaOfInterestRenderer = new SimpleRenderer();
      areaOfInterestRenderer.setSymbol(areaOfInterestLineSymbol);
      areasOfInterestGraphicsOverlay.setRenderer(areaOfInterestRenderer);

      // create an offline map task for the portal item
      offlineMapTask = new OfflineMapTask(portalItem);

      // use a cell factory which shows the preplanned area's title
      preplannedAreasListView.setCellFactory(c -> new PreplannedMapAreaListCell());

      // get the preplanned map areas from the offline map task and show them in the list view
      ListenableFuture<List<PreplannedMapArea>> preplannedMapAreasFuture = offlineMapTask.getPreplannedMapAreasAsync();
      preplannedMapAreasFuture.addDoneListener(() -> {
        try {
          // get the preplanned areas and add them to the list view
          preplannedMapAreas = preplannedMapAreasFuture.get();
          preplannedAreasListView.getItems().addAll(preplannedMapAreas);

          // load each area and show a red border around their area of interest
          preplannedMapAreas.forEach(preplannedMapArea -> {
            preplannedMapArea.loadAsync();
            preplannedMapArea.addDoneLoadingListener(() -> {
              if (preplannedMapArea.getLoadStatus() == LoadStatus.LOADED) {
                areasOfInterestGraphicsOverlay.getGraphics().add(new Graphic(preplannedMapArea.getAreaOfInterest()));
              } else {
                new Alert(Alert.AlertType.ERROR, "Failed to load preplanned map area").show();
              }
            });
          });

        } catch (InterruptedException | ExecutionException e) {
          new Alert(Alert.AlertType.ERROR, "Failed to get the Preplanned Map Areas from the Offline Map Task.").show();
        }
      });


      preplannedAreasListView.getSelectionModel().selectedItemProperty().addListener((o, p, n) -> {
        PreplannedMapArea selectedPreplannedMapArea = preplannedAreasListView.getSelectionModel().getSelectedItem();
        if (selectedPreplannedMapArea != null) {

          // clear the download jobs list view selection
          downloadJobsListView.getSelectionModel().clearSelection();

          // show the online map with the areas of interest
          mapView.setMap(onlineMap);
          areasOfInterestGraphicsOverlay.setVisible(true);

          // set the viewpoint to the preplanned map area's area of interest
          Envelope areaOfInterest = GeometryEngine.buffer(selectedPreplannedMapArea.getAreaOfInterest(), 100).getExtent();
          mapView.setViewpointAsync(new Viewpoint(areaOfInterest), 1.5f);
        }
      });

      // disable the download button when no area is selected
      downloadButton.disableProperty().bind(preplannedAreasListView.getSelectionModel().selectedItemProperty().isNull());

      // use a cell factory which shows the download preplanned offline map job's progress and title
      downloadJobsListView.setCellFactory(c -> new DownloadPreplannedOfflineMapJobListCell());

      ChangeListener<DownloadPreplannedOfflineMapJob> selectedDownloadChangeListener = new ChangeListener<>() {
        @Override
        public void changed(ObservableValue<? extends DownloadPreplannedOfflineMapJob> observable, DownloadPreplannedOfflineMapJob oldValue, DownloadPreplannedOfflineMapJob newValue) {
          DownloadPreplannedOfflineMapJob selectedJob = downloadJobsListView.getSelectionModel().getSelectedItem();
          if (selectedJob != null) {

            // hide the preplanned map areas and clear the preplanned area list view's selection
            areasOfInterestGraphicsOverlay.setVisible(false);
            preplannedAreasListView.getSelectionModel().clearSelection();

            if (selectedJob.getStatus() == Job.Status.SUCCEEDED) {
              DownloadPreplannedOfflineMapResult result = selectedJob.getResult();

              // check if the result has errors
              if (result.hasErrors()) {

                // collect the layer and table errors into a single alert message
                StringBuilder stringBuilder = new StringBuilder("Errors: ");

                Map<Layer, ArcGISRuntimeException> layerErrors = result.getLayerErrors();
                layerErrors.forEach((layer, exception) ->
                        stringBuilder.append("Layer: ").append(layer.getName()).append(". Exception: ").append(exception.getMessage()).append(". ")
                );

                Map<FeatureTable, ArcGISRuntimeException> tableError = result.getTableErrors();
                tableError.forEach((table, exception) ->
                        stringBuilder.append("Table: ").append(table.getTableName()).append(". Exception: ").append(exception.getMessage()).append(". ")
                );

                new Alert(Alert.AlertType.ERROR, "One or more errors occurred with the Offline Map Result: " + stringBuilder.toString()).show();
              } else {
                // show the offline map in the map view
                ArcGISMap downloadOfflineMap = result.getOfflineMap();
                mapView.setMap(downloadOfflineMap);
              }

            } else {
              // alert the user the job is still in progress if selected before the job is done
              new Alert(Alert.AlertType.WARNING, "Job status: " + selectedJob.getStatus()).show();

              // when the job is done, re-trigger the listener to show the job's result if it is still selected
              selectedJob.addJobDoneListener(() ->
                this.changed(observable, oldValue, downloadJobsListView.getSelectionModel().getSelectedItem())
              );
            }
          }
        }
      };

      downloadJobsListView.getSelectionModel().selectedItemProperty().addListener(selectedDownloadChangeListener);

    } catch (Exception e) {
      // on any exception, print the stacktrace
      e.printStackTrace();
    }
  }

  /**
   * Download the selected preplanned map area from the list view to a temporary directory. The download job is tracked in another list view.
   */
  @FXML
  private void handleDownloadPreplannedAreaButtonClicked() {
    PreplannedMapArea selectedMapArea = preplannedAreasListView.getSelectionModel().getSelectedItem();
    if (selectedMapArea != null) {

      // hide the preplanned areas and clear the selection
      preplannedAreasListView.getSelectionModel().clearSelection();

      // create default download parameters from the offline map task
      ListenableFuture<DownloadPreplannedOfflineMapParameters> downloadPreplannedOfflineMapParametersFuture = offlineMapTask.createDefaultDownloadPreplannedOfflineMapParametersAsync(selectedMapArea);
      downloadPreplannedOfflineMapParametersFuture.addDoneListener(() -> {
        try {
          DownloadPreplannedOfflineMapParameters downloadPreplannedOfflineMapParameters = downloadPreplannedOfflineMapParametersFuture.get();

          // set the update mode to not receive updates
          downloadPreplannedOfflineMapParameters.setUpdateMode(PreplannedUpdateMode.NO_UPDATES);

          // create a job to download the preplanned offline map to a temporary directory
          Path path = Files.createTempDirectory(selectedMapArea.getPortalItem().getTitle());
          path.toFile().deleteOnExit();
          DownloadPreplannedOfflineMapJob downloadPreplannedOfflineMapJob = offlineMapTask.downloadPreplannedOfflineMap(downloadPreplannedOfflineMapParameters, path.toFile().getAbsolutePath());

          // start the job
          downloadPreplannedOfflineMapJob.start();

          // track the job in the second list view
          downloadJobsListView.getItems().add(downloadPreplannedOfflineMapJob);

        } catch (InterruptedException | ExecutionException e) {
          new Alert(Alert.AlertType.ERROR, "Failed to generate default parameters for the download job.").show();
        } catch(IOException e) {
          new Alert(Alert.AlertType.ERROR, "Failed to create a temporary directory for the download").show();
        }
      });
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  void terminate() {

    if (mapView != null) {
      mapView.dispose();
    }
  }

}
