/*
 * Copyright 2017 Esri.
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

package com.esri.samples.featurelayers.feature_layer_extrusion;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.OrbitLocationCameraController;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.symbology.Renderer;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;

public class FeatureLayerExtrusionSample extends Application {

  private boolean showTotalPopulation = true;
  private SceneView sceneView;

  @Override
  public void start(Stage stage) {

    StackPane stackPane = new StackPane();
    Scene fxScene = new Scene(stackPane);
    // for adding styling to any controls that are added 
    fxScene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

    // set title, size, and add scene to stage
    stage.setTitle("Feature Layer Extrusion Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(fxScene);
    stage.show();

    // so scene can be visible within application
    ArcGISScene scene = new ArcGISScene(Basemap.createTopographic());
    sceneView = new SceneView();
    sceneView.setArcGISScene(scene);
    stackPane.getChildren().add(sceneView);

    // get us census data as a service feature table
    ServiceFeatureTable statesServiceFeatureTable = new ServiceFeatureTable("http://sampleserver6.arcgisonline.com/arcgis/rest/services/Census/MapServer/3");

    // creates feature layer from table and add to scene
    final FeatureLayer statesFeatureLayer = new FeatureLayer(statesServiceFeatureTable);
    // feature layer must be rendered dynamically for extrusion to work
    statesFeatureLayer.setRenderingMode(FeatureLayer.RenderingMode.DYNAMIC);
    scene.getOperationalLayers().add(statesFeatureLayer);

    // symbols are used to display features (US states) from table
    SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF000000, 1.0f);
    SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0xFF0000FF, lineSymbol);
    final SimpleRenderer renderer = new SimpleRenderer(fillSymbol);
    // set the extrusion mode to absolute height
    renderer.getSceneProperties().setExtrusionMode(Renderer.SceneProperties.ExtrusionMode.ABSOLUTE_HEIGHT);
    statesFeatureLayer.setRenderer(renderer);

    // set camera to focus on state features
    Point lookAtPoint = new Point(-10974490, 4814376, 0, SpatialReferences.getWebMercator());
    OrbitLocationCameraController orbitCamera = new OrbitLocationCameraController(lookAtPoint, 10000000);
    sceneView.setCameraController(orbitCamera);

    // create a control panel
    VBox vBoxControl = new VBox();
    vBoxControl.setMaxSize(200, 40);
    vBoxControl.getStyleClass().add("panel-region");
    stackPane.getChildren().add(vBoxControl);
    StackPane.setAlignment(vBoxControl, Pos.TOP_LEFT);
    StackPane.setMargin(vBoxControl, new Insets(10, 0, 0, 10));

    // controls for extruding by total population or by population density
    Button extrusionButton = new Button("Population Density");
    extrusionButton.setOnAction(v -> {
      if (showTotalPopulation) {
        // scale down outlier populations
        renderer.getSceneProperties().setExtrusionExpression("[POP2007]/ 10");
        extrusionButton.setText("Population Density");
        showTotalPopulation = false;
      } else {
        // scale up density
        renderer.getSceneProperties().setExtrusionExpression("[POP07_SQMI] * 5000 + 100000");
        extrusionButton.setText("Total Population");
        showTotalPopulation = true;
      }
    });
    extrusionButton.setMaxWidth(Double.MAX_VALUE);
    extrusionButton.fire();
    vBoxControl.getChildren().add(extrusionButton);
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

    if (sceneView != null) {
      sceneView.dispose();
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
