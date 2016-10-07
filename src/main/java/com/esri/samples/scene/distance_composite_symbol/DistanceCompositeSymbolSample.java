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

package com.esri.samples.scene.distance_composite_symbol;

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
import com.esri.arcgisruntime.symbology.SimpleMarkerSceneSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class DistanceCompositeSymbolSample extends Application {

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
      stage.setTitle("Distance Composite Symbol");
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
      SimpleMarkerSceneSymbol coneSymbol = SimpleMarkerSceneSymbol.createCone(red, 75, 75);
      coneSymbol.setPitch(-90);
      String modelURI = System.getProperty("user.dir") + "/samples-data/skycrane/Skycrane.lwo";
      ModelSceneSymbol modelSymbol = new ModelSceneSymbol(modelURI, 0.01);
      modelSymbol.setHeading(180);
      modelSymbol.loadAsync();

      // set up the distance composite symbol
      DistanceCompositeSceneSymbol compositeSymbol = new DistanceCompositeSceneSymbol();
      compositeSymbol.getRangeCollection().add(new DistanceCompositeSceneSymbol.Range(modelSymbol, 0, 999));
      compositeSymbol.getRangeCollection().add(new DistanceCompositeSceneSymbol.Range(coneSymbol, 1000, 1999));
      compositeSymbol.getRangeCollection().add(new DistanceCompositeSceneSymbol.Range(circleSymbol, 2000, 0));

      // create graphic
      Point aircraftPosition = new Point(-2.708471, 56.096575, 5000, SpatialReferences.getWgs84());
      Graphic aircraftGraphic = new Graphic(aircraftPosition, compositeSymbol);
      // add graphic to graphics overlay
      graphicsOverlay.getGraphics().add(aircraftGraphic);

      // add a camera and initial camera position
      Camera camera = new Camera(aircraftPosition, 500, 0, 80.0, 0.0);
      sceneView.setViewpointCamera(camera);
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
