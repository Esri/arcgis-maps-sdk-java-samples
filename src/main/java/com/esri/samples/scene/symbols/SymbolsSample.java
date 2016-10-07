/*
 * Copyright 2016 Esri. Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.esri.samples.scene.symbols;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.*;
import com.esri.arcgisruntime.symbology.SceneSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSceneSymbol;

public class SymbolsSample extends Application {

  private SceneView sceneView;
  private static final String ELEVATION_IMAGE_SERVICE =
      "http://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer";

  @Override
  public void start(Stage stage) throws Exception {

    try {

      // create stack pane and JavaFX app scene
      StackPane stackPane = new StackPane();
      Scene fxScene = new Scene(stackPane);

      // set title, size, and add JavaFX scene to stage
      stage.setTitle("Symbols Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(fxScene);
      stage.show();

      // create a scene and add a basemap to it
      ArcGISScene scene = new ArcGISScene();
      scene.setBasemap(Basemap.createImagery());

      // add the SceneView to the stack pane
      sceneView = new SceneView();
      sceneView.setArcGISScene(scene);
      stackPane.getChildren().add(sceneView);

      // add a camera and initial camera position
      Camera camera = new Camera(29, 45, 6000, 0, 0, 0);
      sceneView.setViewpointCamera(camera);
      Point cameraLocation = camera.getLocation();

      // add base surface for elevation data
      ArcGISTiledElevationSource elevationSource = new ArcGISTiledElevationSource(ELEVATION_IMAGE_SERVICE);
      Surface surface = new Surface();
      surface.getElevationSources().add(elevationSource);
      scene.setBaseSurface(surface);

      // add graphics overlay(s)
      GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
      graphicsOverlay.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.ABSOLUTE);
      sceneView.getGraphicsOverlays().add(graphicsOverlay);

      // create graphics for each type of symbol
      AtomicInteger counter = new AtomicInteger(0);
      double x = 44.975;
      double y = 29;
      double z = 500;
      Stream.of(SimpleMarkerSceneSymbol.Style.values()).map(style -> {
        int color;
        switch (style) {
          case CONE:
            color = 0xFFFF0000; // red
            break;
          case TETRAHEDRON:
            color = 0xFF00FF00; // green
            break;
          case SPHERE:
            color = 0xFF0000FF; // blue
            break;
          case CYLINDER:
            color = 0xFFFF00FF; // purple
            break;
          case DIAMOND:
            color = 0xFF00FFFF; // turquoise
            break;
          case CUBE:
          default:
            color = 0xFFFFFFFF; // white
        }
        SimpleMarkerSceneSymbol symbol = new SimpleMarkerSceneSymbol(style, color, 200, 200, 200,
            SceneSymbol.AnchorPosition.CENTER);
        int position = counter.getAndIncrement();
        return new Graphic(new Point(x + 0.01 * position, y, z, cameraLocation.getSpatialReference()), symbol);
      }).collect(Collectors.toCollection(graphicsOverlay::getGraphics));

    } catch (Exception e) {
      // on any error, display the stack trace
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
