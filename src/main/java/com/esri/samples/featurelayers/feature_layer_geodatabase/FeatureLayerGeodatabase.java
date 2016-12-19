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

package com.esri.samples.featurelayers.feature_layer_geodatabase;

import java.io.File;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.data.Geodatabase;
import com.esri.arcgisruntime.data.GeodatabaseFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.ArcGISVectorTiledLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;

public class FeatureLayerGeodatabase extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) throws Exception {

    try {

      // create stack pane and JavaFX app scene
      StackPane stackPane = new StackPane();
      Scene fxScene = new Scene(stackPane);

      // set title, size, and add JavaFX scene to stage
      stage.setTitle("Feature Layer Geodatabase Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(fxScene);
      stage.show();

      // add vector tiled layer of Los Angeles as basemap to map
      String vectorTileUrl = new File(getClass().getResource("/LosAngeles.vtpk").getPath()).getAbsolutePath();
      ArcGISVectorTiledLayer tiledLayer = new ArcGISVectorTiledLayer(vectorTileUrl);
      tiledLayer.loadAsync();
      Basemap basemap = new Basemap(tiledLayer);
      ArcGISMap map = new ArcGISMap(basemap);

      // add map to voew
      mapView = new MapView();
      mapView.setMap(map);

      // set initial view point of map view with scale 
      Point initialPoint = new Point(-1.3160351979111826E7, 4033294.989576314, mapView.getSpatialReference());
      mapView.setViewpointAsync(new Viewpoint(initialPoint, 35e4));

      // create geodatabase from local resource
      String geodatabaseUrl = new File(getClass().getResource("/LA_Trails.geodatabase").getPath()).getAbsolutePath();
      Geodatabase geodatabase = new Geodatabase(geodatabaseUrl);
      geodatabase.addDoneLoadingListener(() -> {
        // access the geodatabase's feature table Trailheads
        GeodatabaseFeatureTable geodatabaseFeatureTable = geodatabase.getGeodatabaseFeatureTable("Trailheads");
        geodatabaseFeatureTable.loadAsync();

        // create a layer from the geodatabase feature table above and add to map
        FeatureLayer featureLayer = new FeatureLayer(geodatabaseFeatureTable);
        map.getOperationalLayers().add(featureLayer);
      });
      // load geodatabase
      geodatabase.loadAsync();

      // add the map view to stack pane
      stackPane.getChildren().addAll(mapView);
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
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
