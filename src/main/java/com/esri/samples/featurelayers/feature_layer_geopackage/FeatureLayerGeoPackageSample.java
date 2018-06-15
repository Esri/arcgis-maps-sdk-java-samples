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

package com.esri.samples.featurelayers.feature_layer_geopackage;

import java.io.File;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.data.GeoPackage;
import com.esri.arcgisruntime.data.GeoPackageFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;

public class FeatureLayerGeoPackageSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Feature Layer GeoPackage Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a map with the streets vector basemap
      ArcGISMap map = new ArcGISMap(Basemap.createStreetsVector());

      // create a GeoPackage from a local gpkg file
      File geoPackageFile = new File("./samples-data/auroraCO/AuroraCO.gpkg");
      GeoPackage geoPackage = new GeoPackage(geoPackageFile.getAbsolutePath());
      geoPackage.loadAsync();

      // create a feature layer from the first feature table in the gpkg
      geoPackage.addDoneLoadingListener(() -> {
        if (geoPackage.getLoadStatus() == LoadStatus.LOADED) {
          List<GeoPackageFeatureTable> featureTables = geoPackage.getGeoPackageFeatureTables();
          if (featureTables.size() > 0) {
            FeatureLayer featureLayer = new FeatureLayer(featureTables.get(0));
            map.getOperationalLayers().add(featureLayer);
            // zoom to the layer
            featureLayer.addDoneLoadingListener(() ->
              mapView.setViewpointAsync(new Viewpoint(featureLayer.getFullExtent()))
            );
          }
        } else {
          Alert alert = new Alert(Alert.AlertType.ERROR, geoPackage.getLoadError().getMessage());
          alert.show();
        }
      });

      // set the map to be displayed in this view
      mapView = new MapView();
      mapView.setMap(map);

      // add the map view to stack pane
      stackPane.getChildren().add(mapView);

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
