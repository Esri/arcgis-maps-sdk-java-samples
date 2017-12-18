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

import java.util.Arrays;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import javafx.util.Duration;

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

      // create splitPane pane and JavaFX app scene
      SplitPane splitPane = new SplitPane();
      splitPane.setOrientation(Orientation.VERTICAL);
      Scene fxScene = new Scene(splitPane);

      // set title, size, and add JavaFX scene to stage
      stage.setTitle("Feature Layer Rendering Mode Scene Sample");
      stage.setWidth(800);
      stage.setHeight(700);
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
      sceneViewTop.setArcGISScene(sceneTop);
      splitPane.getItems().add(sceneViewTop);
      // creating bottom scene view
      sceneViewBottom = new SceneView();
      sceneViewBottom.setArcGISScene(sceneBottom);
      splitPane.getItems().add(sceneViewBottom);

      // create service feature table using a point, polyline, and polygon service
      ServiceFeatureTable poinServiceFeatureTable = new ServiceFeatureTable("http://sampleserver6.arcgisonline.com/arcgis/rest/services/Energy/Geology/FeatureServer/0");
      ServiceFeatureTable polylineServiceFeatureTable = new ServiceFeatureTable("http://sampleserver6.arcgisonline.com/arcgis/rest/services/Energy/Geology/FeatureServer/8");
      ServiceFeatureTable polygonServiceFeatureTable = new ServiceFeatureTable("http://sampleserver6.arcgisonline.com/arcgis/rest/services/Energy/Geology/FeatureServer/9");

      // create feature layer from service feature tables
      FeatureLayer pointFeatureLayer = new FeatureLayer(poinServiceFeatureTable);
      FeatureLayer polylineFeatureLayer = new FeatureLayer(polylineServiceFeatureTable);
      FeatureLayer polygonFeatureLayer = new FeatureLayer(polygonServiceFeatureTable);

      // add each layer to top and bottom scene
      sceneTop.getOperationalLayers().addAll(Arrays.asList(pointFeatureLayer, polylineFeatureLayer, polygonFeatureLayer));
      sceneBottom.getOperationalLayers().addAll(Arrays.asList(pointFeatureLayer.copy(), polylineFeatureLayer.copy(), polygonFeatureLayer.copy()));

      // camera locations for camera to zoom in and out to
      Camera zoomOutCamera = new Camera(new Point(-118.37, 34.46, SpatialReferences.getWgs84()), 42000, 0, 0, 0);
      Camera zoomInCamera = new Camera(new Point(-118.45, 34.395, SpatialReferences.getWgs84()), 2500, 90, 75, 0);
      sceneViewTop.setViewpointCamera(zoomOutCamera);
      sceneViewBottom.setViewpointCamera(zoomOutCamera);

      //loop an animation into and out from the zoom in point (5 seconds each) with a 2 second gap between zooming
      Timeline timeline = new Timeline();
      timeline.setCycleCount(Animation.INDEFINITE);
      timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(7), event -> zoomTo(zoomOutCamera, 5)));
      timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(14), event -> zoomTo(zoomInCamera, 5)));
      timeline.play();

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
    */
  private void zoomTo(Camera camera, int seconds) {
    sceneViewTop.setViewpointCameraAsync(camera, seconds);
    sceneViewBottom.setViewpointCameraAsync(camera, seconds);
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
