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

package com.esri.samples.featurelayers.feature_layer_selection;

import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;

public class FeatureLayerSelectionSample extends Application {

  private MapView mapView;

  private final String DAMAGE_ASSESSMENT_FEATURE_SERVICE =
      "http://sampleserver6.arcgisonline.com/arcgis/rest/services/DamageAssessment/FeatureServer/0";

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // size the stage, add a title, and set scene to stage
      stage.setTitle("Feature Layer Selection Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a view for this ArcGISMap
      mapView = new MapView();

      // create a ArcGISMap with the streets basemap
      ArcGISMap map = new ArcGISMap(Basemap.createStreets());

      // set an initial viewpoint
      map.setInitialViewpoint(new Viewpoint(new Envelope(-1131596.019761, 3893114.069099, 3926705.982140,
          7977912.461790, 0, 0, SpatialReferences.getWebMercator())));

      // set the ArcGISMap to be displayed in the view
      mapView.setMap(map);

      // create feature layer with its service feature table
      // create the service feature table
      final ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(DAMAGE_ASSESSMENT_FEATURE_SERVICE);

      // create the feature layer using the service feature table
      final FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);
      featureLayer.setSelectionColor(0xFF00FFFF); // cyan color
      featureLayer.setSelectionWidth(3);

      // add the layer to the ArcGISMap
      map.getOperationalLayers().add(featureLayer);

      mapView.setOnMouseClicked(event -> {
        // check for primary or secondary mouse click
        if (event.isStillSincePress() && event.getButton() == MouseButton.PRIMARY) {
          // clear previous results
          featureLayer.clearSelection();

          // create a point from where the user clicked
          Point2D point = new Point2D(event.getX(), event.getY());

          // identify the clicked features
          final ListenableFuture<IdentifyLayerResult> results = mapView.identifyLayerAsync(featureLayer, point, 10,
              false, 10);
          results.addDoneListener(() -> {

            try {
              IdentifyLayerResult layer = results.get();

              // search the layers for identified features
              List<Feature> features = layer.getElements().stream().filter(g -> g instanceof Feature).map(
                  g -> (Feature) g).collect(Collectors.toList());

              // select features
              featureLayer.selectFeatures(features);

            } catch (Exception e) {
              e.printStackTrace();
            }
          });
        }
      });

      // add the map view to stack pane
      stackPane.getChildren().add(mapView);

    } catch (Exception e) {
      // on any error, display exception
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
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
