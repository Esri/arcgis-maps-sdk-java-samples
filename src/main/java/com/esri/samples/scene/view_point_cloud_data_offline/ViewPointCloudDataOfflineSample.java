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

package com.esri.samples.scene.view_point_cloud_data_offline;

import java.io.File;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.layers.PointCloudLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.SceneView;

public class ViewPointCloudDataOfflineSample extends Application {

  private SceneView sceneView;

  @Override
  public void start(Stage stage) {

    try {

      // set the title and size of the stage and show it
      stage.setTitle("View Point Cloud Data Offline Sample");
      stage.setWidth(800);
      stage.setHeight(700);

      // create a JavaFX scene with a stackpane and set it to the stage
      StackPane stackPane = new StackPane();
      Scene fxScene = new Scene(stackPane);
      stage.setScene(fxScene);
      stage.show();

      // create a scene view and add it to the stack pane
      sceneView = new SceneView();
      stackPane.getChildren().add(sceneView);

      // create a scene with a basemap and add it to the scene view
      ArcGISScene scene = new ArcGISScene();
      scene.setBasemap(Basemap.createImagery());
      sceneView.setArcGISScene(scene);

      // set the base surface with world elevation
      Surface surface = new Surface();
      surface.getElevationSources().add(new ArcGISTiledElevationSource("http://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer"));
      scene.setBaseSurface(surface);

      // add a a point cloud layer with data from Balboa Park in San Diego
      File pointCloudSLPK = new File("./samples-data/slpks/sandiego-north-balboa-pointcloud.slpk");
      PointCloudLayer pointCloudLayer = new PointCloudLayer(pointCloudSLPK.getAbsolutePath());
      scene.getOperationalLayers().add(pointCloudLayer);

      // zoom to the layer when it is done loading
      pointCloudLayer.addDoneLoadingListener(() -> {
        if (pointCloudLayer.getLoadStatus() == LoadStatus.LOADED) {
          sceneView.setViewpointCamera(new Camera(pointCloudLayer.getFullExtent().getCenter(), 2000, 30, 60, 0));
        } else {
          new Alert(Alert.AlertType.ERROR, "Point cloud layer failed to load").show();
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
