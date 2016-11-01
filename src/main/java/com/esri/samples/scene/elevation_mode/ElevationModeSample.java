/*
 * Copyright 2016 Esri.
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

package com.esri.samples.scene.elevation_mode;

import static com.esri.arcgisruntime.mapping.view.LayerSceneProperties.SurfacePlacement;
import static com.esri.arcgisruntime.symbology.TextSymbol.HorizontalAlignment;
import static com.esri.arcgisruntime.symbology.TextSymbol.VerticalAlignment;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ElevationModeSample extends Application {

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
      stage.setTitle("Elevation Mode Sample");
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
      Camera camera = new Camera(53.04, -4.04, 1300, 0, 90.0, 0);
      sceneView.setViewpointCamera(camera);

      // add base surface for elevation data
      Surface surface = new Surface();
      surface.getElevationSources().add(new ArcGISTiledElevationSource(ELEVATION_IMAGE_SERVICE));
      scene.setBaseSurface(surface);

      // create overlays with elevation modes
      GraphicsOverlay drapedOverlay = new GraphicsOverlay();
      drapedOverlay.getSceneProperties().setSurfacePlacement(SurfacePlacement.DRAPED);
      sceneView.getGraphicsOverlays().add(drapedOverlay);
      GraphicsOverlay relativeOverlay = new GraphicsOverlay();
      relativeOverlay.getSceneProperties().setSurfacePlacement(SurfacePlacement.RELATIVE);
      sceneView.getGraphicsOverlays().add(relativeOverlay);
      GraphicsOverlay absoluteOverlay = new GraphicsOverlay();
      absoluteOverlay.getSceneProperties().setSurfacePlacement(SurfacePlacement.ABSOLUTE);
      sceneView.getGraphicsOverlays().add(absoluteOverlay);

      // create point for graphic location
      Point point = new Point(-4.04, 53.06, 1000, camera.getLocation().getSpatialReference());

      // create a red (0xFFFF0000) circle symbol
      SimpleMarkerSymbol circleSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0xFFFF0000, 10);

      // create a text symbol for each elevation mode
      TextSymbol drapedText = new TextSymbol(10, "DRAPED", 0xFFFFFFFF, HorizontalAlignment.LEFT,
          VerticalAlignment.MIDDLE);
      TextSymbol relativeText = new TextSymbol(10, "RELATIVE", 0xFFFFFFFF, HorizontalAlignment.LEFT,
          VerticalAlignment.MIDDLE);
      TextSymbol absoluteText = new TextSymbol(10, "ABSOLUTE", 0xFFFFFFFF, HorizontalAlignment.LEFT,
          VerticalAlignment.MIDDLE);

      // add the point graphic and text graphic to the corresponding graphics
      // overlay
      drapedOverlay.getGraphics().add(new Graphic(point, circleSymbol));
      drapedOverlay.getGraphics().add(new Graphic(point, drapedText));

      relativeOverlay.getGraphics().add(new Graphic(point, circleSymbol));
      relativeOverlay.getGraphics().add(new Graphic(point, relativeText));

      absoluteOverlay.getGraphics().add(new Graphic(point, circleSymbol));
      absoluteOverlay.getGraphics().add(new Graphic(point, absoluteText));

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
