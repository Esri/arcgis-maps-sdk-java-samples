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

package com.esri.samples.map.read_geopackage;

import java.io.File;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.data.GeoPackage;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.RasterLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;

public class ReadGeoPackageSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Read GeoPackage Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a map and add it to the map view
      final ArcGISMap map = new ArcGISMap(Basemap.Type.STREETS, 39.7294, -104.8319, 11);
      mapView = new MapView();
      mapView.setMap(map);

      // open the Geopackage
      File geoPackageFile = new File("./samples-data/auroraCO/AuroraCO.gpkg");
      GeoPackage geoPackage = new GeoPackage(geoPackageFile.getAbsolutePath());
      geoPackage.loadAsync();
      geoPackage.addDoneLoadingListener(() -> {

        // get the read only list of GeoPackageRasters from the GeoPackage and loop through each GeoPackageRaster
        geoPackage.getGeoPackageRasters().forEach(raster -> {
          // create a RasterLayer from the GeoPackageRaster
          RasterLayer rasterLayer = new RasterLayer(raster);
          rasterLayer.loadAsync();
          // make the raster layer semi-transparent so we can see layers below it
          rasterLayer.setOpacity(0.5f);
          // add the layer to the map
          map.getOperationalLayers().add(rasterLayer);
        });

        // get the read only list of GeoPackageFeatureTables from the GeoPackage and loop through each GeoPackageFeatureTable
        geoPackage.getGeoPackageFeatureTables().forEach(table -> {
          // create a feature layer from the GeoPackageFeatureTable
          FeatureLayer featureLayer = new FeatureLayer(table);
          featureLayer.loadAsync();
          // add the layer to the map
          map.getOperationalLayers().add(featureLayer);
        });
      });

      // add the map view and control box to stack pane
      stackPane.getChildren().add(mapView);
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
