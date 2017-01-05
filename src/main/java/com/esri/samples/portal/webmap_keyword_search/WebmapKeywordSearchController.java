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

package com.esri.samples.portal.webmap_keyword_search;

import java.util.List;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import com.esri.arcgisruntime.portal.PortalQueryParameters;
import com.esri.arcgisruntime.portal.PortalQueryResultSet;
import com.esri.arcgisruntime.security.AuthenticationManager;
import com.esri.arcgisruntime.security.OAuthConfiguration;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class WebmapKeywordSearchController {


  @FXML private TextField keyword;
  @FXML private MapView mapView;
  @FXML private ListView<PortalItem> resultsList;
  @FXML private Button moreButton;

  private Portal portal;
  private PortalQueryResultSet<PortalItem> portalQueryResultSet;

  @FXML
  private void initialize() {
    // load a portal for arcgis.com
    portal = new Portal("http://arcgis.com");
    portal.loadAsync();

    resultsList.setCellFactory(c -> new PortalItemCell());

    // show the selected webmap in a mapview
    resultsList.getSelectionModel().selectedItemProperty().addListener(o -> {
      PortalItem webmap = resultsList.getSelectionModel().getSelectedItem();
      if (webmap != null) {
        webmap.loadAsync();
        mapView.setMap(new ArcGISMap(webmap));
        // check if webmap supported
        mapView.getMap().addDoneLoadingListener(() -> {
          if (mapView.getMap().getLoadError() != null) {
            showMessage("Unable to load map", mapView.getMap().getLoadError().getMessage(), Alert.AlertType.ERROR);
          }
        });
      }
    });
  }

  /**
   * Searches a portal for webmaps matching query string in keyword textfield. The list view is updated with
   * the results.
   */
  @FXML
  private void search() {

    // create query parameters specifying the type WEBMAP
    PortalQueryParameters params = new PortalQueryParameters();
    params.setQuery(PortalItem.Type.WEBMAP, null, keyword.getText());

    // find matching portal items
    ListenableFuture<PortalQueryResultSet<PortalItem>> results = portal.findItemsAsync(params);
    results.addDoneListener(() -> {
      try {
        // update the results list view with matching items
        portalQueryResultSet = results.get();
        List<PortalItem> portalItems = portalQueryResultSet.getResults();
        resultsList.getItems().clear();
        resultsList.getItems().addAll(portalItems);
        moreButton.setDisable(false);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  /**
   * Adds the next set of results to the list view.
   */
  @FXML
  private void getMoreResults() {
    if (portalQueryResultSet.getNextQueryParameters() != null) {
      // find matching portal items
      ListenableFuture<PortalQueryResultSet<PortalItem>> results = portal.findItemsAsync(portalQueryResultSet.getNextQueryParameters());
      results.addDoneListener(() -> {
        try {
          // replace the result set with the current set of results
          portalQueryResultSet = results.get();
          List<PortalItem> portalItems =portalQueryResultSet.getResults();

          // add set of results to list view
          resultsList.getItems().addAll(portalItems);
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
    } else {
      showMessage("End of results", "There are no more results matching this query", Alert.AlertType.INFORMATION);
      moreButton.setDisable(true);
    }
  }

  /**
   * Shows a Layer title in a ListView.
   */
  private class PortalItemCell extends ListCell<PortalItem> {
    @Override
    protected void updateItem(PortalItem portalItem, boolean empty) {
      super.updateItem(portalItem, empty);
      setText(empty ? null : portalItem.getTitle());
      setGraphic(null);
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
