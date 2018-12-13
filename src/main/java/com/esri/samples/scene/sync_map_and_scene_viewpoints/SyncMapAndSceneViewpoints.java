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


package com.esri.samples.scene.sync_map_and_scene_viewpoints;

import java.io.IOException;
import java.util.Stack;

import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.mapping.view.ViewpointChangedEvent;
import com.esri.arcgisruntime.mapping.view.ViewpointChangedListener;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class SyncMapAndSceneViewpoints extends Application {

  private MapView mapView;
  private SceneView sceneView;

  @Override
  public void start(Stage stage) throws Exception {

    // create split pane and JavaFX app scene
    SplitPane splitPane = new SplitPane();
    splitPane.setOrientation(Orientation.VERTICAL);
    Scene fxScene = new Scene(splitPane);

    fxScene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

    // set title, size, and add JavaFX scene to stage
    stage.setTitle("Sync Map and Scene Viewpoints");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(fxScene);
    stage.show();

    // create a map and add a basemap to it
    ArcGISMap map = new ArcGISMap();
    map.setBasemap(Basemap.createImagery());

    // create a scene and add a basemap to it
    ArcGISScene scene = new ArcGISScene();
    scene.setBasemap(Basemap.createImagery());

    // set the map to a map view
    mapView = new MapView();
    mapView.setMap(map);
    splitPane.getItems().add(mapView);

    // set the scene to a scene view
    sceneView = new SceneView();
    sceneView.setArcGISScene(scene);
    splitPane.getItems().add(sceneView);

    ViewpointChangedListener theListener = new ViewpointChangedListener() {
      @Override
      public void viewpointChanged(ViewpointChangedEvent viewpointChangedEvent) {

        GeoView combinedview = new GeoView() {
          @Override
          public ReadOnlyDoubleProperty attributionTopProperty() {
            return null;
          }

          @Override
          public double getAttributionTop() {
            return 0;
          }

          @Override
          public SpatialReference getSpatialReference() {
            return null;
          }
        };

        Viewpoint sharedView = combinedview.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE);

        System.out.println("The view point is on the move");
        sceneView.setViewpoint(mapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE));
        if (scene.getLoadStatus() == LoadStatus.LOADED ){
        mapView.setViewpoint(sceneView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE));
        }

      }
    };

//    ViewpointChangedListener changedListener = viewpointChangedEvent ->
//            sceneView.setViewpoint(mapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE));

    mapView.addViewpointChangedListener(theListener);



  }



  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

    if (mapView != null && sceneView !=null) {
      mapView.dispose();
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
