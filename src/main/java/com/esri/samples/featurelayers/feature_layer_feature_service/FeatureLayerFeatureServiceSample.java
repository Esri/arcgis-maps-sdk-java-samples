/*
 * Copyright 2017 Esri.
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

package com.esri.samples.featurelayers.feature_layer_feature_service;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;

public class FeatureLayerFeatureServiceSample extends Application {

  private MapView mapView;

  private static final String GEOLOGY_FEATURE_SERVICE =
      "http://sampleserver6.arcgisonline.com/arcgis/rest/services/Energy/Geology/FeatureServer/9";

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // size the stage, add a title, and set scene to stage
      stage.setTitle("Feature Layer Feature Service Sample");
      stage.setHeight(700);
      stage.setWidth(800);
      stage.setScene(scene);
      stage.show();

      // create a view for this ArcGISMap
      mapView = new MapView();

      // create a ArcGISMap with the terrain with labels basemap
      ArcGISMap map = new ArcGISMap(Basemap.createTerrainWithLabels());

      // set an initial viewpoint
      map.setInitialViewpoint(new Viewpoint(new Point(-13176752, 4090404, SpatialReferences.getWebMercator()), 500000));

      // create feature layer with its service feature table
      // create the service feature table
      ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(GEOLOGY_FEATURE_SERVICE);

      // create the feature layer using the service feature table
      FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);

      // add the layer to the ArcGISMap
      map.getOperationalLayers().add(featureLayer);

      // set the ArcGISMap to be displayed in the view
      mapView.setMap(map);

      // add the map view to stack pane
      stackPane.getChildren().add(mapView);

    } catch (Exception e) {
      // on any error, display stack trace
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   * 
   * @throws Exception if security manager doesn't allow JVM to exit with
   *           current status
   */
  @Override
  public void stop() throws Exception {

    // release resources when the application closes
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
