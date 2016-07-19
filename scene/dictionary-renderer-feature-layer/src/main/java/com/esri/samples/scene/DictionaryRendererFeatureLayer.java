/*
 * Copyright 2015 Esri.
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
package com.esri.samples.scene;

import java.io.File;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.datasource.arcgis.Geodatabase;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.symbology.DictionaryRenderer;
import com.esri.arcgisruntime.symbology.SymbolDictionary;

public class DictionaryRendererFeatureLayer extends Application {

  // number of layers added to the map
  private int layerCounter = 0;

  private SceneView sceneView;
  // location for view to zoom to after layers are loaded to map
  private Envelope geometryBoundary;

  private static final String ELEVATION_IMAGE_SERVICE =
      "http://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer";

  @Override
  public void start(Stage stage) throws Exception {
    sceneView = new SceneView();
    StackPane appWindow = new StackPane(sceneView);
    Scene scenefx = new Scene(appWindow);

    // set title, size, and add scene to stage
    stage.setTitle("Dictionary Renderer Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scenefx);
    stage.show();

    ArcGISScene scene = new ArcGISScene(Basemap.createImagery());
    sceneView.setArcGISScene(scene);

    // add base surface for elevation data
    Surface surface = new Surface();
    surface.getElevationSources().add(new ArcGISTiledElevationSource(ELEVATION_IMAGE_SERVICE));
    scene.setBaseSurface(surface);

    // load geo-database from local location
    File databaseLocation = new File(getClass().getResource("/militaryoverlay.geodatabase").getPath());
    Geodatabase geodatabase = new Geodatabase(databaseLocation.getAbsolutePath());
    geodatabase.loadAsync();

    // render tells layer what symbols to apply to what features
    SymbolDictionary symbolDictionary = new SymbolDictionary("mil2525d");
    symbolDictionary.loadAsync();

    geodatabase.addDoneLoadingListener(() -> {
      geodatabase.getGeodatabaseFeatureTables().forEach(table -> {
        FeatureLayer featureLayer = new FeatureLayer(table);
        featureLayer.loadAsync();

        featureLayer.addDoneLoadingListener(() -> {
          layerCounter++;
          if (layerCounter == geodatabase.getGeodatabaseFeatureTables().size()) {
            LayerList sceneLayers = scene.getOperationalLayers();
            if (sceneLayers.size() >= 0) {
              geometryBoundary = null;
              sceneLayers.forEach(layer -> {
                geometryBoundary = geometryBoundary == null ? layer.getFullExtent()
                    : GeometryEngine.union(geometryBoundary, layer.getFullExtent()).getExtent();
              });
              geometryBoundary = GeometryEngine.project(geometryBoundary, sceneView.getSpatialReference()).getExtent();
              Camera camera = new Camera(geometryBoundary.getCenter(), 15000, 0.0, 50.0, 0.0);
              sceneView.setViewpointCamera(camera);
            }
          }
        });

        scene.getOperationalLayers().add(featureLayer);
        DictionaryRenderer dictionaryRenderer = new DictionaryRenderer(symbolDictionary);
        featureLayer.setRenderer(dictionaryRenderer);
      });
    });
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

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
