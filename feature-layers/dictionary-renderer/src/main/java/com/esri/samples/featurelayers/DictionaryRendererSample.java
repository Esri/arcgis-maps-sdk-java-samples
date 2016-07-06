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
package com.esri.samples.featurelayers;

import java.io.File;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.datasource.arcgis.Geodatabase;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.DictionaryRenderer;
import com.esri.arcgisruntime.symbology.SymbolDictionary;

public class DictionaryRendererSample extends Application {

  // number of layers added to the map
  private int layerCounter = 0;

  private MapView mapView;
  // location for view to zoom to after layers are loaded to map
  private Envelope initialExtent;

  @Override
  public void start(Stage stage) throws Exception {
    mapView = new MapView();
    StackPane appWindow = new StackPane(mapView);
    Scene scene = new Scene(appWindow);

    // set title, size, and add scene to stage
    stage.setTitle("Dictionary Renderer Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    ArcGISMap map = new ArcGISMap(Basemap.createTopographic());
    mapView.setMap(map);

    // load geo-database from local location
    File databaseLocation = new File(getClass().getResource("/militaryoverlay.geodatabase").getPath());
    Geodatabase geodatabase = new Geodatabase(databaseLocation.getAbsolutePath());
    geodatabase.loadAsync();

    geodatabase.addDoneLoadingListener(() -> {
      SymbolDictionary symbolDictionary = new SymbolDictionary("mil2525d");

      geodatabase.getGeodatabaseFeatureTables().forEach(table -> {
        FeatureLayer featureLayer = new FeatureLayer(table);
        featureLayer.loadAsync();

        featureLayer.addDoneLoadingListener(() -> {
          layerCounter++;
          if (layerCounter == geodatabase.getGeodatabaseFeatureTables().size()) {
            LayerList mapLayers = map.getOperationalLayers();
            if (mapLayers.size() >= 0) {
              initialExtent = null;
              mapLayers.forEach(layer -> {
                initialExtent = initialExtent == null ? layer.getFullExtent()
                    : GeometryEngine.union(initialExtent, layer.getFullExtent()).getExtent();
              });
              mapView.setViewpoint(new Viewpoint(initialExtent));
            }
          }
        });

        map.getOperationalLayers().add(featureLayer);
        // render tells layer what symbols to apply to what features
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

    if (mapView != null) {
      mapView.dispose();
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
