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
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This sample shows how to use a layer from an ArcGIS feature service as a
 * feature layer. How it works: first create a service feature table using the
 * URL to the layer in the feature service you want to use. This is the data
 * source. Then, create a <@FeatureLayer> and pass in the service feature table
 * you have created. Add the feature layer to a map then set the map on a map
 * view and the layer will be displayed using default modes and properties as
 * defined on the service.
 */
public class FeatureLayerFeatureService extends Application {

  private MapView mapView;

  private static final String GEOLOGY_FEATURE_SERVICE =
      "http://sampleserver6.arcgisonline.com/arcgis/rest/services/Energy/Geology/FeatureServer/9";
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
    vBoxControl.setMaxSize(240, 120);
    vBoxControl.getStyleClass().add("panel-region");

    // create sample label and description
    Label descriptionLabel = new Label("Sample Description");
    descriptionLabel.getStyleClass().add("panel-label");

    TextArea description = new TextArea("This sample shows how to use a layer"
        + " from an ArcGIS feature service as a feature layer.");
    description.setWrapText(true);
    description.autosize();
    description.setEditable(false);

    // add sample label and description to the control panel
    vBoxControl.getChildren().addAll(descriptionLabel, description);

    try {
      // create a view for this map
      mapView = new MapView();

      // create a map with the terrain with labels basemap
      Map map = new Map(Basemap.createTerrainWithLabels());

      // set an initial viewpoint
      map.setInitialViewpoint(new Viewpoint(
          new Point(-13176752, 4090404, SpatialReferences.getWebMercator()),
          500000));

      // create feature layer with its service feature table
      // create the service feature table
      ServiceFeatureTable serviceFeatureTable =
          new ServiceFeatureTable(GEOLOGY_FEATURE_SERVICE);

      // create the feature layer using the service feature table
      FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);

      // add the layer to the map
      map.getOperationalLayers().add(featureLayer);

      // set the map to be displayed in the view
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
