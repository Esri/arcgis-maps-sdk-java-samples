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
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.File;

public class ChooseCameraControllerSample extends Application {

  private SceneView sceneView;

  @Override
  public void start(Stage stage){

    try {

      // create stack pane and JavaFX app scene
      StackPane stackPane = new StackPane();
      Scene fxScene = new Scene(stackPane);
      fxScene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

      // set title, size, and add JavaFX scene to stage
      stage.setTitle("Choose Camera Controller Sample");
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

      // create a graphic with a ModelSceneSymbol of a plane to add to the scene
      String modelURI = new File("./samples-data/bristol/Collada/Bristol.dae").getAbsolutePath();
      ModelSceneSymbol plane3DSymbol = new ModelSceneSymbol(modelURI, 1.0);
      plane3DSymbol.loadAsync();
      plane3DSymbol.setHeading(45);
      Graphic plane3D = new Graphic(new Point(-109.937516, 38.456714, 5000, SpatialReferences.getWgs84()), plane3DSymbol);
      sceneGraphicsOverlay.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.ABSOLUTE);
      sceneGraphicsOverlay.getGraphics().add(plane3D);

      // create a camera and set it as the viewpoint for when the scene loads
      Camera camera = new Camera(38.459291, -109.937576, 5500, 150.0, 20.0, 0.0);
      sceneView.setViewpointCamera(camera);

      // instantiate a new camera controller which orbits the plane at a set distance
      OrbitGeoElementCameraController orbitPlaneCameraController = new OrbitGeoElementCameraController(plane3D, 100.0);
      orbitPlaneCameraController.setCameraPitchOffset(30);
      orbitPlaneCameraController.setCameraHeadingOffset(150);

      // instantiate a new camera controller which orbits a target location
      Point locationPoint = new Point(-109.929589, 38.437304, 1700, SpatialReferences.getWgs84());
      OrbitLocationCameraController orbitLocationCameraController = new OrbitLocationCameraController(locationPoint, 5000);
      orbitLocationCameraController.setCameraPitchOffset(3);
      orbitLocationCameraController.setCameraHeadingOffset(150);

      // create radio buttons for choosing camera controller
      RadioButton orbitPlane = new RadioButton("ORBIT CAMERA AROUND PLANE");
      RadioButton orbitLocation = new RadioButton("ORBIT CAMERA AROUND CRATER");
      RadioButton globeCamera = new RadioButton("FREE PAN ROUND THE GLOBE");
      globeCamera.setSelected(true);

      // set the buttons to a toggle group
      ToggleGroup toggleGroup = new ToggleGroup();
      orbitPlane.setToggleGroup(toggleGroup);
      orbitLocation.setToggleGroup(toggleGroup);
      globeCamera.setToggleGroup(toggleGroup);

      // set the radio buttons to choose which camera controller is active
      orbitPlane.setOnAction(event -> sceneView.setCameraController(orbitPlaneCameraController));

      orbitLocation.setOnAction(event -> sceneView.setCameraController(orbitLocationCameraController));

      globeCamera.setOnAction(event -> sceneView.setCameraController(new GlobeCameraController()));

      // create a control panel
      VBox controlsVBox = new VBox(10);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0, 0, 0, 0.3)"), CornerRadii.EMPTY, Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(300, 80);
      controlsVBox.getStyleClass().add("panel-region");
      // add radio buttons to the control panel
      controlsVBox.getChildren().addAll(orbitPlane, orbitLocation, globeCamera);

      // add scene view, label and control panel to the stack pane
      stackPane.getChildren().addAll(sceneView, controlsVBox);
      StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
      StackPane.setMargin(controlsVBox, new Insets(60, 0, 0, 20));

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







