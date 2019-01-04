/*
 * Copyright 2019 Esri.
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

package com.esri.samples.scene.scene_layer_package;

import java.io.File;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.layers.ArcGISSceneLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.SceneView;

public class SceneLayerPackageSample extends Application {

  private SceneView sceneView;

  @Override
  public void start(Stage stage) {

    try {
      // set the title and size of the stage and show it
      stage.setTitle("Scene Layer Package Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.show();

      // creat a JavaFX scene with a stack pane and add it to the stage
      StackPane stackPane = new StackPane();
      Scene fxScene = new Scene(stackPane);
      stage.setScene(fxScene);

      // create a scene view and add it to the stack pane
      sceneView = new SceneView();
      stackPane.getChildren().add(sceneView);

      // create a scene and add a basemap to it
      ArcGISScene scene = new ArcGISScene();
      scene.setBasemap(Basemap.createImagery());

      // add elevation data to the scene's base surface
      Surface surface = new Surface();
      surface.getElevationSources().add(new ArcGISTiledElevationSource("http://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer"));
      scene.setBaseSurface(surface);

      // add the scene to the scene view
      sceneView.setArcGISScene(scene);

      // create a scene layer with the path to the local scene layer package (.slpk)
      String slpkPath = new File("./samples-data/slpk/Petronas Towers.slpk").getAbsolutePath();
      ArcGISSceneLayer sceneLayer = new ArcGISSceneLayer(slpkPath);

      // add the scene layer to the scene as an operational layer
      scene.getOperationalLayers().add(sceneLayer);

      // when the layer finishes loading, zoom to its extent
      sceneLayer.addDoneLoadingListener(() -> {
        if (sceneLayer.getLoadStatus() == LoadStatus.LOADED) {
          sceneView.setViewpointCamera(new Camera(sceneLayer.getFullExtent().getCenter(), 1000, 0, 70, 0));
        }
      });


    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
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
