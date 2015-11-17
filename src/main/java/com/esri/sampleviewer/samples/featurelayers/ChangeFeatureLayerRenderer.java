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

import com.esri.arcgisruntime.datasource.arcgis.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.Viewpoint;
import com.esri.arcgisruntime.symbology.RgbColor;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;

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
 * This sample demonstrates how to override and reset a Renderer of a
 * FeatureLayer.
 * <h4>How it Works</h4>
 * 
 * First a {@link FeatureLayer} is created from a {@link ServiceFeatureTable}.
 * Next a {@link SimpleRenderer} can be created using a SimpleLineSymbol which
 * is set to the FeatureLayer using the {@link FeatureLayer#setRenderer} method.
 * To reset a FeatureLayer's Renderer back to the original Renderer, use the
 * {@link FeatureLayer#resetRenderer} method.
 */
public class ChangeFeatureLayerRenderer extends Application {

  private MapView mapView;
  private FeatureLayer featureLayer;

  private final static String FEATURE_SERVICE_URL =
      "http://sampleserver6.arcgisonline.com/arcgis/rest/services/PoolPermits/FeatureServer/0";

  @Override
  public void start(Stage stage) throws Exception {

    // create stack pane and application scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    scene.getStylesheets().add(getClass()
        .getResource("../resources/SamplesTheme.css").toExternalForm());

    // set title, size, and add scene to stage
    stage.setTitle("Change Feature Layer Renderer Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    // create a control panel
    VBox vBoxControl = new VBox(6);
    vBoxControl.setMaxSize(250, 200);
    vBoxControl.getStyleClass().add("panel-region");

    // create sample description
    Label descriptionLabel = new Label("Sample Description:");
    descriptionLabel.getStyleClass().add("panel-label");
    TextArea description = new TextArea(
        "This sample shows how to change a Renderer of a Feature Layer and "
            + "reset it back.");
    description.setWrapText(true);
    description.autosize();
    description.setEditable(false);

    // create change and reset renderer buttons
    Button changeButton = new Button("Change Render");
    Button resetButton = new Button("Reset Renderer");
    changeButton.setMaxWidth(Double.MAX_VALUE);
    resetButton.setMaxWidth(Double.MAX_VALUE);
    changeButton.setDisable(true);
    resetButton.setDisable(true);

    changeButton.setOnAction(e -> {
      // create a blue line symbol renderer
      SimpleLineSymbol lineSymbol = new SimpleLineSymbol(
          SimpleLineSymbol.Style.SOLID, new RgbColor(0, 0, 255, 255), 2, 1);
      SimpleRenderer simpleRenderer = new SimpleRenderer(lineSymbol);

      featureLayer.setRenderer(simpleRenderer);
    });

    resetButton.setOnAction(e -> featureLayer.resetRenderer());

    // add label, sample description, and buttons to the control panel
    vBoxControl.getChildren().addAll(descriptionLabel, description,
        changeButton, resetButton);
    try {

      // create starting envelope for the map
      SpatialReference spatialReference = SpatialReferences.getWebMercator();
      Point topLeftPoint =
          new Point(-1.30758164047166E7, 4014771.46954516, spatialReference);
      Point bottomRightPoint =
          new Point(-1.30730056797177E7, 4016869.78617381, spatialReference);
      Envelope envelope = new Envelope(topLeftPoint, bottomRightPoint);

      // create a service feature table using the url
      final ServiceFeatureTable featureTable =
          new ServiceFeatureTable(FEATURE_SERVICE_URL);

      // create a feature layer from the service feature table
      featureLayer = new FeatureLayer(featureTable);

      // enable buttons when feature layer is done loading
      featureLayer.addDoneLoadingListener(() -> {
        changeButton.setDisable(false);
        resetButton.setDisable(false);
      });

      // create a map with basemap topographic
      final Map map = new Map(Basemap.createTopographic());

      // set starting envelope for the map
      map.setInitialViewpoint(new Viewpoint(envelope));

      // add feature layer to map
      map.getOperationalLayers().add(featureLayer);

      // create a view for this map and set map to it
      mapView = new MapView();
      mapView.setMap(map);

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
