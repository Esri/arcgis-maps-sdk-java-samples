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

package com.esri.samples.portal.integrated_windows_authentication;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.DrawStatus;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import com.esri.arcgisruntime.portal.PortalQueryParameters;
import com.esri.arcgisruntime.portal.PortalQueryResultSet;
import com.esri.arcgisruntime.security.AuthenticationManager;

public class IntegratedWindowsAuthenticationController {

  @FXML
  private MapView mapView;
  @FXML
  private ListView<PortalItem> resultsListView;
  @FXML
  private TextField portalUrlTextField;
  @FXML
  private ProgressIndicator progressIndicator;
  @FXML
  private Text loadStateTextView;
  @FXML
  private Text loadWebMapTextView;

  public void initialize() {
    try {

      // create a streets base map
      ArcGISMap map = new ArcGISMap(Basemap.createStreets());

      // set the map to be displayed in the map view
      mapView.setMap(map);

      // set authentication challenge handler
      AuthenticationManager.setAuthenticationChallengeHandler(new IWAChallengeHandler());

      // add a listener to the map results list view that loads the map on selection
      resultsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> displayMap());

      // make the list view show a preview of the portal items' map area
      resultsListView.setCellFactory(c -> new PortalItemListCell());

      mapView.addDrawStatusChangedListener(drawStatusChangedEvent -> {
        if (drawStatusChangedEvent.getDrawStatus() == DrawStatus.COMPLETED) {
          progressIndicator.setVisible(false);
        }
      });

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Handles searching a public portal.
   */
  @FXML
  private void handleSearchPublicPress() {
    searchPortal(new Portal("http://www.arcgis.com"));
  }

  /**
   * Handles searching a secure portal.
   */
  @FXML
  private void handleSearchSecurePress() {
    // check for a url in the URL field
    if (!portalUrlTextField.getText().isEmpty()) {
      // search an instance of the IWA-secured portal, the user may be challenged for access
      searchPortal(new Portal(portalUrlTextField.getText(), true));
    } else {
      new Alert(Alert.AlertType.ERROR, "Portal URL is empty. Please enter a portal URL.").show();
    }
  }

  /**
   * Search the given portal for its portal items and display them in a list view.
   *
   * @param portal to search
   */
  private void searchPortal(Portal portal) {

    // check if the portal is null
    if (portal == null) {
      new Alert(Alert.AlertType.ERROR, "No portal provided").show();
      return;
    }

    // clear any existing items in the list view
    resultsListView.getItems().clear();

    // clear the information about the previously loaded map
    loadWebMapTextView.setText("");

    // show portal load state
    progressIndicator.setVisible(true);
    loadStateTextView.setText("Searching for web map items on the portal at " + portal.getUri());

    // load the portal items
    portal.loadAsync();
    portal.addDoneLoadingListener(() -> {
      if (portal.getLoadStatus() == LoadStatus.LOADED) {
        try {
          // update load state in UI with the portal URI
          loadStateTextView.setText("Connected to the portal on " + new URI(portal.getUri()).getHost());
        } catch (URISyntaxException e) {
          new Alert(Alert.AlertType.ERROR, "Error getting URI from portal: " + e.getMessage()).show();
        }

        // report the user name used for this connection
        if (portal.getUser() != null) {
          loadStateTextView.setText("Connected as: " + portal.getUser().getUsername());
        } else {
          // for a secure portal, the user should never be anonymous
          loadStateTextView.setText("Connected as: Anonymous");
        }

        // search the portal for web maps
        ListenableFuture<PortalQueryResultSet<PortalItem>> portalItemResultFuture = portal.findItemsAsync(new PortalQueryParameters("type:(\"web map\" NOT \"web mapping application\")"));
        portalItemResultFuture.addDoneListener(() -> {
          try {
            // get the result
            PortalQueryResultSet<PortalItem> portalItemSet = portalItemResultFuture.get();
            List<PortalItem> portalItems = portalItemSet.getResults();
            // add the items to the list view
            portalItems.forEach(portalItem -> resultsListView.getItems().add(portalItem));
          } catch (ExecutionException | InterruptedException e) {
            new Alert(Alert.AlertType.ERROR, "Error getting portal item set from portal: " + e.getMessage()).show();
          }
          // hide the progress indicator
          progressIndicator.setVisible(false);
        });

      } else {
        // hide the progress indicator
        progressIndicator.setVisible(false);
        // report error
        new Alert(Alert.AlertType.ERROR, "Portal sign in failed: " + portal.getLoadError().getCause().getMessage()).show();
      }
    });
  }

  /**
   * Gets the selected PortalItem from the list view, creates a new map from it and displays it in the map view.
   */
  private void displayMap() {
    if (resultsListView.getSelectionModel().getSelectedItem() != null) {
      // create a portal item from the selecttion in the list view
      PortalItem selectedItem = resultsListView.getSelectionModel().getSelectedItem();

      // set the map to the map view
      ArcGISMap webMap = new ArcGISMap(selectedItem);
      mapView.setMap(webMap);

      // show progress indicator while map is drawing
      progressIndicator.setVisible(true);

      loadWebMapTextView.setText("Loaded web map from item " + selectedItem.getItemId());
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
   * Shows the available thumbnail of the PortalItem map in the selection list view.
   */
  class PortalItemListCell extends ListCell<PortalItem> {

    @Override
    protected void updateItem(PortalItem portalItem, boolean empty) {
      super.updateItem(portalItem, empty);
      if (portalItem != null) {
        HBox hBox = new HBox();
        hBox.setMinHeight(65);

        // show the preplanned map area thumbnail image
        ImageView thumbnailImageView = new ImageView();
        hBox.getChildren().add(thumbnailImageView);
        thumbnailImageView.setFitHeight(65);
        thumbnailImageView.setPreserveRatio(true);
        ListenableFuture<byte[]> thumbnailData = portalItem.fetchThumbnailAsync();
        thumbnailData.addDoneListener(() -> {
          try {
            thumbnailImageView.setImage(new Image(new ByteArrayInputStream(thumbnailData.get())));
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        });

        setGraphic(hBox);

        // set the list cell's text to the map's index
        setText(portalItem.getTitle());
      } else {
        setGraphic(null);
        setText(null);
      }
    }
  }
}
