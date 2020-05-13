/*
 * Copyright 2018 Esri.
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

package com.esri.samples.time_based_query;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.TimeExtent;
import com.esri.arcgisruntime.mapping.view.MapView;

public class TimeBasedQuerySample extends Application {

  private MapView mapView;
  private ServiceFeatureTable serviceFeatureTable; // keep loadable in scope to avoid garbage collection

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // size the stage, add a title, and set scene to stage
      stage.setTitle("Time Based Query Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a map and set it to a map view
      mapView = new MapView();
      ArcGISMap map = new ArcGISMap(Basemap.createOceans());
      mapView.setMap(map);

      // create a feature table with the URL of the feature service
      String serviceURL = "https://sampleserver6.arcgisonline.com/arcgis/rest/services/Hurricanes/MapServer/0";
      serviceFeatureTable = new ServiceFeatureTable(serviceURL);

      // define the request mode to manual
      serviceFeatureTable.setFeatureRequestMode(ServiceFeatureTable.FeatureRequestMode.MANUAL_CACHE);

      // load the table and set the query
      serviceFeatureTable.addDoneLoadingListener(() -> {
        if (serviceFeatureTable.getLoadStatus() == LoadStatus.LOADED) {
          // create query parameters
          QueryParameters queryParameters = new QueryParameters();
          // set a time extent (beginning of time to September 16th, 2000)
          Calendar beg = new Calendar.Builder().setDate(1, 1, 1).build();
          Calendar end = new Calendar.Builder().setDate(2000, 9, 16).build();
          TimeExtent timeExtent = new TimeExtent(beg, end);
          queryParameters.setTimeExtent(timeExtent);

          // return all fields
          List<String> outputFields = Collections.singletonList("*");

          // populate the service with features that fit the time extent, when done zoom to the layer's extent
          serviceFeatureTable.populateFromServiceAsync(queryParameters, true, outputFields).addDoneListener(() -> {
            mapView.setViewpointGeometryAsync(serviceFeatureTable.getExtent());
          });

        } else {
          new Alert(Alert.AlertType.ERROR, serviceFeatureTable.getLoadError().getMessage()).show();
        }
      });

      // create the feature layer using the service feature table
      FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);

      // add the layer to the map
      map.getOperationalLayers().add(featureLayer);

      // add the map view to stack pane
      stackPane.getChildren().addAll(mapView);

    } catch (Exception e) {
      // on any error, display stack trace
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

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
