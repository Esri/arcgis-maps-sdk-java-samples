/*
 * Copyright 2016 Esri.
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

package com.esri.samples.scene.terrain_exaggeration;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.SceneView;

public class TerrainExaggerationController {

  @FXML private SceneView sceneView;
  @FXML private Slider exaggerationSlider;

  public void initialize() {

    try {

      // create a scene and add a basemap to it
      ArcGISScene scene = new ArcGISScene();
      scene.setBasemap(Basemap.createNationalGeographic());

      // add the SceneView to the stack pane
      sceneView.setArcGISScene(scene);

      // add base surface for elevation data
      Surface surface = new Surface();
      final String elevationImageService =
          "http://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer";
      surface.getElevationSources().add(new ArcGISTiledElevationSource(elevationImageService));
      scene.setBaseSurface(surface);

      // set exaggeration of surface to the value the user selected
      exaggerationSlider.valueChangingProperty().addListener(o -> {
        if (!exaggerationSlider.isValueChanging()) {
          surface.setElevationExaggeration((float) exaggerationSlider.getValue());
        }
      });
      // add a camera and initial camera position
      Point initialLocation = new Point(-119.94891542688772, 46.75792111605992, 0, sceneView.getSpatialReference());
      Camera camera = new Camera(initialLocation, 15000.0, 40.0, 60.0, 0.0);
      sceneView.setViewpointCamera(camera);

    } catch (Exception e) {
      // on any exception, print the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Disposes application resources.
   */
  void terminate() {
    if (sceneView != null) {
      sceneView.dispose();
    }
  }
}
