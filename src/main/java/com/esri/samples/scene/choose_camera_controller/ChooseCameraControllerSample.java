/*
 * Copyright 2019 Esri.
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

package com.esri.samples.scene.choose_camera_controller;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.GlobeCameraController;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LayerSceneProperties;
import com.esri.arcgisruntime.mapping.view.OrbitGeoElementCameraController;
import com.esri.arcgisruntime.mapping.view.OrbitLocationCameraController;
import com.esri.arcgisruntime.mapping.view.SceneView;

import com.esri.arcgisruntime.symbology.ModelSceneSymbol;
import com.esri.arcgisruntime.symbology.SceneSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSceneSymbol;
import javafx.application.Application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.File;

public class ChooseCameraControllerSample extends Application {

  private OrbitGeoElementCameraController orbitCameraController;
  private OrbitLocationCameraController orbitLocationCameraController;
  private SceneView sceneView;

  @Override
  public void start(Stage stage) throws Exception {

    try {

      // create stack pane and JavaFX app scene
      StackPane stackPane = new StackPane();
      Scene fxScene = new Scene(stackPane);
      fxScene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

      // set title, size, and add JavaFX scene to stage
      stage.setTitle("Change Atmosphere Effect Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(fxScene);
      stage.show();

      // create a scene and add a basemap to it
      ArcGISScene scene = new ArcGISScene();
      scene.setBasemap(Basemap.createImagery());

      // set the scene to a scene view
      sceneView = new SceneView();
      sceneView.setArcGISScene(scene);

      // add base surface for elevation data
      Surface surface = new Surface();
      ArcGISTiledElevationSource elevationSource = new ArcGISTiledElevationSource("http://elevation3d.arcgis" +
              ".com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer");
      surface.getElevationSources().add(elevationSource);
      scene.setBaseSurface(surface);

      // create a graphics overlay for the scene
      GraphicsOverlay sceneGraphicsOverlay = new GraphicsOverlay();
      sceneView.getGraphicsOverlays().add(sceneGraphicsOverlay);

      // create a graphic with a SimpleMarkerSceneSymbol to add to the scene
      SimpleMarkerSceneSymbol symbol = new SimpleMarkerSceneSymbol(SimpleMarkerSceneSymbol.Style.DIAMOND, 0xFFEFA70D, 50, 50, 50,
              SceneSymbol.AnchorPosition.CENTER);
      // for the target in utah
//      Graphic floatingSymbol = new Graphic(new Point(-116.851151, 37.563910, 1635, SpatialReferences.getWgs84()), symbol);

//      Graphic floatingSymbol = new Graphic(new Point(-109.901519, 38.410304, 5000, SpatialReferences.getWgs84()), symbol);
//      sceneGraphicsOverlay.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.ABSOLUTE);
//      sceneGraphicsOverlay.getGraphics().add(floatingSymbol);

      // create a graphic with a ModelSceneSymbol of a plane to add to the scene
      String modelURI = new File("./samples-data/bristol/Collada/Bristol.dae").getAbsolutePath();
      ModelSceneSymbol plane3DSymbol = new ModelSceneSymbol(modelURI, 1.0);
      plane3DSymbol.loadAsync();
      plane3DSymbol.setHeading(45);
      Graphic plane3D = new Graphic(new Point(-109.937516, 38.456714, 5000, SpatialReferences.getWgs84()), plane3DSymbol);
      sceneGraphicsOverlay.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.ABSOLUTE);
      sceneGraphicsOverlay.getGraphics().add(plane3D);

      Camera camera = new Camera(38.459291, -109.937576, 5500, 150.0, 20.0, 0.0);
      sceneView.setViewpointCamera(camera);

      // instantiate a new camera controller which orbits the graphic at a certain distance
      orbitCameraController = new OrbitGeoElementCameraController(plane3D, 100.0);
      // set the orbit camera controller to the sceneview upon loading the application
      orbitCameraController.setCameraPitchOffset(30);
      orbitCameraController.setCameraHeadingOffset(150);

      // instantiate a new camera controller which orbits a target
      Point locationPoint = new Point(-109.929589, 38.437304, 3500, SpatialReferences.getWgs84());
      // target below
//      Point locationPoint = new Point(-116.851151, 37.563910, 1655, SpatialReferences.getWgs84());
      orbitLocationCameraController = new OrbitLocationCameraController(locationPoint, 10);
      orbitLocationCameraController.setCameraPitchOffset(3);
      orbitLocationCameraController.setCameraHeadingOffset(150);

      // instantiate a new label to display what camera controller is active
      Label cameraModeLabel = new Label("MOVE FREELY ROUND THE SCENE");
      cameraModeLabel.setMaxWidth(Double.MAX_VALUE);
      cameraModeLabel.setAlignment(Pos.CENTER);

      // instantiate control buttons to choose what camera controller is active
      Button orbitCameraControllerButton = new Button("Orbit Aeroplane");
      orbitCameraControllerButton.setMaxWidth(Double.MAX_VALUE);
      Button globeCameraControllerButton = new Button ("Globe Camera Controller");
      globeCameraControllerButton.setMaxWidth(Double.MAX_VALUE);
      Button orbitLocationCameraControllerButton = new Button ("Orbit Location Camera Controller");
      orbitLocationCameraControllerButton.setMaxWidth(Double.MAX_VALUE);

      // set the camera to an OrbitGeoElementCameraController
      orbitCameraControllerButton.setOnAction(event -> {
        sceneView.setCameraController(orbitCameraController);
        cameraModeLabel.setText("MOVE AROUND AN OBJECT");
      });

      // set the camera to a OrbitLocationCameraController
      orbitLocationCameraControllerButton.setOnAction(event -> {
        sceneView.setCameraController(orbitLocationCameraController);
        cameraModeLabel.setText("MOVE AROUND A LOCATION");
      });


      // set the camera to a newly instantiated GlobeCameraController
      globeCameraControllerButton.setOnAction(event -> {
        sceneView.setCameraController(new GlobeCameraController());
        cameraModeLabel.setText("MOVE FREELY ROUND THE SCENE");
      });

      // instantiate a control panel
      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0, 0, 0, 0.3)"),
              CornerRadii.EMPTY, Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(260, 110);
      controlsVBox.getStyleClass().add("panel-region");
      // add buttons to the control panel
      controlsVBox.getChildren().addAll(orbitCameraControllerButton, orbitLocationCameraControllerButton, globeCameraControllerButton, cameraModeLabel);

      // add scene view, label and control panel to the stack pane
      stackPane.getChildren().addAll(sceneView, controlsVBox);
      StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
      StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));

    } catch (Exception e) {
      // on any exception, print the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {
    // release resources when the application closes
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







