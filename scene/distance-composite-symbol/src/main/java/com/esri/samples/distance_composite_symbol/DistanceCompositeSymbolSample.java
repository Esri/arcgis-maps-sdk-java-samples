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

package com.esri.samples.scene.distance_composite_symbol;

import java.io.File;

import com.esri.arcgisruntime.mapping.view.OrbitGeoElementCameraController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LayerSceneProperties;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.symbology.DistanceCompositeSceneSymbol;
import com.esri.arcgisruntime.symbology.ModelSceneSymbol;
import com.esri.arcgisruntime.symbology.SceneSymbol.AnchorPosition;
import com.esri.arcgisruntime.symbology.SimpleMarkerSceneSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

public class DistanceCompositeSymbolSample extends Application {

  private SceneView sceneView;
  private static final String ELEVATION_IMAGE_SERVICE =
      "http://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer";

  @Override
  public void start(Stage stage) {

    try {

      // create stack pane and JavaFX app scene
      StackPane stackPane = new StackPane();
      Scene fxScene = new Scene(stackPane);

      // set title, size, and add JavaFX scene to stage
      stage.setTitle("Distance Composite Symbol Sample");
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
      stackPane.getChildren().addAll(sceneView);

      // add base surface for elevation data
      Surface surface = new Surface();
      surface.getElevationSources().add(new ArcGISTiledElevationSource(ELEVATION_IMAGE_SERVICE));
      scene.setBaseSurface(surface);

      // add a graphics overlay
      GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
      graphicsOverlay.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.RELATIVE);
      sceneView.getGraphicsOverlays().add(graphicsOverlay);

      // set up the different symbols
      int red = 0xFFFF0000;
      SimpleMarkerSymbol circleSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, red, 10);
      SimpleMarkerSceneSymbol coneSymbol = SimpleMarkerSceneSymbol.createCone(red, 3, 10);
      coneSymbol.setPitch(-90);
      coneSymbol.setAnchorPosition(AnchorPosition.CENTER);
      String modelURI = new File("./samples-data/bristol/Collada/Bristol.dae").getAbsolutePath();
      ModelSceneSymbol modelSymbol = new ModelSceneSymbol(modelURI, 1.0);
      modelSymbol.loadAsync();

      // set up the distance composite symbol
      DistanceCompositeSceneSymbol compositeSymbol = new DistanceCompositeSceneSymbol();
      compositeSymbol.getRangeCollection().add(new DistanceCompositeSceneSymbol.Range(modelSymbol, 0, 100));
      compositeSymbol.getRangeCollection().add(new DistanceCompositeSceneSymbol.Range(coneSymbol, 100, 500));
      compositeSymbol.getRangeCollection().add(new DistanceCompositeSceneSymbol.Range(circleSymbol, 500, 0));

      // create graphic
      Point aircraftPosition = new Point(-2.708471, 56.096575, 5000, SpatialReferences.getWgs84());
      Graphic aircraftGraphic = new Graphic(aircraftPosition, compositeSymbol);
      // add graphic to graphics overlay
      graphicsOverlay.getGraphics().add(aircraftGraphic);

      // add an orbit camera controller to lock the camera to the graphic
      OrbitGeoElementCameraController cameraController = new OrbitGeoElementCameraController(aircraftGraphic, 20);
      cameraController.setCameraPitchOffset(80);
      cameraController.setCameraHeadingOffset(-30);
      sceneView.setCameraController(cameraController);
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
