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

package com.esri.samples.display_scene;

import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.layers.RasterLayer;
import com.esri.arcgisruntime.mapping.*;
import com.esri.arcgisruntime.raster.Raster;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.SceneView;

import java.util.ArrayList;

public class DisplaySceneSample extends Application {

  private SceneView sceneView;
  private ArcGISScene scene;
  private RasterElevationSource elevationSource;
  private static final String ELEVATION_IMAGE_SERVICE =
      "https://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer";

  @Override
  public void start(Stage stage) {

    try {

      String tmpdir = System.getProperty("java.io.tmpdir");
      System.out.println("Temp file path: " + tmpdir);

      // create stack pane and JavaFX app scene
      StackPane stackPane = new StackPane();
      Scene fxScene = new Scene(stackPane);

      // set title, size, and add JavaFX scene to stage
      stage.setTitle("Display Scene Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(fxScene);
      stage.show();

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a scene with a basemap style
      scene = new ArcGISScene(Basemap.createImageryWithLabels());

      // add the SceneView to the stack pane
      sceneView = new SceneView();
      sceneView.setArcGISScene(scene);
      stackPane.getChildren().addAll(sceneView);

      // add base surface for elevation data
      Surface surface = new Surface();
      surface.getElevationSources().add(new ArcGISTiledElevationSource(ELEVATION_IMAGE_SERVICE));
      scene.setBaseSurface(surface);

      ArrayList<String> files = new ArrayList<>();
      files.add("./E000/N50.DT1");

      elevationSource = new RasterElevationSource(files);
      elevationSource.loadAsync();
      elevationSource.addDoneLoadingListener(()-> {
        System.out.println("Elevation load status " + elevationSource.getLoadStatus());

        surface.getElevationSources().add(elevationSource);
      });


      Raster raster = new Raster("./E000/N50.DT1");
      raster.loadAsync();
      raster.addDoneLoadingListener(()-> {
        System.out.println("raster loaded " + raster.getLoadStatus());
        RasterLayer rasterLayer = new RasterLayer(raster);

        scene.getOperationalLayers().add(rasterLayer);
      });

      // add the SceneView to the stack pane
      //sceneView = new SceneView();
      //sceneView.setArcGISScene(scene);
      //stackPane.getChildren().addAll(sceneView);

      // add base surface for elevation data
      //Surface surface = new Surface();
      //surface.getElevationSources().add(new ArcGISTiledElevationSource(ELEVATION_IMAGE_SERVICE));
      //scene.setBaseSurface(surface);

      // add a camera and initial camera position
      //Camera camera = new Camera(28.4, 83.9, 10010.0, 10.0, 80.0, 0.0);
      //sceneView.setViewpointCamera(camera);

      Button button = new Button("close scene");
      button.setOnAction(event  -> {
        System.out.println("button pressed");

        sceneView.setArcGISScene(null);
        scene = null;
        elevationSource = null;

      });

      stackPane.getChildren().add(button);

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

    //Layer layer = sceneView.getArcGISScene().getOperationalLayers().get(0);
    //sceneView.getArcGISScene().getOperationalLayers().clear();


    if (sceneView != null) {
      sceneView.dispose();
    }

    scene = null;
    elevationSource = null;

    try {

      System.out.println("keep alive for 5 seconds after null setting");
      Thread.sleep(5000);
      System.out.println("closing after delay...");

    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println("GC");
    System.gc();


    try {

      System.out.println("keep alive for 5 seconds");
      Thread.sleep(5000);
      System.out.println("closing after delay...");

    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Opens and runs application.
   *
   * @param args arguments passed to this application
   */
  public static void main(String[] args) {
    ArcGISRuntimeEnvironment.setInstallDirectory("/Users/mark8487/.arcgis/200.0.0-3570");

    Application.launch(args);
  }

}
