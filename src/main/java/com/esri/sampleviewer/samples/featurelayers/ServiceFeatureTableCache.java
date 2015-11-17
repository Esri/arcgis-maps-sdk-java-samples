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

import com.esri.arcgisruntime.datasource.arcgis.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This sample demonstrates how to use a feature service with a service feature
 * table in on-interaction-cache mode (which is the default mode for service
 * feature tables). How it works Set the
 * <@ServiceFeatureTable.FeatureRequestMode> property of the service feature
 * table to <b>ON_INTERACTION_CACHE</b> before the table is loaded. The mode
 * cannot be changed once the table has been loaded.
 */
public class ServiceFeatureTableCache extends Application {

  private MapView mapView;

  private static final String FEATURE_SERVICE_URL =
      "http://sampleserver6.arcgisonline.com/arcgis/rest/services/PoolPermits/FeatureServer/0";
  private static final String SAMPLES_THEME_PATH =
      "../resources/SamplesTheme.css";

  @Override
  public void start(Stage stage) throws Exception {

    // create stack pane and application scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    scene.getStylesheets()
        .add(getClass().getResource(SAMPLES_THEME_PATH).toExternalForm());

    // size the stage, add a title, and set scene to stage
    stage.setTitle("Feature Layer from Feature Server");
    stage.setHeight(700);
    stage.setWidth(800);
    stage.setScene(scene);
    stage.show();

    // create a control panel
    VBox vBoxControl = new VBox(6);
    vBoxControl.setMaxSize(250, 150);
    vBoxControl.getStyleClass().add("panel-region");

    // create sample label and description
    Label descriptionLabel = new Label("Sample Description");
    descriptionLabel.getStyleClass().add("panel-label");
    TextArea description = new TextArea(
        "This sample demonstrates how to use a feature service with a "
            + "Service Feature Table in on-interaction-cache mode.");
    description.setWrapText(true);
    description.autosize();
    description.setEditable(false);

    // add sample label and description to the control panel
    vBoxControl.getChildren().addAll(descriptionLabel, description);

    try {
      // create a view for this map
      mapView = new MapView();

      // create a map with the light Gray Canvas basemap
      Map map = new Map(Basemap.createLightGrayCanvas());

      // set an initial viewpoint
      map.setInitialViewpoint(new Viewpoint(new Envelope(-1.30758164047166E7,
          4014771.46954516, -1.30730056797177E7, 4016869.78617381, 0, 0, 0, 0,
          SpatialReferences.getWebMercator())));

      // create feature layer with its service feature table
      // create the service feature table
      ServiceFeatureTable serviceFeatureTable =
          new ServiceFeatureTable(FEATURE_SERVICE_URL);

      // explicitly set the mode to on interaction cache (which is also
      // the default mode for service feature tables)
      serviceFeatureTable.setFeatureRequestMode(
          ServiceFeatureTable.FeatureRequestMode.ON_INTERACTION_CACHE);

      // create the feature layer using the service feature table
      FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);

      // add the layer to the map
      map.getOperationalLayers().add(featureLayer);

      // set map to be displayed in map view
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
