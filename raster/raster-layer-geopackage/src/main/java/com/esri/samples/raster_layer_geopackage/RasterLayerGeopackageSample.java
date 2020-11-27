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

package com.esri.samples.raster_layer_geopackage;

import java.io.File;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.data.GeoPackage;
import com.esri.arcgisruntime.layers.RasterLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.raster.GeoPackageRaster;

public class RasterLayerGeopackageSample extends Application {

  private MapView mapView;
  private GeoPackage geoPackage; // keep loadable in scope to avoid garbage collection

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Raster Layer GeoPackage");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a map with a light gray basemap style
      ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_LIGHT_GRAY);

      // create a map view and set its map
      mapView = new MapView();
      mapView.setMap(map);

      // create a geopackage from a local gpkg file
      geoPackage = new GeoPackage(new File(System.getProperty("data.dir"), "./samples-data/auroraCO/AuroraCO.gpkg").getAbsolutePath());

      // load the geopackage
      geoPackage.loadAsync();
      geoPackage.addDoneLoadingListener(() -> {
        if (geoPackage.getLoadStatus() == LoadStatus.LOADED && geoPackage.getGeoPackageRasters().size() > 0) {
          // get the geopackage raster
          GeoPackageRaster raster = geoPackage.getGeoPackageRasters().get(0);

          // create a raster layer and add it to the map
          RasterLayer rasterLayer = new RasterLayer(raster);
          rasterLayer.setOpacity(0.7f);
          map.getOperationalLayers().add(rasterLayer);

          // set viewpoint on the raster layer
          rasterLayer.addDoneLoadingListener(() -> {
            if (rasterLayer.getLoadStatus() == LoadStatus.LOADED) {
              mapView.setViewpointGeometryAsync(rasterLayer.getFullExtent(), 150);
            } else {
              Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load raster layer");
              alert.show();
            }
          });

        } else {
          Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load geopackage");
          alert.show();
        }
      });

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
