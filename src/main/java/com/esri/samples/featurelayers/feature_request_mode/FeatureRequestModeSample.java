/*
 * Copyright 2018 Esri.
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

package com.esri.samples.featurelayers.feature_request_mode;

import java.util.Collections;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;

public class FeatureRequestModeSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // size the stage, add a title, and set scene to stage
      stage.setTitle("Feature Request Mode Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a map view
      mapView = new MapView();

      // create a map with a dark gray basemap and set it to the map view
      ArcGISMap map = new ArcGISMap(Basemap.createDarkGrayCanvasVector());
      mapView.setMap(map);

      // set an initial viewpoint near the data (Portland, OR)
      map.setInitialViewpoint(new Viewpoint(new Point(-13653672.875600, 5706372.417150, SpatialReferences.getWebMercator()), 300000));

      // create a button to request a cache of features in manual cache mode
      Button requestCacheButton = new Button("Request cache");
      requestCacheButton.setDisable(true);

      // create a combo box to select the feature request mode
      ComboBox<ServiceFeatureTable.FeatureRequestMode> featureRequestModeComboBox = new ComboBox<>();
      featureRequestModeComboBox.getItems().addAll(
          ServiceFeatureTable.FeatureRequestMode.MANUAL_CACHE,
          ServiceFeatureTable.FeatureRequestMode.ON_INTERACTION_CACHE,
          ServiceFeatureTable.FeatureRequestMode.ON_INTERACTION_NO_CACHE
      );

      // when the request mode is selected, clear the map and create a new feature layer using the selected request mode
      featureRequestModeComboBox.getSelectionModel().selectedItemProperty().addListener(observable -> {

        // clear previous layers
        map.getOperationalLayers().clear();

        // get the selected feature request mode
        ServiceFeatureTable.FeatureRequestMode featureRequestMode = featureRequestModeComboBox.getSelectionModel().getSelectedItem();

        // create a feature table from a feature service URL
        ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable("https://services2.arcgis.com/ZQgQTuoyBrtmoGdP/arcgis/rest/services/Trees_of_Portland/FeatureServer/0");

        // set the feature request mode before creating the layer
        serviceFeatureTable.setFeatureRequestMode(featureRequestMode);

        // create a feature layer with the feature table
        FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);

        // add the feature layer to the map
        map.getOperationalLayers().add(featureLayer);

        // when the request mode is manual cache, enable a button to manually request a subset of the table's features
        if (featureRequestMode == ServiceFeatureTable.FeatureRequestMode.MANUAL_CACHE) {
          requestCacheButton.setDisable(false);
          requestCacheButton.setOnAction(e -> {
            // create query to select all tree with a "fair" condition
            QueryParameters queryParams = new QueryParameters();
            queryParams.setWhereClause("\"Condition\" = 2");
            // return all of the features' fields
            List<String> outfields = Collections.singletonList("*");
            // get queried features from service feature table and clear previous cache
            // the populated features will automatically get displayed in the feature layer
            serviceFeatureTable.populateFromServiceAsync(queryParams, true, outfields);
          });
        } else {
          // disable the button if the request mode is not manual cache
          requestCacheButton.setDisable(true);
        }

      });

      // start with ON_INTERACTION_CACHE (the default mode) selected in the combo box
      featureRequestModeComboBox.getSelectionModel().select(ServiceFeatureTable.FeatureRequestMode.ON_INTERACTION_CACHE);

      // add the map view and UI controls to the stack pane
      stackPane.getChildren().addAll(mapView, featureRequestModeComboBox, requestCacheButton);
      StackPane.setAlignment(featureRequestModeComboBox, Pos.TOP_LEFT);
      StackPane.setAlignment(requestCacheButton, Pos.TOP_RIGHT);
      StackPane.setMargin(featureRequestModeComboBox, new Insets(10));
      StackPane.setMargin(requestCacheButton, new Insets(10));

    } catch (Exception e) {
      // on any error, display stack trace
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

    // release resources when the application closes
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
