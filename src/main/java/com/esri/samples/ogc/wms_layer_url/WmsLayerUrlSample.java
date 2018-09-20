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

package com.esri.samples.ogc.wms_layer_url;

import java.util.Collections;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.WmsLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;

public class WmsLayerUrlSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("WMS Layer URL Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a map and add it to the map view
      ArcGISMap map = new ArcGISMap(Basemap.createLightGrayCanvas());
      mapView = new MapView();
      mapView.setMap(map);

      // start zoomed in over the US
      mapView.setViewpointGeometryAsync(new Envelope(-19195297.778679, 512343.939994, -3620418.579987, 8658913.035426, 0.0, 0.0, SpatialReferences.getWebMercator()));

      // show a progress indicator while the layer loads
      ProgressIndicator progressIndicator = new ProgressIndicator();
      progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
      progressIndicator.setMaxSize(25, 25);

      // create a WMS layer
      List<String> wmsLayerNames = Collections.singletonList("1");
      String url = "https://nowcoast.noaa.gov/arcgis/services/nowcoast/radar_meteo_imagery_nexrad_time/MapServer/WMSServer?request=GetCapabilities&service=WMS";
      WmsLayer wmsLayer = new WmsLayer(url, wmsLayerNames);
      // load the layer and add it as an operational layer
      wmsLayer.addDoneLoadingListener(() -> {
        if (wmsLayer.getLoadStatus() != LoadStatus.LOADED) {
          wmsLayer.getLoadError().printStackTrace();
          new Alert(Alert.AlertType.ERROR, "Failed to load WMS layer").show();
        }
        progressIndicator.setVisible(false);
      });
      map.getOperationalLayers().add(wmsLayer);

      // add the map view to stack pane
      stackPane.getChildren().addAll(mapView, progressIndicator);
      StackPane.setAlignment(progressIndicator, Pos.CENTER);
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
