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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;


import java.io.File;

public class OrbitCameraControllerController {

  @FXML private OrbitGeoElementCameraController orbitCameraController;
  @FXML private SceneView sceneView;
  @FXML private Slider headingSlider;
  @FXML private Slider pitchSlider;
  @FXML private Slider distanceSlider;
  @FXML private Button asyncButton;
  @FXML private Button returnButton;
  @FXML private CheckBox cameraHeadingCheckbox;
  @FXML private CheckBox cameraPitchCheckbox;
  @FXML private CheckBox cameraDistanceCheckbox;
  @FXML private Spinner<Integer> cameraMinHeadingSpinner;
  @FXML private Spinner<Integer> cameraMaxHeadingSpinner;

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
      // set the orbit camera controller to the scene view
      sceneView.setCameraController(orbitCameraController);
      // set up initial camera settings
      initializeCameraController();

      // CAMERA CONTROLLER OPTIONS: HEADING, PITCH AND DISTANCE
      // -------------------------------------------------------
      // set the maximum and minimum camera heading offset, and choose if it is interactive.
      cameraHeadingControllerOptions(cameraMinHeadingSpinner, cameraMaxHeadingSpinner, headingSlider);

      // set the maximum and minimum camera pitch offset, and choose if it is interactive.
      cameraPitchControllerOptions(cameraMinHeadingSpinner, cameraMaxHeadingSpinner, headingSlider);

      // set the maximum and minimum camera distance, and choose if it is interactive.
      cameraDistanceControllerOptions(cameraMinHeadingSpinner, cameraMaxHeadingSpinner, headingSlider);

      // INTERACT WITH THE CAMERA OPTIONS: HEADING, PITCH AND DISTANCE
      // -------------------------------------------------------------
      cameraHeadingCheckbox.setOnAction(event -> {
        orbitCameraController.setCameraDistanceInteractive(cameraHeadingCheckbox.isSelected());
      });

      cameraPitchCheckbox.setOnAction(event -> {
        orbitCameraController.setCameraPitchOffsetInteractive(cameraPitchCheckbox.isSelected());
      });

      cameraDistanceCheckbox.setOnAction(event -> {
        orbitCameraController.setCameraHeadingOffsetInteractive(cameraDistanceCheckbox.isSelected());
      });








      // slider for controlling camera heading direction
//      headingSlider.valueProperty().addListener(o -> {
//        orbitCameraController.setCameraHeadingOffset(headingSlider.getValue());
//      });

//      pitchSlider.valueProperty().addListener(o -> {
//        orbitCameraController.setCameraPitchOffset(pitchSlider.getValue());
//      });
//
//      distanceSlider.valueProperty().addListener(o -> {
//        orbitCameraController.setCameraDistance(distanceSlider.getValue());
//      });

      // update slider positions whilst interacting with the camera
      sceneView.addViewpointChangedListener(event -> {
        headingSlider.setValue(orbitCameraController.getCameraHeadingOffset());
//        pitchSlider.setValue(orbitCameraController.getCameraPitchOffset());
//        distanceSlider.setValue(orbitCameraController.getCameraDistance());
      });


      // these are always relative to the object
//      asyncButton.setOnAction(event -> {
//        orbitCameraController.setTargetOffsetsAsync(500, 550, 0, 6).addDoneListener(() -> {
//          System.out.println("Done moving");
//        });
//      });
//
//      returnButton.setOnAction(event -> {
//        orbitCameraController.setTargetOffsetsAsync(0, 0, 0, 6).addDoneListener(() -> {
//          System.out.println("Done moving");
//        });
//      });



    } catch (Exception e) {
      // on any exception, print the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Initializes pitch, heading, and max/min distance of the camera.
   */
  private void initializeCameraController() {
    // set the pitch of the camera
    orbitCameraController.setCameraPitchOffset(85);
    // set the heading of the camera
    orbitCameraController.setCameraHeadingOffset(120);
    // set the min max distance of orbit camera
    orbitCameraController.setMaxCameraDistance(500);
    orbitCameraController.setMinCameraDistance(10);
  }


  /**
   * Combines setting the maximum and minimum camera heading offset along with setting if
   * the camera heading offset is interactive.
   */
  private void cameraHeadingControllerOptions(Spinner<Integer> minSpinner, Spinner<Integer> maxSpinner, Slider slider) {

    maxSpinner.valueProperty().addListener(e -> {
      orbitCameraController.setMaxCameraHeadingOffset(maxSpinner.getValue());
      slider.setMax(maxSpinner.getValue());
    });

    minSpinner.valueProperty().addListener(e -> {
      orbitCameraController.setMinCameraHeadingOffset(minSpinner.getValue());
      slider.setMin(minSpinner.getValue());
    });

    slider.valueProperty().addListener(o -> {
      orbitCameraController.setCameraHeadingOffset(slider.getValue());
    });
  }

  /**
   * Combines setting the maximum and minimum camera pitch offset along with setting if
   * the camera pitch offset is interactive.
   */
  private void cameraPitchControllerOptions(Spinner<Integer> minSpinner, Spinner<Integer> maxSpinner, Slider slider) {

    maxSpinner.valueProperty().addListener(e -> {
      orbitCameraController.setMaxCameraPitchOffset(maxSpinner.getValue());
      slider.setMax(maxSpinner.getValue());
    });

    minSpinner.valueProperty().addListener(e -> {
      orbitCameraController.setMinCameraPitchOffset(minSpinner.getValue());
      slider.setMin(minSpinner.getValue());
    });
  }

  /**
   * Combines setting the maximum and minimum camera distance along with setting if
   * the camera distance is interactive.
   */
  private void cameraDistanceControllerOptions(Spinner<Integer> minSpinner, Spinner<Integer> maxSpinner, Slider slider) {

    maxSpinner.valueProperty().addListener(e -> {
      orbitCameraController.setMaxCameraDistance(maxSpinner.getValue());
      slider.setMax(maxSpinner.getValue());
    });

    minSpinner.valueProperty().addListener(e -> {
      orbitCameraController.setMaxCameraDistance(minSpinner.getValue());
      slider.setMin(minSpinner.getValue());
    });
  }





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
