/*
 * Copyright 2015 Esri.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.esri.sampleviewer.samples.featurelayers;

import com.esri.arcgisruntime.datasource.Feature;
import com.esri.arcgisruntime.datasource.arcgis.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.MapView;

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
 * This sample demonstrates how to limit what Features are displayed on a Map.
 * <h4>How it Works</h4>
 * 
 * First a {@link FeatureLayer} is created from a {@link ServiceFeatureTable}.
 * Next, {@link FeatureLayer#setDefinitionExpression} method is called to set a
 * SQL where clause that filters the {@link Feature}s to be displayed.
 */
public class FeatureLayerDefinitionExpression extends Application {

  private MapView mapView;
  private FeatureLayer featureLayer;

  private final static String FEATURE_SERVICE_URL =
      "http://sampleserver6.arcgisonline.com/arcgis/rest/services/SF311/FeatureServer/0";

  @Override
  public void start(Stage stage) throws Exception {

    // create stack pane and application scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    scene.getStylesheets().add(getClass()
        .getResource("../resources/SamplesTheme.css").toExternalForm());

    // set title, size, and add scene to stage
    stage.setTitle("Feature Layer Definition Expression Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    // create a control panel
    VBox vBoxControl = new VBox(6);
    vBoxControl.setMaxSize(240, 260);
    vBoxControl.getStyleClass().add("panel-region");

    // create sample description
    Label descriptionLabel = new Label("Sample Description:");
    descriptionLabel.getStyleClass().add("panel-label");
    TextArea description = new TextArea(
        "This sample shows how to limit what Features are displayed on a Map.\n"
            + "Click Apply to filter by tree Features. Click Reset to remove the filter.");
    description.setWrapText(true);
    description.autosize();
    description.setEditable(false);

    // create buttons to apply/reset definition expression
    Button applyButton = new Button("Apply");
    Button resetButton = new Button("Reset");
    applyButton.setMaxWidth(Double.MAX_VALUE);
    resetButton.setMaxWidth(Double.MAX_VALUE);
    applyButton.setDisable(true);
    resetButton.setDisable(true);

    // set the definition expression
    applyButton.setOnAction(e -> {
      featureLayer
          .setDefinitionExpression("req_Type = 'Tree Maintenance or Damage'");
    });

    // reset the definition expression
    resetButton.setOnAction(e -> {
      featureLayer.setDefinitionExpression("");
    });

    // add label, sample description and buttons to the control panel
    vBoxControl.getChildren().addAll(descriptionLabel, description, applyButton,
        resetButton);
    try {

      // create service feature table
      final ServiceFeatureTable featureTable =
          new ServiceFeatureTable(FEATURE_SERVICE_URL);

      // create feature layer from service feature table
      featureLayer = new FeatureLayer(featureTable);

      // enable buttons when done loading
      featureLayer.addDoneLoadingListener(() -> {
        applyButton.setDisable(false);
        resetButton.setDisable(false);
      });

      // create a map using the basemap topographic
      final Map map = new Map(Basemap.createTopographic());

      // add the feature layer to the map
      map.getOperationalLayers().add(featureLayer);

      // create a view for this map and set map to it
      mapView = new MapView();
      mapView.setMap(map);

      // starting location for sample
      Point startPoint =
          new Point(-13630845, 4544861, SpatialReferences.getWebMercator());

      // set the viewpoint for the map view
      mapView.setViewpointCenterWithScaleAsync(startPoint, 150000);

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
