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

package com.esri.samples.featurelayers.feature_collection_layer_query;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.FeatureCollection;
import com.esri.arcgisruntime.data.FeatureCollectionTable;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureCollectionLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class FeatureCollectionLayerQuerySample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {

    try {
      // create stackpane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Feature Collection Layer Query Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a ArcGISMap with a imagery basemap
      ArcGISMap map = new ArcGISMap();
      map.setBasemap(Basemap.createTopographic());

      // create a view for the ArcGISMap and set the ArcGISMap to it
      mapView = new MapView();
      mapView.setMap(map);

      // initialize service feature table to be queried (in this sample, wild fire response points)
      FeatureTable featureTable = new ServiceFeatureTable("https://sampleserver6.arcgisonline.com/arcgis/rest/services/Wildfire/FeatureServer/0");

      // create query parameters
      QueryParameters queryParams = new QueryParameters();
      queryParams.setWhereClause("1=1"); // 1=1 will give all the features from the table

      // query feature from the table
      ListenableFuture<FeatureQueryResult> queryResultListenableFuture = featureTable.queryFeaturesAsync(queryParams);
      queryResultListenableFuture.addDoneListener(() -> {

        try {
          // create a feature collection table from the query results
          FeatureCollectionTable featureCollectionTable = new FeatureCollectionTable(queryResultListenableFuture.get());

          // create a feature collection from the above feature collection table
          FeatureCollection featureCollection = new FeatureCollection();
          featureCollection.getTables().add(featureCollectionTable);

          // create a feature collection layer with the above feature collection
          FeatureCollectionLayer featureCollectionLayer = new FeatureCollectionLayer(featureCollection);

          // add the feature collection layer to the map's operational layers
          mapView.getMap().getOperationalLayers().add(featureCollectionLayer);

        } catch (Exception e) {
          // on any error, display the stack trace
          e.printStackTrace();
        }
      });

      // add the map view to the stack pane
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
