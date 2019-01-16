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
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;

import com.esri.arcgisruntime.mapping.view.GlobeCameraController;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LayerSceneProperties;
import com.esri.arcgisruntime.mapping.view.OrbitGeoElementCameraController;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.symbology.ModelSceneSymbol;

import javafx.application.Application;
import javafx.fxml.FXML;
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

public class ControlTheCameraController {

  @FXML private OrbitGeoElementCameraController orbitCameraController;
  @FXML private SceneView sceneView;

  public void initialize() {

    try {

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

      // create a graphics overlay for the scene
      GraphicsOverlay sceneGraphicsOverlay = new GraphicsOverlay();
      sceneGraphicsOverlay.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.ABSOLUTE);
      sceneView.getGraphicsOverlays().add(sceneGraphicsOverlay);

      // create a control panel for camera controls
      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0, 0, 0, 0.3)"),
              CornerRadii.EMPTY, Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(180, 250);
      controlsVBox.getStyleClass().add("panel-region");

      // create a control panel for camera type toggling
      VBox buttonsVBox = new VBox(10);
      buttonsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0, 0, 0, 0.3)"),
              CornerRadii.EMPTY, Insets.EMPTY)));
      buttonsVBox.setPadding(new Insets(10.0));
      buttonsVBox.setMaxSize(180, 100);
      buttonsVBox.getStyleClass().add("panel-region");

      // label for displaying current active camera
      Label cameraModeLabel = new Label("Active Camera: Fixed view");
      cameraModeLabel.setTextAlignment(TextAlignment.CENTER);
      cameraModeLabel.setPadding(new Insets(0, 0, 0, 10));
      // set up labels for camera controls
      Label cameraHeadingLabel = new Label("Camera Heading");
      cameraHeadingLabel.setPadding(new Insets(0, 0, 0, 37));
      Label cameraPitchLabel = new Label("Camera Pitch");
      cameraPitchLabel.setPadding(new Insets(0, 0, 0, 42));
      Label cameraDistanceLabel = new Label("Distance from Camera (m)");
      cameraDistanceLabel.setPadding(new Insets(0, 0, 0, 15));

      // create a graphic with a ModelSceneSymbol of a plane to add to the scene
      String modelURI = new File("./samples-data/bristol/Collada/Bristol.dae").getAbsolutePath();
      ModelSceneSymbol plane3DSymbol = new ModelSceneSymbol(modelURI, 1.0);
      plane3DSymbol.loadAsync();
      plane3DSymbol.setHeading(45);
      Graphic plane3D = new Graphic(new Point(6.637, 45.399, 1955, SpatialReferences.getWgs84()), plane3DSymbol);
      sceneGraphicsOverlay.getGraphics().add(plane3D);

      // instantiate a new camera controller which orbits a given geo element at a certain distance
      orbitCameraController = new OrbitGeoElementCameraController(plane3D, 100.0);
      // this controls the pitch of the camera
      orbitCameraController.setCameraPitchOffset(85);
      // this controls the heading of the camera
      orbitCameraController.setCameraHeadingOffset(120);
      // control min max distance of orbit camera
      orbitCameraController.setMaxCameraDistance(500);
      orbitCameraController.setMinCameraDistance(10);

      // set the orbit camera controller to the scene view
      sceneView.setCameraController(orbitCameraController);

      // slider for controlling camera heading direction
      Slider headingSlider = new Slider(-180, 180, 1);
      headingSlider.setValue(120);
      headingSlider.setShowTickMarks(true);
      headingSlider.setMajorTickUnit(60);
      headingSlider.setShowTickLabels(true);
      headingSlider.valueProperty().addListener( o -> { orbitCameraController.setCameraHeadingOffset(headingSlider.getValue());});

      // slider for controlling camera pitch direction
      Slider pitchSlider = new Slider(0, 180, 1);
      pitchSlider.setValue(85);
      pitchSlider.setShowTickMarks(true);
      pitchSlider.setShowTickLabels(true);
      pitchSlider.setOrientation(Orientation.VERTICAL);
      pitchSlider.setPrefHeight(100);
      pitchSlider.setMajorTickUnit(60);
      pitchSlider.setPadding(new Insets(0, 75, 0, 75));
      pitchSlider.valueProperty().addListener( o -> { orbitCameraController.setCameraPitchOffset(pitchSlider.getValue()); });

      // slider for controlling camera distance
      Slider distanceSlider = new Slider(10, 500, 1);
      distanceSlider.setValue(orbitCameraController.getCameraDistance());
      distanceSlider.setShowTickMarks(true);
      distanceSlider.setMajorTickUnit(250);
      distanceSlider.setShowTickLabels(true);
      distanceSlider.valueProperty().addListener(o -> { orbitCameraController.setCameraDistance(distanceSlider.getValue()); });

      // button for fixing camera to the aeroplane model
      Button fixCameraToPlaneButton = new Button("Fix camera to plane");
      fixCameraToPlaneButton.setMaxWidth(Double.MAX_VALUE);
      fixCameraToPlaneButton.setDisable(true);
      // button for free camera
      Button freeCameraModeButton = new Button ("Free Camera Mode");
      freeCameraModeButton.setMaxWidth(Double.MAX_VALUE);

      fixCameraToPlaneButton.setOnAction(event -> {
        // create an orbit camera controller to restrict the view to the graphic
        sceneView.setCameraController(orbitCameraController);
        fixCameraToPlaneButton.setDisable(true);
        freeCameraModeButton.setDisable(false);
        cameraModeLabel.setText("Active Camera: Fixed view");
        headingSlider.setDisable(false);
        pitchSlider.setDisable(false);
        distanceSlider.setDisable(false);
      });

      freeCameraModeButton.setOnAction(event -> {
        // create a globe camera controller to allow panning of the view across the scene
        sceneView.setCameraController(new GlobeCameraController());
        // set the viewpoint to the current camera
        sceneView.setViewpointCamera(sceneView.getCurrentViewpointCamera());
        freeCameraModeButton.setDisable(true);
        fixCameraToPlaneButton.setDisable(false);
        cameraModeLabel.setText("Active Camera: Free view");
        headingSlider.setDisable(true);
        pitchSlider.setDisable(true);
        distanceSlider.setDisable(true);
      });

      // update slider positions whilst interacting with the camera
      sceneView.addViewpointChangedListener(event -> {
        headingSlider.setValue(orbitCameraController.getCameraHeadingOffset());
        pitchSlider.setValue(orbitCameraController.getCameraPitchOffset());
        distanceSlider.setValue(orbitCameraController.getCameraDistance());
      });

      // add labels, sliders and buttons to appropriate VBox
      controlsVBox.getChildren().addAll(cameraHeadingLabel, headingSlider, cameraPitchLabel, pitchSlider, cameraDistanceLabel, distanceSlider);
      buttonsVBox.getChildren().addAll(cameraModeLabel, fixCameraToPlaneButton, freeCameraModeButton);

      // add scene view to the stack pane
//      stackPane.getChildren().addAll(sceneView, controlsVBox, buttonsVBox);
//      StackPane.setAlignment(controlsVBox, Pos.BOTTOM_RIGHT);
//      StackPane.setAlignment(buttonsVBox, Pos.BOTTOM_LEFT);
//      StackPane.setMargin(controlsVBox, new Insets(0, 10, 30, 0));
//      StackPane.setMargin(buttonsVBox, new Insets(0, 0, 30, 10));

    } catch (Exception e) {
      // on any exception, print the stack trace
      e.printStackTrace();
    }
  }
//  }

  /**
   * Disposes of application resources.
   */
  void terminate() {

    // release resources when the application closes
    if (sceneView != null) {
      sceneView.dispose();
    }
  }
}
