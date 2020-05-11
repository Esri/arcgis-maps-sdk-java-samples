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

package com.esri.samples.service_feature_table_no_cache;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;

public class ServiceFeatureTableNoCacheSample extends Application {

  private FeatureLayer featureLayer;
  private MapView mapView;
  private ServiceFeatureTable serviceFeatureTable; // keeps loadable in scope to avoid garbage collection

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Service Feature Table No Cache Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a view for this ArcGISMap
      mapView = new MapView();

      // create a ArcGISMap with the light Gray Canvas basemap
      ArcGISMap map = new ArcGISMap(Basemap.createLightGrayCanvas());

      // set ArcGISMap to be displayed in ArcGISMap view
      mapView.setMap(map);

      // set an initial viewpoint
      map.setInitialViewpoint(new Viewpoint(new Envelope(-140.740858094945, 14.1552479740679, -47.693259181055,
              64.8874243113506, SpatialReferences.getWgs84())));

      // create the service feature table
      serviceFeatureTable = new ServiceFeatureTable("https://services2.arcgis.com/ZQgQTuoyBrtmoGdP/arcgis/rest/services/US_Bridges/FeatureServer/0");

      // explicitly set the mode to no cache
      serviceFeatureTable.setFeatureRequestMode(ServiceFeatureTable.FeatureRequestMode.ON_INTERACTION_NO_CACHE);

      // wait for the service feature table to load
      serviceFeatureTable.loadAsync();
      serviceFeatureTable.addDoneLoadingListener(() -> {
        if (serviceFeatureTable.getLoadStatus() == LoadStatus.LOADED) {

          // create the feature layer using the service feature table
          featureLayer = new FeatureLayer(serviceFeatureTable);

          // add the layer to the ArcGISMap
          map.getOperationalLayers().add(featureLayer);

        } else {
          new Alert(Alert.AlertType.ERROR, "Error loading Service Feature Table").show();
        }
      });

      // add the map view to stack pane
      stackPane.getChildren().addAll(mapView);

    } catch (Exception e) {
      // on any error, display the stack trace
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
