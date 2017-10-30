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

package com.esri.samples.scene.feature_layer_rendering_mode_scene;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.SceneView;

public class FeatureLayerRenderingModeSceneSample extends Application {

  private SceneView sceneViewTop;
  private SceneView sceneViewBottom;

  @Override
  public void start(Stage stage) throws Exception {

    try {

      // create border pane and JavaFX app scene
      BorderPane borderPane = new BorderPane();
      Scene fxScene = new Scene(borderPane);

      // set title, size, and add JavaFX scene to stage
      stage.setTitle("Feature Layer Rendering Mode Scene Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setResizable(false);
      stage.setScene(fxScene);
      stage.show();

      // create a scene (top) and set it to render all features in static rendering mode
      ArcGISScene sceneTop = new ArcGISScene();
      sceneTop.getLoadSettings().setPreferredPointFeatureRenderingMode(FeatureLayer.RenderingMode.STATIC);
      sceneTop.getLoadSettings().setPreferredPolylineFeatureRenderingMode(FeatureLayer.RenderingMode.STATIC);
      sceneTop.getLoadSettings().setPreferredPolygonFeatureRenderingMode(FeatureLayer.RenderingMode.STATIC);

      // create a scene (bottom) and set it to render all features in dynamic rendering mode
      ArcGISScene sceneBottom = new ArcGISScene();
      sceneBottom.getLoadSettings().setPreferredPointFeatureRenderingMode(FeatureLayer.RenderingMode.DYNAMIC);
      sceneBottom.getLoadSettings().setPreferredPolylineFeatureRenderingMode(FeatureLayer.RenderingMode.DYNAMIC);
      sceneBottom.getLoadSettings().setPreferredPolygonFeatureRenderingMode(FeatureLayer.RenderingMode.DYNAMIC);

      // creating top scene view
      sceneViewTop = new SceneView();
      sceneViewTop.setMinHeight(stage.getHeight() / 2);
      sceneViewTop.setArcGISScene(sceneTop);
      borderPane.setTop(sceneViewTop);
      // creating bottom scene view
      sceneViewBottom = new SceneView();
      sceneViewTop.setMinHeight(stage.getHeight() / 2);
      sceneViewBottom.setArcGISScene(sceneBottom);
      borderPane.setCenter(sceneViewBottom);

      // create service feature table using a point, polyline, and polygon service
      ServiceFeatureTable poinServiceFeatureTable = new ServiceFeatureTable("http://sampleserver6.arcgisonline.com/arcgis/rest/services/Energy/Geology/FeatureServer/0");
      ServiceFeatureTable polylineServiceFeatureTable = new ServiceFeatureTable("http://sampleserver6.arcgisonline.com/arcgis/rest/services/Energy/Geology/FeatureServer/8");
      ServiceFeatureTable polygonServiceFeatureTable = new ServiceFeatureTable("http://sampleserver6.arcgisonline.com/arcgis/rest/services/Energy/Geology/FeatureServer/9");

      // create feature layer from service feature tables
      FeatureLayer pointFeatureLayer = new FeatureLayer(poinServiceFeatureTable);
      FeatureLayer polylineFeatureLayer = new FeatureLayer(polylineServiceFeatureTable);
      FeatureLayer polygonFeatureLayer = new FeatureLayer(polygonServiceFeatureTable);

      // add each layer to top and bottom scene
      sceneTop.getOperationalLayers().add(pointFeatureLayer);
      sceneTop.getOperationalLayers().add(polylineFeatureLayer);
      sceneTop.getOperationalLayers().add(polygonFeatureLayer);
      sceneBottom.getOperationalLayers().add(pointFeatureLayer.copy());
      sceneBottom.getOperationalLayers().add(polylineFeatureLayer.copy());
      sceneBottom.getOperationalLayers().add(polygonFeatureLayer.copy());

      // camera locations for camera to zoom in and out to
      Camera zoomOutCamera = new Camera(new Point(-118.37, 34.46, SpatialReferences.getWgs84()), 42000, 0, 0, 0);
      Camera zoomInCamera = new Camera(new Point(-118.45, 34.395, SpatialReferences.getWgs84()), 2500, 90, 75, 90);
      sceneViewTop.setViewpointCamera(zoomOutCamera);
      sceneViewBottom.setViewpointCamera(zoomOutCamera);

      Button zoomButton = new Button("Start Zoom");
      zoomButton.setOnAction(e -> {
        zoomButton.setDisable(true);
        // zoom in for five seconds
        zoomTo(zoomInCamera, 5).addDoneListener(() -> {
          // wait for three seconds before returning
          zoomTo(zoomInCamera, 3).addDoneListener(() -> {
            // zoom out for five seconds
            zoomTo(zoomOutCamera, 5).addDoneListener(() -> {
              zoomButton.setDisable(false);
            });
          });
        });
      });
      zoomButton.setMaxWidth(Double.MAX_VALUE);
      borderPane.setBottom(zoomButton);

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Sets both Sceneviews to a ViewpointCamera over a number of seconds.
   *
   * @param camera to which both SceneViews should be set.
   * @param seconds over which the viewpoint is asynchronously set.
   *
   * @return a ListenableFuture representing the result of the Viewpoint change.
   */
  private ListenableFuture<Boolean> zoomTo(Camera camera, int seconds) {
    ListenableFuture<Boolean> setViewpointFuture = sceneViewTop.setViewpointCameraAsync(camera, seconds);
    sceneViewBottom.setViewpointCameraAsync(camera, seconds);
    return setViewpointFuture;
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

    if (sceneViewTop != null) {
      sceneViewTop.dispose();
    }
    if (sceneViewBottom != null) {
      sceneViewBottom.dispose();
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
