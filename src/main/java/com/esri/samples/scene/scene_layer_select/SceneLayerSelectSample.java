/*
 * Copyright 2018 Esri.
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

package com.esri.samples.scene.scene_layer_select;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.layers.ArcGISSceneLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.SceneView;

public class SceneLayerSelectSample extends Application {

  private SceneView sceneView;

  @Override
  public void start(Stage stage) throws Exception {

    try {

      // create stack pane and JavaFX app scene
      StackPane stackPane = new StackPane();
      Scene fxScene = new Scene(stackPane);

      // set title, size, and add JavaFX scene to stage
      stage.setTitle("Scene Layer Select Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(fxScene);
      stage.show();

      // create a scene and add a basemap to it
      ArcGISScene scene = new ArcGISScene();
      scene.setBasemap(Basemap.createImagery());

      // set the scene to the scene view
      sceneView = new SceneView();
      sceneView.setArcGISScene(scene);

      // add the scene view to the stack pane
      stackPane.getChildren().add(sceneView);

      // add base surface with elevation data
      Surface surface = new Surface();
      final String elevationService = "http://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer";
      surface.getElevationSources().add(new ArcGISTiledElevationSource(elevationService));
      scene.setBaseSurface(surface);

      // add a scene layer of Harvard buildings to the scene
      final String buildings = "https://tiles.arcgis.com/tiles/N82JbI5EYtAkuUKU/arcgis/rest/services/Buildings_Harvard/SceneServer";
      ArcGISSceneLayer sceneLayer = new ArcGISSceneLayer(buildings);
      scene.getOperationalLayers().add(sceneLayer);

      // zoom to the layer's extent when loaded
      sceneLayer.addDoneLoadingListener(() -> {
        if (sceneLayer.getLoadStatus() == LoadStatus.LOADED) {
          sceneView.setViewpoint(new Viewpoint(sceneLayer.getFullExtent()));

          // when the scene is clicked, identify the clicked feature and select it
          sceneView.setOnMouseClicked(e -> {
            if (e.isStillSincePress() && e.getButton() == MouseButton.PRIMARY) {
              // clear any previous selection
              sceneLayer.clearSelection();
              // identify clicked feature
              Point2D point2D = new Point2D(e.getX(), e.getY());
              ListenableFuture<IdentifyLayerResult> identify = sceneView.identifyLayerAsync(sceneLayer, point2D, 10, false, 1);
              identify.addDoneListener(() -> {
                try {
                  // get the identified result and check that it is a feature
                  IdentifyLayerResult result = identify.get();
                  List<GeoElement> geoElements = result.getElements();
                  if (geoElements.size() > 0) {
                    GeoElement geoElement = geoElements.get(0);
                    if (geoElement instanceof Feature) {
                      // select the feature
                      sceneLayer.selectFeature((Feature) geoElement);
                    }
                  }
                } catch (InterruptedException | ExecutionException ex) {
                  new Alert(Alert.AlertType.ERROR, "Error identifying features").show();
                }
              });
            }
          });
        } else {
          new Alert(Alert.AlertType.ERROR, "Error loading scene layer").show();
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
