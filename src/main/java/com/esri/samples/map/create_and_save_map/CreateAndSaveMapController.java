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

package com.esri.samples.map.create_and_save_map;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalFolder;
import com.esri.arcgisruntime.portal.PortalItem;
import com.esri.arcgisruntime.portal.PortalUserContent;
import com.esri.arcgisruntime.security.AuthenticationManager;
import com.esri.arcgisruntime.security.OAuthConfiguration;

public class CreateAndSaveMapController {

  @FXML private MapView mapView;
  @FXML private TextField title;
  @FXML private TextField tags;
  @FXML private TextArea description;
  @FXML private ComboBox<PortalFolder> folderList;
  @FXML private ListView<Basemap> basemapList;
  @FXML private ListView<Layer> layersList;
  @FXML private Button saveButton;
  @FXML private ProgressIndicator progress;

  private ArcGISMap map;
  private Portal portal;

  @FXML
  private void initialize() {

    // set basemap options
    basemapList.getItems().addAll(Basemap.createStreets(), Basemap.createImagery(), Basemap
        .createTopographic(), Basemap.createOceans());

    // update basemap when selection changes
    basemapList.getSelectionModel().select(0);
    basemapList.getSelectionModel().selectedItemProperty()
        .addListener(o -> map.setBasemap(basemapList.getSelectionModel().getSelectedItem()));

    basemapList.setCellFactory(c -> new BasemapCell());

    // create and set a map with the first basemap option
    map = new ArcGISMap(basemapList.getSelectionModel().getSelectedItem());
    mapView.setMap(map);

    // set operational layer options
    String worldElevationService =
        "http://sampleserver6.arcgisonline.com/arcgis/rest/services/WorldTimeZones/MapServer";
    ArcGISMapImageLayer worldElevation = new ArcGISMapImageLayer(worldElevationService);
    worldElevation.loadAsync();

    String worldCensusService = "http://sampleserver6.arcgisonline.com/arcgis/rest/services/Census/MapServer";
    ArcGISMapImageLayer worldCensus = new ArcGISMapImageLayer(worldCensusService);
    worldCensus.loadAsync();

    layersList.getItems().addAll(worldElevation, worldCensus);
    layersList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    layersList.getSelectionModel().selectedItemProperty().addListener(o -> {
      map.getOperationalLayers().clear();
      map.getOperationalLayers().addAll(layersList.getSelectionModel().getSelectedItems());
    });

    layersList.setCellFactory(c -> new LayerCell());

    // set portal folder title converter
    folderList.setConverter(new StringConverter<PortalFolder>() {

      @Override
      public String toString(PortalFolder folder) {
        return folder.getTitle();
      }

      @Override
      public PortalFolder fromString(String string) {
        return null;
      }
    });
  }

  /**
   * Open a dialog to create and log into a portal.
   */
  void authenticate() {

    AuthenticationDialog authenticationDialog = new AuthenticationDialog();
    authenticationDialog.show();
    authenticationDialog.setOnCloseRequest(r -> {

      OAuthConfiguration configuration = authenticationDialog.getResult();
      // check authentication went through
      if (configuration != null) {
        AuthenticationManager.addOAuthConfiguration(configuration);

        // setup the handler that will prompt an authentication challenge to the user
        AuthenticationManager.setAuthenticationChallengeHandler(new OAuthChallengeHandler());

        portal = new Portal("http://" + configuration.getPortalUrl(), true);
        portal.addDoneLoadingListener(() -> {
          if (portal.getLoadStatus() == LoadStatus.LOADED) {
            try {
              PortalUserContent portalUserContent = portal.getUser().fetchContentAsync().get();
              List<PortalFolder> portalFolders = portalUserContent.getFolders();
              folderList.getItems().addAll(portalFolders);
            } catch (Exception e) {
              e.printStackTrace();
            }

            saveButton.setDisable(false);

          } else if (portal.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {

            // show alert message on error
            showMessage("Authentication failed", portal.getLoadError().getMessage(), Alert.AlertType.ERROR);
          }
        });

        // loading the portal info of a secured resource
        // this will invoke the authentication challenge
        portal.loadAsync();
      }
    });

  }

  /**
   * Shows a Basemap title in a ListView.
   */
  private class BasemapCell extends ListCell<Basemap> {

    @Override
    protected void updateItem(Basemap basemap, boolean empty) {
      super.updateItem(basemap, empty);
      setText(empty || basemap == null ? null : basemap.getName());
      setGraphic(null);
    }
  }

  /**
   * Shows a Layer title in a ListView.
   */
  private class LayerCell extends ListCell<Layer> {

    @Override
    protected void updateItem(Layer layer, boolean empty) {
      super.updateItem(layer, empty);
      setText(empty || layer == null ? null : layer.getName());
      setGraphic(null);
    }
  }

  /**
   * Save the map to the portal.
   */
  @FXML
  private void saveMap() {
    progress.setVisible(true);
    try {
      ListenableFuture<PortalItem> result = map.saveAsAsync(portal, folderList.getSelectionModel().getSelectedItem(),
          title.getText(), Arrays.asList(tags.getText().split(",")), description.getText(), null, true);
      result.addDoneListener(() -> {
        try {
          PortalItem portalItem = result.get();
          showMessage("Save Successful", "Map titled " + title.getText() + " saved to portal item with id: " +
              portalItem.getItemId(), Alert.AlertType.INFORMATION);
        } catch (InterruptedException | ExecutionException e) {
          showMessage("Save Unscuccessful", e.getCause().getMessage(), Alert.AlertType.ERROR);
        } finally {
          progress.setVisible(false);
        }
      });
    } catch (Exception e) {
      progress.setVisible(false);
      showMessage("Could not save map", e.getMessage(), Alert.AlertType.ERROR);
    }
  }

  /**
   * Display an alert to the user with the specified information.
   * @param title alert title
   * @param description alert content description
   * @param type alert type
   */
  private void showMessage(String title, String description, Alert.AlertType type) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setContentText(description);
    alert.show();
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
