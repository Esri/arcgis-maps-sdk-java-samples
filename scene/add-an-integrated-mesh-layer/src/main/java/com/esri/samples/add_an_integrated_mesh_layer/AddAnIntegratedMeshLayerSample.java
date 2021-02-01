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

package com.esri.samples.add_an_integrated_mesh_layer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.IntegratedMeshLayer;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.SceneView;

public class AddAnIntegratedMeshLayerSample extends Application {

  private SceneView sceneView;

  @Override
  public void start(Stage stage) {

    try {

      // set the title and size of the stage and show it
      stage.setTitle("Add An Integrated Mesh Layer Sample");
      stage.setWidth(800);
      stage.setHeight(700);

      // create a JavaFX scene with a stack pane and set it to the stage
      StackPane stackPane = new StackPane();
      Scene fxScene = new Scene(stackPane);
      stage.setScene(fxScene);
      stage.show();

      // create a scene
      ArcGISScene scene = new ArcGISScene();

      // create a scene view and set the scene to it
      sceneView = new SceneView();
      sceneView.setArcGISScene(scene);

      // create an integrated mesh layer of Girona, Spain and add it to the scene's operational layers
      IntegratedMeshLayer integratedMeshLayer = new IntegratedMeshLayer("https://tiles.arcgis" +
          ".com/tiles/z2tnIkrLQ2BRzr6P/arcgis/rest/services/Girona_Spain/SceneServer");
      scene.getOperationalLayers().add(integratedMeshLayer);

      // set a camera position on the scene view
      sceneView.setViewpointCamera(new Camera(41.9906, 2.8259, 200.0, 190.0, 65.0, 0));

      // add the scene view to the stack pane
      stackPane.getChildren().add(sceneView);

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
