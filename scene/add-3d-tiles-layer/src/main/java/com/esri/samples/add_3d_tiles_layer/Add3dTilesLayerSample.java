/*
 * Copyright 2024 Esri.
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

package com.esri.samples.add_3d_tiles_layer;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.layers.Ogc3DTilesLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.SceneView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Add3dTilesLayerSample extends Application {
  private SceneView sceneView;
  private ArcGISScene arcGISScene;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene fxScene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Add 3D Tiles Layer Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(fxScene);
      stage.show();

      // create a new elevation source from Terrain3D REST service
      ArcGISTiledElevationSource elevationSource = new ArcGISTiledElevationSource(
          "https://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer");

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      arcGISScene = new ArcGISScene(BasemapStyle.ARCGIS_DARK_GRAY);

      arcGISScene.getBaseSurface().getElevationSources().add(elevationSource);
      add3DTilesLayer();

      sceneView = new SceneView();

      arcGISScene.addDoneLoadingListener(() -> {
        if (arcGISScene.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
          new Alert(Alert.AlertType.ERROR,
              "Scene failed to load: " + arcGISScene.getLoadError().getCause().getMessage()).show();
        }
      });

      sceneView.setArcGISScene(arcGISScene);
      setInitialViewpoint();

      // add the scene view to the stack pane
      stackPane.getChildren().add(sceneView);
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  private void setInitialViewpoint() {
    // add a camera
    double latitude = 48.84553;
    double longitude = 9.16275;
    double altitude = 350.0;
    double heading = 0.0;
    double pitch = 60;
    double roll = 0.0;
    Camera sceneCamera = new Camera(latitude, longitude, altitude, heading, pitch, roll);
    sceneView.setViewpointCamera(sceneCamera);
  }

  private void add3DTilesLayer() {
    String tilePath =
        "https://tiles.arcgis.com/tiles/ZQgQTuoyBrtmoGdP/arcgis/rest/services/Stuttgart/3DTilesServer/tileset.json";

    Ogc3DTilesLayer ogc3dTilesLayer = new Ogc3DTilesLayer(tilePath);
    arcGISScene.getOperationalLayers().add(ogc3dTilesLayer);
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
