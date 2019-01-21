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

package com.esri.samples.scene.orbit_camera_controller;

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

import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;


import java.io.File;

public class OrbitCameraControllerController {

  @FXML
  private OrbitGeoElementCameraController orbitCameraController;
  @FXML
  private SceneView sceneView;
  @FXML
  private Slider headingSlider;
  @FXML
  private Slider pitchSlider;
  @FXML
  private Slider distanceSlider;
  @FXML
  private Label cameraModeLabel;
  @FXML
  private Button fixCameraToPlaneButton;
  @FXML
  private Button freeCameraModeButton;

  public void initialize() {

    try {

      // create a scene and add a basemap to it
      ArcGISScene scene = new ArcGISScene();
      scene.setBasemap(Basemap.createImagery());
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
      headingSlider.valueProperty().addListener(o -> {
        orbitCameraController.setCameraHeadingOffset(headingSlider.getValue());
      });

      pitchSlider.valueProperty().addListener(o -> {
        orbitCameraController.setCameraPitchOffset(pitchSlider.getValue());
      });

      distanceSlider.valueProperty().addListener(o -> {
        orbitCameraController.setCameraDistance(distanceSlider.getValue());
      });

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
