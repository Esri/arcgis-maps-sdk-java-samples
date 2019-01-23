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

import com.esri.arcgisruntime.symbology.SimpleRenderer;
import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;


import java.io.File;

public class OrbitCameraControllerController {

  @FXML private Button travelAwayButton;
  @FXML private Button returnButton;
  @FXML private CheckBox cameraHeadingCheckbox;
  @FXML private CheckBox cameraPitchCheckbox;
  @FXML private CheckBox cameraDistanceCheckbox;
  @FXML private CheckBox planeAutoHeadingCheckbox;
  @FXML private CheckBox planeAutoPitchCheckbox;
  @FXML private CheckBox planeAutoRollCheckbox;

  @FXML private OrbitGeoElementCameraController orbitCameraController;
  @FXML private SceneView sceneView;
  @FXML private Slider headingSlider;
  @FXML private Slider pitchSlider;
  @FXML private Slider distanceSlider;
  @FXML private Slider planeHeadingSlider;
  @FXML private Slider planePitchSlider;
  @FXML private Slider planeRollSlider;

  @FXML private Spinner<Integer> cameraMinHeadingSpinner;
  @FXML private Spinner<Integer> cameraMaxHeadingSpinner;
  @FXML private Spinner<Integer> cameraMinPitchSpinner;
  @FXML private Spinner<Integer> cameraMaxPitchSpinner;
  @FXML private Spinner<Integer> cameraMinDistanceSpinner;
  @FXML private Spinner<Integer> cameraMaxDistanceSpinner;
  @FXML private Spinner<Integer> targetXSpinner;
  @FXML private Spinner<Integer> targetYSpinner;
  @FXML private Spinner<Integer> targetZSpinner;
  @FXML private Spinner<Double> screenFactorSpinner;




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

      // add rendered using rotation expressions
      SimpleRenderer renderer = new SimpleRenderer();
      renderer.getSceneProperties().setHeadingExpression("[HEADING]");
      renderer.getSceneProperties().setPitchExpression("[PITCH]");
      renderer.getSceneProperties().setRollExpression("[ROLL]");
      sceneGraphicsOverlay.setRenderer(renderer);

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
      cameraPitchControllerOptions(cameraMinPitchSpinner, cameraMaxPitchSpinner, pitchSlider);

      // set the maximum and minimum camera distance, and choose if it is interactive.
      cameraDistanceControllerOptions(cameraMinDistanceSpinner, cameraMaxDistanceSpinner, distanceSlider);

      // INTERACT WITH THE CAMERA OPTIONS: HEADING, PITCH AND DISTANCE
      // -------------------------------------------------------------
      cameraHeadingCheckbox.setOnAction(event -> {
        orbitCameraController.setCameraHeadingOffsetInteractive(cameraHeadingCheckbox.isSelected());
      });

      cameraPitchCheckbox.setOnAction(event -> {
        orbitCameraController.setCameraPitchOffsetInteractive(cameraPitchCheckbox.isSelected());
      });

      cameraDistanceCheckbox.setOnAction(event -> {
        orbitCameraController.setCameraDistanceInteractive(cameraDistanceCheckbox.isSelected());
      });

      // update slider positions whilst interacting with the camera
      sceneView.addViewpointChangedListener(event -> {
        headingSlider.setValue(orbitCameraController.getCameraHeadingOffset());
        pitchSlider.setValue(orbitCameraController.getCameraPitchOffset());
        distanceSlider.setValue(orbitCameraController.getCameraDistance());
      });

      // set async camera movement away from the plane
      travelAwayButton.setOnAction(event -> {
        orbitCameraController.setTargetOffsetsAsync(500, 550, 0, 6).addDoneListener(() -> {
        });
      });

      // set async camera movement back to the plane
      returnButton.setOnAction(event -> {
        orbitCameraController.setTargetOffsetsAsync(0, 0, 0, 6).addDoneListener(() -> {
        });
      });

      // use the relevant spinner to set the target offset x, y and z
      targetXSpinner.valueProperty().addListener(e -> {
        orbitCameraController.setTargetOffsetX(targetXSpinner.getValue());
      });

      targetYSpinner.valueProperty().addListener(e -> {
        orbitCameraController.setTargetOffsetY(targetYSpinner.getValue());
      });

      targetZSpinner.valueProperty().addListener(e -> {
        orbitCameraController.setTargetOffsetZ(targetZSpinner.getValue());
      });

      // set up plane heading, pitch and roll
      planeHeadingSlider.valueProperty().addListener(o -> plane3D.getAttributes().put("HEADING", planeHeadingSlider.getValue()));
      planePitchSlider.valueProperty().addListener(o -> plane3D.getAttributes().put("PITCH", planePitchSlider.getValue()));
      planeRollSlider.valueProperty().addListener(o-> plane3D.getAttributes().put("ROLL", planeRollSlider.getValue()));

      planeAutoHeadingCheckbox.setOnAction(event -> {
        orbitCameraController.setAutoHeadingEnabled(planeAutoHeadingCheckbox.isSelected());
      });

      planeAutoPitchCheckbox.setOnAction(event -> {
        orbitCameraController.setAutoPitchEnabled(planeAutoPitchCheckbox.isSelected());
      });

      planeAutoRollCheckbox.setOnAction(event -> {
        orbitCameraController.setAutoRollEnabled(planeAutoRollCheckbox.isSelected());
      });

      // set up vertical screen factor
      screenFactorSpinner.valueProperty().addListener( e -> {

        double valuex = screenFactorSpinner.getValue();
        float valuef = (float)valuex;
        orbitCameraController.setTargetVerticalScreenFactor(valuef);
      });



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
    orbitCameraController.setMaxCameraDistance(1000);
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

    slider.valueProperty().addListener(o -> {
      orbitCameraController.setCameraPitchOffset(slider.getValue());
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
      orbitCameraController.setMinCameraDistance(minSpinner.getValue());
      slider.setMin(minSpinner.getValue());
    });

    slider.valueProperty().addListener(o -> {
      orbitCameraController.setCameraDistance(slider.getValue());
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
