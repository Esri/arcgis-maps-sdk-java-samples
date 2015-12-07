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
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.Viewpoint;

/**
 * This sample shows how to create a FeatureLayer from a ServiceFeatureTable and
 * add it to the Map.
 * <p>
 * A {@link FeatureLayer} allows us to work with Features from a table, like
 * displaying Features to Map.
 * <h4>How it Works</h4>
 * 
 * First a {@link ServiceFeatureTable} is created from a URL and then a
 * FeatureLayer is created by supplying that table. Using the
 * {@link Map#getOperationalLayers} method the FeatureLayer is added to the Map
 * then displayed in the view by setting the Map to the {@link MapView#setMap}
 * method.
 */
public class FeatureLayerFeatureService extends Application {

  private MapView mapView;

  private static final String SERVICE_FEATURE_URL =
      "http://sampleserver6.arcgisonline.com/arcgis/rest/services/Energy/Geology/FeatureServer/9";
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
    TextArea description = new TextArea("This sample shows how to create a "
        + "Feature Layer from a Service Feature Table and add it to the Map.");
    description.setWrapText(true);
    description.autosize();
    description.setEditable(false);

    // add sample label and description to the control panel
    vBoxControl.getChildren().addAll(descriptionLabel, description);
    try {

      // create a map with the terrain with labels basemap
      final Map map = new Map(Basemap.createTerrainWithLabels());

      // set an initial viewpoint
      Point startPoint = new Point(-13176752, 4090404,
          SpatialReferences.getWebMercator());

      map.setInitialViewpoint(new Viewpoint(startPoint, 500000));//point and scale

      // create service feature table from URL
      final ServiceFeatureTable featureTable = new ServiceFeatureTable(
          SERVICE_FEATURE_URL);

      // create the feature layer using the service feature table
      final FeatureLayer featureLayer = new FeatureLayer(featureTable);

      // add the layer to the map
      map.getOperationalLayers().add(featureLayer);

      // create a view and set map to it
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
