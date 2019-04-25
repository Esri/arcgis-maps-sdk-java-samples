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

package com.esri.samples.scene.open_mobile_scene_package;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.MobileScenePackage;
import com.esri.arcgisruntime.mapping.view.SceneView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

public class OpenMobileScenePackageSample extends Application {

  private SceneView sceneView;

  @Override
  public void start(Stage stage) throws Exception {

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

    // load a mobile scene package
    final String mspkPath = new File("./samples-data/mspk/philadelphia.mspk").getAbsolutePath();

    // check if the mobile scene package can be read directly using a static method
    ListenableFuture<Boolean> isDirectReadSupported = MobileScenePackage.isDirectReadSupportedAsync(mspkPath);
    isDirectReadSupported.addDoneListener(() -> {

      try {
        // if the mobile scene package can be read directly, then load the mspk from the direct read path directory
        if (isDirectReadSupported.get()) {
          //instantiate mobile scene package for direct read file
          MobileScenePackage directReadMSPK = new MobileScenePackage(mspkPath);
          loadMobileScenePackage(directReadMSPK);

        } else {
          // create a temporary directory to store unpacked file if appropriate
          Path tempDirectory = Files.createTempDirectory("offline_map");
          final String tempUnpackedPath = tempDirectory.toString();

          // if the mobile scene package has to be unpacked, then unpack the mobile scene package and store it in a local path
          MobileScenePackage.unpackAsync(mspkPath, tempUnpackedPath).addDoneListener(() -> {
            //instantiate mobile scene package for unpacked file
            MobileScenePackage unpackedMSPK = new MobileScenePackage(tempUnpackedPath);
            // load the mobile scene package from the unpacked path
            loadMobileScenePackage(unpackedMSPK);
          });
        }

      } catch (InterruptedException | ExecutionException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Mobile Scene Package direct read could not be determined");
        alert.show();

      } catch (IOException e) {
        e.printStackTrace();
      }

    });

    // add the sceneview to the stackpane
    stackPane.getChildren().add(sceneView);
  }

  /**
   * Loads the mobile scene package asynchronously, and once it has loaded, sets the first scene within the package to the scene view.
   */
  private void loadMobileScenePackage(MobileScenePackage mobileScenePackage) {

    mobileScenePackage.loadAsync();
    mobileScenePackage.addDoneLoadingListener(() -> {

      if (mobileScenePackage.getLoadStatus() == LoadStatus.LOADED && mobileScenePackage.getScenes().size() > 0) {
        // set the first scene from the package to the scene view
        sceneView.setArcGISScene(mobileScenePackage.getScenes().get(0));
      } else {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load the mobile scene package");
        alert.show();
      }
    });
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


