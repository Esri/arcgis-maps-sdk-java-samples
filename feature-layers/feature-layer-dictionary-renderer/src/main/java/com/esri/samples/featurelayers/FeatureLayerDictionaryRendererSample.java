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
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.DictionaryRenderer;
import com.esri.arcgisruntime.symbology.SymbolDictionary;

public class FeatureLayerDictionaryRendererSample extends Application {

  private MapView mapView;
  private Envelope initialViewpoint;

  @Override
  public void start(Stage stage) throws Exception {

    mapView = new MapView();
    StackPane appWindow = new StackPane(mapView);
    Scene scene = new Scene(appWindow);

    // set title, size, and add scene to stage
    stage.setTitle("Feature Layer Dictionary Renderer Sample");
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

    // render tells layer what symbols to apply to what features
    SymbolDictionary symbolDictionary = new SymbolDictionary("mil2525d");
    symbolDictionary.loadAsync();

    geodatabase.addDoneLoadingListener(() -> {
      geodatabase.getGeodatabaseFeatureTables().forEach(table -> {
        // add each layer to map
        FeatureLayer featureLayer = new FeatureLayer(table);
        featureLayer.loadAsync();
        // Features no longer show after this scale
        featureLayer.setMinScale(1000000);
        map.getOperationalLayers().add(featureLayer);

        // displays features from layer using mil2525d symbols
        DictionaryRenderer dictionaryRenderer = new DictionaryRenderer(symbolDictionary);
        featureLayer.setRenderer(dictionaryRenderer);

        featureLayer.addDoneLoadingListener(() -> {
          // initial viewpoint to encompass all graphics displayed on the map view 
          initialViewpoint = initialViewpoint == null ? featureLayer.getFullExtent()
              : GeometryEngine.union(initialViewpoint, featureLayer.getFullExtent()).getExtent();
        });
      });
    });

    // once view has loaded
    mapView.addSpatialReferenceChangedListener(e -> {
      // set initial viewpoint
      mapView.setViewpointGeometryAsync(initialViewpoint);
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
