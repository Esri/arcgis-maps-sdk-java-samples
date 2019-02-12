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

package com.esri.samples.scene.orbit_the_camera_around_an_object;

import java.io.File;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.mapping.view.DrawStatus;
import com.esri.arcgisruntime.mapping.view.DrawStatusChangedEvent;
import com.esri.arcgisruntime.mapping.view.DrawStatusChangedListener;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LayerSceneProperties;
import com.esri.arcgisruntime.mapping.view.OrbitGeoElementCameraController;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.symbology.ModelSceneSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;

public class OrbitTheCameraAroundAnObjectController {

  @FXML
  private SceneView sceneView;
  @FXML
  private CheckBox allowDistanceInteractionCheckBox;
  @FXML
  private Slider cameraHeadingSlider;
  @FXML
  private Slider planePitchSlider;

  private OrbitGeoElementCameraController orbitCameraController;


  public void initialize() {

    try {
      
      // create a scene and add a basemap to it
      ArcGISScene scene = new ArcGISScene();
      scene.setBasemap(Basemap.createImagery());
      sceneView.setArcGISScene(scene);

      // add a base surface with elevation data
      Surface surface = new Surface();
      ArcGISTiledElevationSource elevationSource = new ArcGISTiledElevationSource("http://elevation3d.arcgis" +
              ".com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer");
      surface.getElevationSources().add(elevationSource);
      scene.setBaseSurface(surface);

      // create a graphics overlay for the scene
      GraphicsOverlay sceneGraphicsOverlay = new GraphicsOverlay();
      sceneGraphicsOverlay.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.RELATIVE);
      sceneView.getGraphicsOverlays().add(sceneGraphicsOverlay);

      // create a renderer to control the plane's orientation by its attributes
      SimpleRenderer renderer = new SimpleRenderer();
      renderer.getSceneProperties().setHeadingExpression("[HEADING]");
      renderer.getSceneProperties().setPitchExpression("[PITCH]");
      renderer.getSceneProperties().setRollExpression("[ROLL]");
      sceneGraphicsOverlay.setRenderer(renderer);

      // create a graphic of a plane model
      String modelURI = new File("./samples-data/bristol/Collada/Bristol.dae").getAbsolutePath();
      ModelSceneSymbol plane3DSymbol = new ModelSceneSymbol(modelURI, 1.0);
      plane3DSymbol.loadAsync();
      // position the plane over a runway
      Graphic plane = new Graphic(new Point(6.637, 45.399, 100, SpatialReferences.getWgs84()), plane3DSymbol);
      // initialize the plane's heading to line up with the runway
      plane.getAttributes().put("HEADING", 45.0);
      sceneGraphicsOverlay.getGraphics().add(plane);

      // control the plane's pitch with a slider
      planePitchSlider.valueProperty().addListener(o -> plane.getAttributes().put("PITCH", planePitchSlider.getValue()));

      // listener for the view to stop loading and to add the camera controller
      DrawStatusChangedListener listener = new DrawStatusChangedListener() {

        @Override
        public void drawStatusChanged(DrawStatusChangedEvent drawStatusChangedEvent) {
          if (drawStatusChangedEvent.getDrawStatus() == DrawStatus.COMPLETED) {

              // create an orbit geoelement camera controller with the plane as the target
              orbitCameraController = new OrbitGeoElementCameraController(plane, 50.0);

              // restrict the camera's heading to stay behind the plane
              orbitCameraController.setMinCameraHeadingOffset(-45);
              orbitCameraController.setMaxCameraHeadingOffset(45);

              // restrict the camera's pitch so it doesn't go completely vertical or collide with the ground
              orbitCameraController.setMinCameraPitchOffset(10);
              orbitCameraController.setMaxCameraPitchOffset(100);

              // restrict the camera to stay between 10 and 1000 meters from the plane
              orbitCameraController.setMinCameraDistance(10);
              orbitCameraController.setMaxCameraDistance(100);

              // position the plane a third from the bottom of the screen
              orbitCameraController.setTargetVerticalScreenFactor(0.33f);

              // don't pitch the camera when the plane pitches
              orbitCameraController.setAutoPitchEnabled(false);

              // set the orbit camera controller to the scene view
              sceneView.setCameraController(orbitCameraController);

              // set the camera's heading using a slider
              cameraHeadingSlider.valueProperty().addListener(o -> orbitCameraController.setCameraHeadingOffset(cameraHeadingSlider.getValue()));

              // update camera heading slider position whilst interacting with the camera heading
              sceneView.addViewpointChangedListener( event -> cameraHeadingSlider.setValue(orbitCameraController.getCameraHeadingOffset()));

            // stop listening for the view to load
            sceneView.removeDrawStatusChangedListener(this);
          }
        }
      };

      sceneView.addDrawStatusChangedListener(listener);

    } catch (Exception e) {
      // on any exception, print the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Animates the camera to a cockpit view. The camera's target is offset to the cockpit (instead of the plane's
   * center). The camera is moved onto the target position to create a swivelling camera effect. Auto pitch is
   * enabled so the camera pitches when the plane pitches.
   */
  @FXML
  private void handleCockpitViewButtonClicked() {
    // disable camera distance interaction checkbox
    allowDistanceInteractionCheckBox.setDisable(true);

    // allow the camera to get closer to the target
    orbitCameraController.setMinCameraDistance(0);

    // pitch the camera when the plane pitches
    orbitCameraController.setAutoPitchEnabled(true);

    // animate the camera target to the cockpit instead of the center of the plane
    orbitCameraController.setTargetOffsetsAsync(0, -2, 1.1, 1);

    // animate the camera so that it is 0.01m from the target (cockpit), facing forward (0 deg heading), and aligned
    // with the horizon (90 deg pitch)
    orbitCameraController.moveCameraAsync(0 - orbitCameraController.getCameraDistance(),
        0 - orbitCameraController.getCameraHeadingOffset(), 90 - orbitCameraController.getCameraPitchOffset(), 1).addDoneListener(() -> {
      // once the camera is in the cockpit, only allow the camera's heading to change
      orbitCameraController.setMinCameraPitchOffset(90);
      orbitCameraController.setMaxCameraPitchOffset(90);
    });
  }

  /**
   * Configures the camera controller for a "follow" view. The camera targets the center of the plane with a default
   * position directly behind and slightly above the plane. Auto pitch is disabled so the camera does not pitch when
   * the plane pitches.
   */
  @FXML
  private void handleFollowViewButtonClicked() {
    allowDistanceInteractionCheckBox.setDisable(false);

    orbitCameraController.setAutoPitchEnabled(false);
    orbitCameraController.setTargetOffsetX(0);
    orbitCameraController.setTargetOffsetY(0);
    orbitCameraController.setTargetOffsetZ(0);
    orbitCameraController.setCameraHeadingOffset(0);
    orbitCameraController.setCameraPitchOffset(45);
    orbitCameraController.setMinCameraPitchOffset(10);
    orbitCameraController.setMaxCameraPitchOffset(100);
    orbitCameraController.setMinCameraDistance(10.0);
    orbitCameraController.setCameraDistance(50.0);
  }

  /**
   * Toggle interactive distance. When distance interaction is disabled, the user cannot zoom in with the mouse.
   */
  @FXML
  private void handleDistanceInteractionCheckBoxToggle() {
    orbitCameraController.setCameraDistanceInteractive(allowDistanceInteractionCheckBox.isSelected());
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
