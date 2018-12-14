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

import java.util.ArrayList;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;

public class SyncMapAndSceneViewpoints extends Application {

  private MapView mapView;
  private SceneView sceneView;

  @Override
  public void start(Stage stage) {

    try {

    // create split pane and JavaFX app scene
    SplitPane splitPane = new SplitPane();
    splitPane.setOrientation(Orientation.HORIZONTAL);
    Scene fxScene = new Scene(splitPane);

    // set title, size, and add JavaFX scene to stage
    stage.setTitle("Sync Map and Scene Viewpoints");
    stage.setWidth(1000);
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

    // set the scene to a scene view
    sceneView = new SceneView();
    sceneView.setArcGISScene(scene);

    // add the map view and scene view to the split plane
    splitPane.getItems().addAll(mapView, sceneView);

    // add a view point changed listener to the map view
    mapView.addViewpointChangedListener(viewpointChangedEvent -> {
      synchronizeViewpoints(mapView);
    });

    // add a view point changed listener to the scene view
    sceneView.addViewpointChangedListener(viewpointChangedEvent -> {
      synchronizeViewpoints(sceneView);
    });

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Synchronizes the viewpoint across Geoviews.
   */
  private void synchronizeViewpoints(GeoView geoView) {

    // if the user is actively navigating the GeoView, get its current viewpoint and set the other view to the same viewpoint
    if (geoView.isNavigating()) {

      Viewpoint geoViewPoint = geoView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE);

      // create a list of geoviews being navigated
      ArrayList<GeoView> arrayListofGeoView = new ArrayList<>();
      arrayListofGeoView.add(mapView);
      arrayListofGeoView.add(sceneView);

      // loop through the available geoviews. If it doesn't match the given geoview, then set the geoview to the other's viewpoint
      for (GeoView anyGeoView : arrayListofGeoView) {
        if (anyGeoView != geoView) {
          anyGeoView.setViewpoint(geoViewPoint);
        }
      }
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

    if (mapView != null && sceneView != null) {
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
