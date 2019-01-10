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

package com.esri.samples.scene.control_the_camera;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;

import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.GlobeCameraController;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LayerSceneProperties;
import com.esri.arcgisruntime.mapping.view.OrbitGeoElementCameraController;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.symbology.ModelSceneSymbol;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.File;

public class ControlTheCameraSample extends Application {

  private OrbitGeoElementCameraController orbitCameraController;
  private GlobeCameraController globeCameraController;
  private SceneView sceneView;
  private Graphic plane3D;
  private static final SpatialReference WGS84 = SpatialReferences.getWgs84();
  private Camera camera;


  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and JavaFX app scene
      StackPane stackPane = new StackPane();
      Scene fxScene = new Scene(stackPane);

      // set title, size, and add JavaFX scene to stage
      stage.setTitle("Control the camera");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(fxScene);
      stage.show();

      // create a scene and add a basemap to it
      ArcGISScene scene = new ArcGISScene();
      scene.setBasemap(Basemap.createImagery());

      sceneView = new SceneView();
      sceneView.setArcGISScene(scene);

      // add base surface for elevation data
      Surface surface = new Surface();
      ArcGISTiledElevationSource elevationSource = new ArcGISTiledElevationSource("http://elevation3d.arcgis" +
              ".com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer");
      surface.getElevationSources().add(elevationSource);
      scene.setBaseSurface(surface);

      // create a control panel

      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0, 0, 0, 0.3)"),
              CornerRadii.EMPTY, Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(180, 400);
      controlsVBox.getStyleClass().add("panel-region");

      Button activateCameraButton = new Button("Fix camera to object");
      activateCameraButton.setMaxWidth(Double.MAX_VALUE);
      activateCameraButton.setDisable(true);
      Button activateGlobalViewButton = new Button ("Exit Fixed Camera Mode");
      activateGlobalViewButton.setMaxWidth(Double.MAX_VALUE);

      Label label = new Label("Active Camera: Fixed view");
      label.setWrapText(true);
      label.setTextAlignment(TextAlignment.CENTER);
      label.setMaxWidth(Double.MAX_VALUE);

      // create a graphics overlay for the scene
      GraphicsOverlay sceneGraphicsOverlay = new GraphicsOverlay();
      sceneGraphicsOverlay.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.ABSOLUTE);
      sceneView.getGraphicsOverlays().add(sceneGraphicsOverlay);

      // create a graphic with a ModelSceneSymbol of a plane to add to the scene
      String modelURI = new File("./samples-data/bristol/Collada/Bristol.dae").getAbsolutePath();
      ModelSceneSymbol plane3DSymbol = new ModelSceneSymbol(modelURI, 1.0);
      plane3DSymbol.loadAsync();
      plane3DSymbol.setHeading(45);
      plane3D = new Graphic(new Point(6.637, 45.399, 1955, WGS84), plane3DSymbol);
      sceneGraphicsOverlay.getGraphics().add(plane3D);

      globeCameraController = new GlobeCameraController();

      // instantiate a new camera controller which orbits a given geo element at a certain distance
      orbitCameraController = new OrbitGeoElementCameraController(plane3D, 100.0);
      // set up default camera position
      // this controls the pitch of the camera
      orbitCameraController.setCameraPitchOffset(85);
      // this controls the heading of the camera
      orbitCameraController.setCameraHeadingOffset(120);
      // control max distance of orbit camera
      orbitCameraController.setMaxCameraDistance(500);

      sceneView.setCameraController(orbitCameraController);

      // slider for controlling heading direction
      Slider headingSlider = new Slider(-180, 180, 1);
      headingSlider.setValue(120);
      headingSlider.setShowTickMarks(true);
      headingSlider.setMajorTickUnit(20);
      headingSlider.setShowTickLabels(true);

      headingSlider.valueProperty().addListener( o -> {
        orbitCameraController.setCameraHeadingOffset(headingSlider.getValue());
      });

      Slider pitchSlider = new Slider(0, 180, 1);
      pitchSlider.setValue(85);
      pitchSlider.setOrientation(Orientation.VERTICAL);
      pitchSlider.setPrefHeight(100);
      pitchSlider.setPadding(new Insets(0, 75, 0, 75));

      pitchSlider.valueProperty().addListener( o -> {
        orbitCameraController.setCameraPitchOffset(pitchSlider.getValue());
      });

      Slider distanceSlider = new Slider(10, 500, 1);
      distanceSlider.setValue(orbitCameraController.getCameraDistance());

      distanceSlider.valueProperty().addListener(o -> {
        orbitCameraController.setCameraDistance(distanceSlider.getValue());
      });


      activateCameraButton.setOnAction(event -> {
        // create an orbit camera controller to restrict the view to the graphic
        sceneView.setCameraController(orbitCameraController);

        activateCameraButton.setDisable(true);
        activateGlobalViewButton.setDisable(false);
        label.setText("Active Camera: Fixed view");
        headingSlider.setVisible(true);
        pitchSlider.setVisible(true);
      });

      activateGlobalViewButton.setOnAction(event -> {
        // create a globe camera controller to allow panning of the view across the scene
        sceneView.setCameraController(globeCameraController);
        // set the viewpoint to the current camera
        sceneView.setViewpointCamera(sceneView.getCurrentViewpointCamera());

        activateGlobalViewButton.setDisable(true);
        activateCameraButton.setDisable(false);
        label.setText("Active Camera: Free view");
        headingSlider.setVisible(false);
        pitchSlider.setVisible(false);

      });

      Label setHeadingLabel = new Label("Camera Heading");
      Label setPitchLabel = new Label("Camera Pitch");
      Label setDistanceLabel = new Label("Distance from Camera");
      
      sceneView.addViewpointChangedListener(event -> {

        headingSlider.setValue(orbitCameraController.getCameraHeadingOffset());
        pitchSlider.setValue(orbitCameraController.getCameraPitchOffset());
        distanceSlider.setValue(orbitCameraController.getCameraDistance());

      });

      controlsVBox.getChildren().addAll(activateCameraButton, label, setHeadingLabel, headingSlider, setPitchLabel, pitchSlider, setDistanceLabel, distanceSlider, activateGlobalViewButton);

      // add scene view to the stack pane
      stackPane.getChildren().addAll(sceneView, controlsVBox);
      StackPane.setAlignment(controlsVBox, Pos.TOP_RIGHT);
      StackPane.setMargin(controlsVBox, new Insets(10, 10, 0, 0));

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
