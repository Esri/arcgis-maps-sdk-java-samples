/*
 * Copyright 2016 Esri.
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
package com.esri.samples.scene.terrain_exaggeration;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.SceneView;

public class TerrainExaggerationSample extends Application {

  private SceneView sceneView;

  @Override
  public void start(Stage stage) throws Exception {
    // create stack pane and JavaFX app scene
    StackPane stackPane = new StackPane();
    Scene fxScene = new Scene(stackPane);

    // set up the stage
    stage.setTitle("Terrain Exaggeration Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(fxScene);
    stage.show();

    try {
      // create a scene and add a basemap to it
      ArcGISScene scene = new ArcGISScene();
      scene.setBasemap(Basemap.createNationalGeographic());

      // add the SceneView to the stack pane
      sceneView = new SceneView();
      sceneView.setArcGISScene(scene);

      // add base surface for elevation data
      Surface surface = new Surface();
      final String elevationImageService =
          "http://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer";
      surface.getElevationSources().add(new ArcGISTiledElevationSource(elevationImageService));
      surface.setElevationExaggeration(5);
      scene.setBaseSurface(surface);

      // add terrain layer to scene
      final String imageLayer =
          "https://gis.grantcountywa.gov:6443/arcgis/rest/services/EveryoneData/SlopePercent/MapServer";
      ArcGISMapImageLayer layer = new ArcGISMapImageLayer(imageLayer);
      layer.loadAsync();
      scene.getOperationalLayers().add(layer);

      // add a camera and initial camera position
      Point initialLocation = new Point(-119.94891542688772, 46.75792111605992, 0, sceneView.getSpatialReference());
      Camera camera = new Camera(initialLocation, 15000.0, 40.0, 60.0, 0.0);
      sceneView.setViewpointCamera(camera);

      // add the SceneView to the stack pane
      stackPane.getChildren().add(sceneView);
    } catch (Exception e) {
      // on any exception, print the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

    if (sceneView != null)
      sceneView.dispose();
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
