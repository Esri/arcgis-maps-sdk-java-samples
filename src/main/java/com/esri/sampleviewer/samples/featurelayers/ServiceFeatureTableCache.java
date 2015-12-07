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

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.esri.arcgisruntime.datasource.arcgis.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.Viewpoint;

/**
 * This sample demonstrates how to create a ServiceFeatureTable with cache mode
 * set to ON_INTERACTION_CACHE (this is the default mode for a Service Feature
 * Table).
 * <p>
 * ON_INTERACTION_CACHE retrieves data from the server when it is needed,
 * example would be after a pan or zoom.
 * <h4>How it Works</h4>
 * 
 * A {@link ServiceFeatureTable} is created from a URL,then the cache mode is
 * set by passing ServiceFeatureTable.FeatureRequestMode.ON_INTERACTION_CACHE to
 * {@link ServiceFeatureTable#setFeatureRequestMode}.
 */
public class ServiceFeatureTableCache extends Application {

  private MapView mapView;

  private static final String SERVICE_FEATURE_URL =
      "http://sampleserver6.arcgisonline.com/arcgis/rest/services/PoolPermits/FeatureServer/0";
  private static final String SAMPLES_THEME_PATH =
      "../resources/SamplesTheme.css";

  @Override
  public void start(Stage stage) throws Exception {

    // create stack pane and application scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    scene.getStylesheets().add(getClass().getResource(SAMPLES_THEME_PATH)
        .toExternalForm());

    // set title, size, and add scene to stage
    stage.setTitle("Feature Layer from Feature Server Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    // create a control panel
    VBox vBoxControl = new VBox(6);
    vBoxControl.setMaxSize(250, 190);
    vBoxControl.getStyleClass().add("panel-region");

    // create sample label and description
    Label descriptionLabel = new Label("Sample Description");
    descriptionLabel.getStyleClass().add("panel-label");
    TextArea description = new TextArea(
        "This sample demonstrates how to create a Service Feature Table "
            + "with cache mode set to 'ON_INTERACTION_CACHE'.");
    description.setWrapText(true);
    description.autosize();
    description.setEditable(false);

    // add sample label and description to the control panel
    vBoxControl.getChildren().addAll(descriptionLabel, description);
    try {

      // create a map with the light Gray Canvas basemap
      final Map map = new Map(Basemap.createLightGrayCanvas());

      // set an initial viewpoint
      Point leftPoint = new Point(-1.30758164047166E7, 4014771.46954516,
          SpatialReferences.getWebMercator());
      Point rightPoint = new Point(-1.30730056797177E7, 4016869.78617381,
          SpatialReferences.getWebMercator());

      map.setInitialViewpoint(new Viewpoint(new Envelope(leftPoint,
          rightPoint)));

      // create the service feature table from url
      final ServiceFeatureTable serviceFeatureTable =
          new ServiceFeatureTable(SERVICE_FEATURE_URL);

      // explicitly set the mode to on interaction cache
      serviceFeatureTable.setFeatureRequestMode(
          ServiceFeatureTable.FeatureRequestMode.ON_INTERACTION_CACHE);

      // create the feature layer using the service feature table
      final FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);

      // add the layer to the map
      map.getOperationalLayers().add(featureLayer);

      // create a view for this map and set map to it
      mapView = new MapView();
      mapView.setMap(map);

      // add the map view and control box to stack pane
      stackPane.getChildren().addAll(mapView, vBoxControl);
      StackPane.setAlignment(vBoxControl, Pos.TOP_LEFT);
      StackPane.setMargin(vBoxControl, new Insets(10, 0, 0, 10));
    } catch (Exception e) {
      // on any error, display stack trace
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   * 
   * @throws Exception if security manager doesn't allow JVM to exit with
   *           current status
   */
  @Override
  public void stop() throws Exception {

    // release resources when the application closes
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
