/*
 * Copyright 2015 Esri.
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

package com.esri.sampleviewer.samples.featurelayers;

import java.util.concurrent.atomic.AtomicInteger;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.datasource.FeatureQueryResult;
import com.esri.arcgisruntime.datasource.QueryParameters;
import com.esri.arcgisruntime.datasource.arcgis.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.Viewpoint;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This sample demonstrates how to set Manual Cache mode on a
 * ServiceFeatureTable.
 * <p>
 * Manual Cache mode only fetches Features when
 * {@link ServiceFeatureTable#populateFromServiceAsync} is called. If you know
 * what Features you need ahead of time, this is the best mode.
 * <h4>How it Works</h4>
 * 
 * A {@link ServiceFeatureTable} is created from a URL, then the Manual Cache
 * mode is set by passing ServiceFeatureTable.FeatureRequestMode.MANUAL_CACHE to
 * the {@link ServiceFeatureTable#setFeatureRequestMode} method. A
 * {@link FeatureLayer} is then created using this ServiceFeatureTable.
 * <h4>Implementation Requirements</h4>
 * 
 * Manual Cache mode must be set before the ServiceFeatureTable is loaded.
 * <p>
 * ListenableFuture needs to be a class level field because it could get garbage
 * collected right after being set. Meaning that the addDoneListener method will
 * never be called.
 */
public class ServiceFeatureTableManualCache extends Application {

  private MapView mapView;
  private Label featuresReturnLabel;
  private ServiceFeatureTable featureTable;
  private ListenableFuture<FeatureQueryResult> tableResult;

  private static final String SERVICE_FEATURE_URL =
      "http://sampleserver6.arcgisonline.com/arcgis/rest/services/SF311/FeatureServer/0";

  @Override
  public void start(Stage stage) throws Exception {

    // create stack pane and application scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    scene.getStylesheets().add(getClass()
        .getResource("../resources/SamplesTheme.css").toExternalForm());

    // set title, size, and add scene to stage
    stage.setTitle("Service Feature Table Manual Cache Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    // create a control panel
    VBox vBoxControl = new VBox(6);
    vBoxControl.setMaxSize(250, 270);
    vBoxControl.getStyleClass().add("panel-region");

    // create sample description
    Label descriptionLabel = new Label("Sample Description:");
    descriptionLabel.getStyleClass().add("panel-label");
    TextArea description = new TextArea(
        "This sample shows how to set 'MANUAL_CACHE' mode to a Service Feature Table.\n"
            + "To manually request the cache, click the button to request the cache "
            + "from the Service Feature Table.");
    description.setWrapText(true);
    description.autosize();
    description.setEditable(false);

    // create button to request the service table's cache
    Button requestCacheButton = new Button("Request Cache");
    requestCacheButton.setMaxWidth(Double.MAX_VALUE);
    requestCacheButton.setDisable(true);

    requestCacheButton.setOnAction(e -> fetchCacheManually());

    // create a label to display number of features returned
    featuresReturnLabel = new Label("Features Returned: ");
    featuresReturnLabel.getStyleClass().add("panel-label");

    // add labels, sample description, and button to the control panel
    vBoxControl.getChildren().addAll(descriptionLabel, description,
        requestCacheButton, featuresReturnLabel);
    try {

      // create service feature table from a url
      featureTable = new ServiceFeatureTable(SERVICE_FEATURE_URL);

      // set request mode of service feature table to manual cache
      featureTable.setFeatureRequestMode(
          ServiceFeatureTable.FeatureRequestMode.MANUAL_CACHE);

      featureTable.loadAsync();

      // create a feature layer from the service feature table
      final FeatureLayer featureLayer = new FeatureLayer(featureTable);

      // enable button when feature layer is done loading
      featureLayer.addDoneLoadingListener(() -> {
        requestCacheButton.setDisable(false);
      });

      // create a map with topographic basemap
      Map map = new Map(Basemap.createTopographic());

      // add feature layer to the map
      map.getOperationalLayers().add(featureLayer);

      // create a view for this map and set map to it
      mapView = new MapView();
      mapView.setMap(map);

      // set the starting viewpoint for the map view
      mapView.setViewpoint(new Viewpoint(
          new Point(-13630484, 4545415, SpatialReferences.getWebMercator()),
          150000));

      // add the map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView, vBoxControl);
      StackPane.setAlignment(vBoxControl, Pos.TOP_LEFT);
      StackPane.setMargin(vBoxControl, new Insets(10, 0, 0, 10));
    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Fetches the cache from a Service Feature Table manually.
   */
  private void fetchCacheManually() {

    // create query to select all tree or damage features
    QueryParameters queryParams = new QueryParameters();
    queryParams.getOutFields().add("*"); // all features
    queryParams.setWhereClause("req_type = 'Tree Maintenance or Damage'");

    // get queried features from service feature table and clear previous cache
    tableResult = featureTable.populateFromServiceAsync(queryParams, true);

    tableResult.addDoneListener(() -> {
      try {
        // find the number of features returned from query
        AtomicInteger featuresReturned = new AtomicInteger();
        tableResult.get()
            .forEach(feature -> featuresReturned.getAndIncrement());

        // display to user how many features where returned
        Platform.runLater(() -> featuresReturnLabel
            .setText("Features Returned: " + featuresReturned));
      } catch (Exception e) {
        // on any error, display the stack trace
        e.printStackTrace();
      }
    });
  }

  /**
   * Stops and releases all resources used in application.
   *
   * @throws Exception if security manager doesn't allow JVM to exit with
   *           current status
   */
  @Override
  public void stop() throws Exception {

    if (mapView != null) {
      mapView.dispose();
    }
    Platform.exit();
    System.exit(0);
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
