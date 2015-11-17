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

package com.esri.sampleviewer.samples.mapview;

import com.esri.arcgisruntime.datasource.arcgis.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.DrawStatus;
import com.esri.arcgisruntime.mapping.view.DrawStatusChangedEvent;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.Viewpoint;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This sample demonstrates how to use the MapView's DrawStatus.
 * <p>
 * {@link DrawStatus} has two states:
 * <li>IN_PROGRESS which lets you know that drawing has begun.</li>
 * <li>COMPLETED to let you know drawing has finished.</li>
 * <h4>How it Works</h4>
 * 
 * A {@link DrawStatusChangedEvent} fires whenever the DrawStatus of the MapView
 * changes. Listen for this event using
 * {@link MapView#addDrawStatusChangedListener}.
 */
public class DisplayDrawingStatus extends Application {

  private MapView mapView;

  private static final String SERVICE_FEATURE_URL =
      "http://sampleserver6.arcgisonline.com/arcgis/rest/services/DamageAssessment/FeatureServer/0";

  @Override
  public void start(Stage stage) throws Exception {

    // create stack pane and application scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    scene.getStylesheets().add(getClass()
        .getResource("../resources/SamplesTheme.css").toExternalForm());

    // set title, size, and add scene to stage
    stage.setTitle("Display Drawing Status Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    // create a control panel
    VBox vBoxControl = new VBox(6);
    vBoxControl.setMaxSize(250, 180);
    vBoxControl.getStyleClass().add("panel-region");

    // create sample description
    Label descriptionLabel = new Label("Sample Description:");
    descriptionLabel.getStyleClass().add("panel-label");
    TextArea description = new TextArea(
        "This sample shows how to use the DrawStatus event to notify "
            + "the user when the Map has finished drawing.");
    description.setWrapText(true);
    description.autosize();
    description.setEditable(false);

    // create progress bar
    ProgressBar progressBar = new ProgressBar();
    progressBar.setMaxWidth(Double.MAX_VALUE);

    // add controls to the user interface panel
    vBoxControl.getChildren().addAll(descriptionLabel, description,
        progressBar);
    try {

      // create a map with topographic basemap
      final Map map = new Map(Basemap.createTopographic());

      // create a starting viewpoint for the map
      SpatialReference spatialReference = SpatialReferences.getWebMercator();
      Point bottomLeftPoint =
          new Point(-13639984.0, 4537387.0, spatialReference);
      Point topRightPoint = new Point(-13606734.0, 4558866, spatialReference);
      Envelope envelope = new Envelope(bottomLeftPoint, topRightPoint);
      Viewpoint viewpoint = new Viewpoint(envelope);

      // set the initial viewpoint of map to viewpoint
      map.setInitialViewpoint(viewpoint);

      // create a feature table from a service URL
      final ServiceFeatureTable featuretable =
          new ServiceFeatureTable(SERVICE_FEATURE_URL);

      // create a feature layer from service table
      final FeatureLayer featureLayer = new FeatureLayer(featuretable);

      // add feature layer to map
      map.getOperationalLayers().add(featureLayer);

      // create a view for this map
      mapView = new MapView();

      // set map to be displayed in map view
      mapView.setMap(map);

      mapView.addDrawStatusChangedListener(e -> {
        // check to see if draw status is in progress
        if (e.getDrawStatus() == DrawStatus.IN_PROGRESS) {
          // set progress bar to be visible
          progressBar.setVisible(true);

          // check to see if draw status is complete
        } else if (e.getDrawStatus() == DrawStatus.COMPLETED) {
          // set progress bar to be invisible
          progressBar.setVisible(false);
        }
      });

      // add map view and control panel to stack pane
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
