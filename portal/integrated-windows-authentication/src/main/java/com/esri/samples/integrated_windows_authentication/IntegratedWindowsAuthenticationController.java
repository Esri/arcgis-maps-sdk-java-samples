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

package com.esri.samples.integrated_windows_authentication;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
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

  @FXML private MapView mapView;
  @FXML private ListView<PortalItem> resultsListView;
  @FXML private TextField portalUrlTextField;
  @FXML private ProgressIndicator progressIndicator;
  @FXML private Text loadWebMapTextView;

  private Portal iwaSecuredPortal; // keeps loadable in scope to avoid garbage collection

  public void initialize() {
    try {

      // add a map to the map view
      ArcGISMap map = new ArcGISMap(Basemap.createStreets());
      mapView.setMap(map);

      // set authentication challenge handler
      AuthenticationManager.setAuthenticationChallengeHandler(new IWAChallengeHandler());

      // add a listener to the map results list view that loads the map on selection
      resultsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
        if (resultsListView.getSelectionModel().getSelectedItem() != null) {
          // show progress indicator while map is drawing
          progressIndicator.setVisible(true);

          // get the portal item ID from the selected list view item
          PortalItem portalItem = resultsListView.getSelectionModel().getSelectedItem();

          if (portalItem != null) {
            // create a Map using the web map (portal item)
            ArcGISMap webMap = new ArcGISMap(portalItem);
            // set the map to the map view
            mapView.setMap(webMap);
          }

          loadWebMapTextView.setText("Loaded web map from item " + portalItem);
        }
      });

      // make the list view show a preview of the portal items' map area
      resultsListView.setCellFactory(c -> new PortalItemInfoListCell());

      // hide the progress indicator when the map is finished drawing
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
   * Handles searching the provided IWA secured portal.
   */
  @FXML
  private void handleSearchPortalPress() {
    // check for a url in the URL field
    if (portalUrlTextField.getText().isEmpty()) {
      new Alert(Alert.AlertType.ERROR, "Portal URL is empty. Please enter a portal URL.").show();

    } else {
      // prefix the entered URL with 'https://' if it is not already
      String portalUrl = portalUrlTextField.getText();
      if (!portalUrl.startsWith("https://")){
        portalUrl = "https://" + portalUrl;
      }

      // keep hold of the portal we are searching and set a variable indicating that this is a secure portal, to allow retrieving portal items later
      iwaSecuredPortal = new Portal(portalUrl, true);

      // clear any existing items in the list view
      resultsListView.getItems().clear();

      // clear the information about the previously loaded map
      loadWebMapTextView.setText("");

      // show progress indicator
      progressIndicator.setVisible(true);

      // load the portal items
      iwaSecuredPortal.loadAsync();
      iwaSecuredPortal.addDoneLoadingListener(() -> {
        if (iwaSecuredPortal.getLoadStatus() == LoadStatus.LOADED) {

          // create portal query parameters searching for web maps
          PortalQueryParameters portalQueryParameters = new PortalQueryParameters(
                  "type:(\"web map\" NOT \"web mapping application\")");

          // search the portal for web maps
          ListenableFuture<PortalQueryResultSet<PortalItem>> portalItemResultFuture = iwaSecuredPortal.findItemsAsync(
            portalQueryParameters);
          portalItemResultFuture.addDoneListener(() -> {
            try {
              // get the result
              PortalQueryResultSet<PortalItem> portalItemSet = portalItemResultFuture.get();
              List<PortalItem> portalItems = portalItemSet.getResults();
              // add the IDs and titles of the portal items to the list view
              resultsListView.getItems().addAll(portalItems);

            } catch (ExecutionException | InterruptedException e) {
              new Alert(Alert.AlertType.ERROR, "Error getting portal item set from portal.").show();
            }
            // hide the progress indicator
            progressIndicator.setVisible(false);
          });

        } else {
          // hide the progress indicator
          progressIndicator.setVisible(false);

          // report error
          new Alert(Alert.AlertType.ERROR, "Portal sign in failed").show();
        }
      });
    }
  }

  /**
   * Stops and releases all resources used in the application.
   */
  void terminate() {

    if (mapView != null) {
      mapView.dispose();
    }
  }

}
