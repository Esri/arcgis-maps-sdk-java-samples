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

package com.esri.samples.open_mobile_scene_package;

import java.io.File;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.MobileScenePackage;
import com.esri.arcgisruntime.mapping.view.SceneView;

public class OpenMobileScenePackageSample extends Application {

  private SceneView sceneView;
  private MobileScenePackage mobileScenePackage; // keeps loadable in scope to avoid garbage collection

  @Override
  public void start(Stage stage) {

    // create stack pane and application scene
    StackPane stackPane = new StackPane();
    Scene fxScene = new Scene(stackPane);

    // set title, size, and add scene to stage
    stage.setTitle("Open Mobile Scene Package Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(fxScene);
    stage.show();

    // create a new scene view
    sceneView = new SceneView();

    // create a mobile scene package from a file
    final String mspkPath = new File(System.getProperty("data.dir"),"./samples-data/mspk/philadelphia.mspk").getAbsolutePath();
    mobileScenePackage = new MobileScenePackage(mspkPath);

    // load the mobile scene package
    mobileScenePackage.loadAsync();

    // wait for the mobile scene package to load
    mobileScenePackage.addDoneLoadingListener(() -> {
      if (mobileScenePackage.getLoadStatus() == LoadStatus.LOADED && mobileScenePackage.getScenes().size() > 0) {
        // set the first scene from the package to the scene view
        sceneView.setArcGISScene(mobileScenePackage.getScenes().get(0));
      } else {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load the mobile scene package");
        alert.show();
      }
    });

    // add the sceneview to the stackpane
    stackPane.getChildren().add(sceneView);
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {
    // release resources when the application closes
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
