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

import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.SceneView;

public class TerrainExaggerationController {

  @FXML private SceneView sceneView;
  @FXML private Slider exaggerationSlider;

  private static final String ELEVATION_IMAGE_SERVICE =
      "http://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer";
  private static final String TILED_LAYER =
      "http://tiles.arcgis.com/tiles/Sf0q24s0oDKgX14j/arcgis/rest/services/TIN_DataStructure/MapServer";

  public void initialize() {

    try {

      // create a scene and add a basemap to it
      ArcGISScene scene = new ArcGISScene();
      scene.setBasemap(Basemap.createNationalGeographic());

      // add the SceneView to the stack pane
      sceneView.setArcGISScene(scene);

      // add base surface for elevation data
      Surface surface = new Surface();
      surface.getElevationSources().add(new ArcGISTiledElevationSource(ELEVATION_IMAGE_SERVICE));
      scene.setBaseSurface(surface);

      // add terrain layer to scene
      ArcGISTiledLayer layer = new ArcGISTiledLayer(TILED_LAYER);
      layer.loadAsync();
      scene.getOperationalLayers().add(layer);

      // set exaggeration of surface to the value the user selected
      exaggerationSlider.valueProperty()
          .addListener(e -> surface.setElevationExaggeration((float) exaggerationSlider.getValue()));

      // add a camera and initial camera position
      Camera camera = new Camera(36.8802, -84.9655, 5000.0, 40.0, 50.0, 0.0);
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
    if (sceneView != null)
      sceneView.dispose();
  }
}
