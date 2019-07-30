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

package com.esri.samples.read_geopackage;

import java.io.File;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.data.GeoPackage;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.RasterLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
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

      // load the local GeoPackage
      File geoPackageFile = new File("./samples-data/auroraCO/AuroraCO.gpkg");
      GeoPackage geoPackage = new GeoPackage(geoPackageFile.getAbsolutePath());
      geoPackage.loadAsync();
      geoPackage.addDoneLoadingListener(() -> {
        if (geoPackage.getLoadStatus() == LoadStatus.LOADED) {
          // add raster layers from the rasters in the GeoPackage
          geoPackage.getGeoPackageRasters().forEach(raster -> {
            RasterLayer rasterLayer = new RasterLayer(raster);
            // make the layer semi-transparent to see through it
            rasterLayer.setOpacity(0.5f);

            map.getOperationalLayers().add(rasterLayer);
          });

          // add feature layers from the feature tables in the GeoPackage
          geoPackage.getGeoPackageFeatureTables().forEach(table -> {
            FeatureLayer featureLayer = new FeatureLayer(table);

            map.getOperationalLayers().add(featureLayer);
          });
        } else {
          new Alert(Alert.AlertType.ERROR, "GeoPackage failed to load").show();
        }
      });

      // add the map view to the stack pane
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