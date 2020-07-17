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

package com.esri.samples.extrude_graphics;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LayerSceneProperties;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.symbology.ColorUtil;
import com.esri.arcgisruntime.symbology.Renderer.SceneProperties;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;

public class ExtrudeGraphicsSample extends Application {

  private SceneView sceneView;
  private static final String ELEVATION_IMAGE_SERVICE =
      "https://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer";

  @Override
  public void start(Stage stage) {

    try {

      // create stack pane and JavaFX app scene
      StackPane stackPane = new StackPane();
      Scene fxScene = new Scene(stackPane);

      // set title, squareSize, and add JavaFX scene to stage
      stage.setTitle("Extrude Graphics Sample");
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

      Camera camera = new Camera(28.4, 83, 10000, 10.0, 80.0, 0);
      sceneView.setViewpointCamera(camera);

      // add base surface for elevation data
      Surface surface = new Surface();
      surface.getElevationSources().add(new ArcGISTiledElevationSource(ELEVATION_IMAGE_SERVICE));
      scene.setBaseSurface(surface);

      // add a graphics overlay
      GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
      graphicsOverlay.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.DRAPED);

      // set renderer with extrusion property
      SimpleRenderer renderer = new SimpleRenderer();
      SceneProperties renderProperties = renderer.getSceneProperties();
      renderProperties.setExtrusionMode(SceneProperties.ExtrusionMode.ABSOLUTE_HEIGHT);
      renderProperties.setExtrusionExpression("[HEIGHT]");
      graphicsOverlay.setRenderer(renderer);

      // setup graphic positions
      double squareSize = 0.01;
      double maxHeight = 10000.0;
      double x = camera.getLocation().getX();
      double y = camera.getLocation().getY() + 0.2;
      List<Point> points = IntStream.range(0, 100).mapToObj(i -> new Point(i / 10 * squareSize + x, i % 10 *
          squareSize + y)).collect(Collectors.toList());

      // create and style graphics
      points.forEach(p -> {
        double z = (int) (maxHeight * Math.random());
        int color = ColorUtil.colorToArgb(Color.color(1.0 / maxHeight * z, 0, 0.5, 1));
        Polygon polygon = new Polygon(new PointCollection(Arrays.asList(new Point(p.getX(), p.getY(), z), new Point(p
            .getX() + squareSize, p.getY(), z), new Point(p.getX() + squareSize, p.getY() + squareSize, z), new Point(p
                .getX(), p.getY() + squareSize, z))));
        Graphic graphic = new Graphic(polygon);
        graphic.getAttributes().put("HEIGHT", z);
        graphic.setSymbol(new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, color, null));
        graphicsOverlay.getGraphics().add(graphic);
      });

      sceneView.getGraphicsOverlays().add(graphicsOverlay);
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
